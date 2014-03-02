package com.tngtech.jenkins.notification;

import com.tngtech.missile.usb.MissileLauncher;

import java.util.ArrayList;
import java.util.List;

public class Bootstrap {

    public static void main(String[] args) throws Exception {

        Config config = new ConfigLoader().load();

        String command = args[0];
        if (args.length == 1) {
            if ("fire".equals(command)) {
                MissileLauncher.findMissileLauncher().fire();
            } else if ("ledOn".equals(command)) {
                MissileLauncher.findMissileLauncher().ledOn();
            } else if ("ledOff".equals(command)) {
                MissileLauncher.findMissileLauncher().ledOff();
            } else if ("stalk".equals(command)) {
                new CamelApplication(config).run();
            }
        }
        if (args.length >= 2) {
            if ("shootAt".equals(command)) {
                MissileEndpoint missileEndpoint = new MissileEndpoint(config.getLocations());
                List<String> culprits = new ArrayList<>();
                for (int i = 1; i < args.length; i++) {
                    culprits.add(args[i]);
                }
                missileEndpoint.shootAt(culprits);
            } else {
                MissileLauncher.Direction direction =
                        MissileLauncher.Direction.valueOf(args[0].toUpperCase());
                int time = Integer.valueOf(args[1]);
                MissileLauncher.findMissileLauncher().move(direction, time);
            }
        }
    }
}
