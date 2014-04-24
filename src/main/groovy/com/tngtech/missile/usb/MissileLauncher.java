package com.tngtech.missile.usb;

import javax.usb.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MissileLauncher {

    public static final UsbInterfacePolicy FORCE_CLAIM_POLICY = new UsbInterfacePolicy() {
        @Override
        public boolean forceClaim(UsbInterface usbInterface) {
            return true;
        }
    };

    // On Linux we need to claim the usb-device
    public static final boolean NEEDS_CLAIM = "Linux".equalsIgnoreCase(System.getProperty("os.name"));

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
            this.bytes = new byte[]{(byte) type, (byte) command, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        }
    }

    public static final Set<Command> directionCommands = EnumSet.of(
            Command.LEFT,
            Command.RIGHT,
            Command.UP,
            Command.DOWN);

    private final UsbDevice device;

    public static MissileLauncher findMissileLauncher() throws Exception {
        UsbDevice launcher = findDevice();
        MissileLauncher missileLauncher = null;
        if (launcher != null) {
            missileLauncher = new MissileLauncher(launcher);
        }
        return missileLauncher;
    }

    private MissileLauncher(UsbDevice device) {
        this.device = device;
    }

    public void execute(Command command) throws UsbException {
        UsbInterface usbInterface = null;
        if (NEEDS_CLAIM) {
            usbInterface = device.getActiveUsbConfiguration().getUsbInterface((byte) 0);
            usbInterface.claim(FORCE_CLAIM_POLICY);
        }
        try {
            UsbControlIrp irp = device.createUsbControlIrp(
                    (byte) (UsbConst.REQUESTTYPE_TYPE_CLASS | UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE),
                    UsbConst.REQUEST_SET_CONFIGURATION,
                    (short) 0,
                    (short) 0);
            irp.setData(command.bytes);
            device.syncSubmit(irp);
        } finally {
            if (usbInterface != null) {
                usbInterface.release();
            }
        }
    }

    private static UsbDevice findDevice() throws UsbException {
        UsbServices usbServices = UsbHostManager.getUsbServices();
        UsbHub rootHub = usbServices.getRootUsbHub();
        return findDevice(rootHub);
    }

    private static UsbDevice findDevice(UsbDevice device) {
        UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();

        if ((desc.idVendor() & 0xffff) == 0x2123 && (desc.idProduct() & 0xffff) == 0x1010) {
            return device;
        }

        if (device.isUsbHub()) {
            UsbHub hub = (UsbHub) device;
            @SuppressWarnings("unchecked")
            List<UsbDevice> attachedUsbDevices = (List<UsbDevice>) hub.getAttachedUsbDevices();
            for (UsbDevice child : attachedUsbDevices) {
                UsbDevice missileLauncher = findDevice(child);
                if (missileLauncher != null) {
                    return missileLauncher;
                }
            }
        }
        return null;
    }
}
