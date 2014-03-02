package com.tngtech.missile.usb;

import javax.usb.*;
import java.util.List;

public class MissileLauncher {

    public enum Command {
        LEFT(0x04),
        RIGHT(0x08),
        UP(0x02),
        DOWN(0x01),
        FIRE(0x10),
        STOP(0x20),
        LED_ON(0x03, 0x01),
        LED_OFF(0x03, 0x00);

        public final byte[] bytes;

        private Command(int command) {
            this(0x02, command);
        }

        private Command(int type, int command) {
            this.bytes = new byte[] { (byte) type, (byte) command, 0x00,0x00,0x00,0x00,0x00,0x00 };
        }
    }

    public enum Direction {
        LEFT(Command.LEFT),
        RIGHT(Command.RIGHT),
        UP(Command.UP),
        DOWN(Command.DOWN);

        public final Command command;

        private Direction(Command command) {
            this.command = command;
        }
    }

    private final UsbDevice device;

    public static MissileLauncher findMissileLauncher() throws Exception {
        UsbDevice launcher = findDevice();
        MissileLauncher missileLauncher = null;
        if (launcher != null) {
            missileLauncher = new MissileLauncher(launcher);
        }
        return missileLauncher;
    }

    public MissileLauncher(UsbDevice device) {
        this.device = device;
    }

    private void execute(Command command) throws UsbException {
        UsbControlIrp irp = device.createUsbControlIrp(
                (byte) (UsbConst.REQUESTTYPE_TYPE_CLASS | UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE),
                UsbConst.REQUEST_SET_CONFIGURATION,
                (short) 0,
                (short) 0);
        irp.setData(command.bytes);
        device.syncSubmit(irp);

    }

    public void move(Direction direction, long millis) throws Exception {
        execute(direction.command);
        Thread.sleep(millis);
        execute(Command.STOP);
    }

    public void fire() throws Exception {
        execute(Command.FIRE);
        Thread.sleep(4500);
    }

    public void ledOn() throws Exception {
        execute(Command.LED_ON);
    }

    public void ledOff() throws Exception {
        execute(Command.LED_OFF);
    }

    public void zero() throws Exception {
        move(Direction.LEFT, 6000);
        move(Direction.DOWN, 1500);
    }

    private static UsbDevice findDevice() throws UsbException {
        UsbServices usbServices = UsbHostManager.getUsbServices();
        UsbHub rootHub = usbServices.getRootUsbHub();
        return findDevice(rootHub);
    }

    private static UsbDevice findDevice(UsbDevice device)
    {
        UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();

        if ((desc.idVendor() & 0xffff) == 0x2123 && (desc.idProduct() & 0xffff) == 0x1010) {
            return device;
        }

        if (device.isUsbHub())
        {
            UsbHub hub = (UsbHub) device;
            for (UsbDevice child : (List<UsbDevice>) hub.getAttachedUsbDevices())
            {
                UsbDevice missileLauncher = findDevice(child);
                if (missileLauncher != null) {
                    return missileLauncher;
                }
            }
        }
        return null;
    }
}
