package com.tngtech.jenkins.notification;

import com.tngtech.jenkins.notification.endpoints.MissileEndpoint;
import com.tngtech.jenkins.notification.endpoints.TtsEndpoint;
import com.tngtech.jenkins.notification.model.Config;
import com.tngtech.missile.usb.MissileController;
import com.tngtech.missile.usb.MissileLauncher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bootstrap {

    public static void main(String[] args) throws Exception {

        Config config = new ConfigLoader().load();

        String command = args[0];
        if (args.length == 1) {
            if ("fire".equals(command)) {
                getMissileController().fire();
            } else if ("ledOn".equals(command)) {
                getMissileController().ledOn();
            } else if ("ledOff".equals(command)) {
                getMissileController().ledOff();
            } else if ("stalk".equals(command)) {
                new CamelApplication(config).run();
            }
        }
        if (args.length >= 2) {
            if ("shootAt".equals(command)) {
                MissileEndpoint missileEndpoint = new MissileEndpoint(config.getMissile());
                List<String> culprits = new ArrayList<>();
                culprits.addAll(Arrays.asList(args).subList(1, args.length));
                missileEndpoint.shootAt(culprits);
            } else if ("say".equals(command)) {
                TtsEndpoint ttsEndpoint = new TtsEndpoint(config.getTts());
                String text = "";
                for (int i = 1; i < args.length; i++) {
                    text = text + " " + args[i];
                }
                ttsEndpoint.say(text);
            } else {
                MissileLauncher.Command direction =
                        MissileLauncher.Command.valueOf(args[0].toUpperCase());
                int time = Integer.valueOf(args[1]);
                getMissileController().move(direction, time);
            }
        }
    }

    private static MissileController getMissileController() throws Exception {
        return new MissileController(MissileLauncher.findMissileLauncher());
    }
}
