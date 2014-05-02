package com.tngtech.missile.usb;

import org.junit.Ignore;
import org.junit.Test;

import javax.usb.*;
import java.util.List;

public class UsbTryout {

    @Test
    @Ignore
    public void findDevices() throws UsbException {
        UsbServices usbServices = UsbHostManager.getUsbServices();
        UsbHub rootHub = usbServices.getRootUsbHub();
        dump(rootHub);
    }

    @Test
    @Ignore
    public void missileLauncher_can_be_controlled_is_found() throws Exception {
        MissileController missileLauncher = new MissileController(MissileLauncher.findMissileLauncher());

        missileLauncher.ledOn();
        for (MissileLauncher.Command direction : MissileLauncher.directionCommands) {
            missileLauncher.move(direction, 500);
        }
        missileLauncher.fire();
        missileLauncher.ledOff();

    }

    private static void dump(UsbDevice device)
    {
        UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
        System.out.format("%04x:%04x%n", desc.idVendor() & 0xffff, desc.idProduct() & 0xffff);
        if (device.isUsbHub())
        {
            UsbHub hub = (UsbHub) device;
            @SuppressWarnings("unchecked")
            List<UsbDevice> attachedUsbDevices = (List<UsbDevice>) hub.getAttachedUsbDevices();
            for (UsbDevice child : attachedUsbDevices)
            {
                dump(child);
            }
        }
    }
}
