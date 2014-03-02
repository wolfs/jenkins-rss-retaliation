package com.tngtech.missile.usb;

import org.junit.Test;

import javax.usb.*;
import java.util.List;

public class UsbTryout {

    @Test
    public void findDevices() throws UsbException {
        UsbServices usbServices = UsbHostManager.getUsbServices();
        UsbHub rootHub = usbServices.getRootUsbHub();
        dump(rootHub);
    }

    @Test
//    @Ignore
    public void missileLauncher_can_be_controlled_is_found() throws Exception {
        MissileLauncher missileLauncher = MissileLauncher.findMissileLauncher();

        missileLauncher.ledOn();
        for (MissileLauncher.Direction direction : MissileLauncher.Direction.values()) {
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
            for (UsbDevice child : (List<UsbDevice>) hub.getAttachedUsbDevices())
            {
                dump(child);
            }
        }
    }
}
