package com.tngtech.jenkins.notification;

import com.tngtech.jenkins.notification.camel.CamelApplication;
import com.tngtech.jenkins.notification.endpoints.MissileEndpoint;
import com.tngtech.jenkins.notification.endpoints.TtsEndpoint;
import com.tngtech.jenkins.notification.model.Config;
import com.tngtech.missile.usb.MissileController;
import com.tngtech.missile.usb.MissileLauncher;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bootstrap {

    public static void main(String[] args) throws Exception {

        Config config = new ConfigLoader().load();

        String command = args[0];
        if (args.length == 1) {
            switch (command) {
                case "fire":
                    getMissileController().fire();
                    break;
                case "ledOn":
                    getMissileController().ledOn();
                    break;
                case "ledOff":
                    getMissileController().ledOff();
                    break;
                case "stalk":
                    new CamelApplication(config).run();
                    break;
                default:
                    throw new IllegalArgumentException("Could not parse commandline");
            }
        }
        if (args.length >= 2) {
            switch (command) {
                case "shootAt":
                    MissileEndpoint missileEndpoint = new MissileEndpoint(config.getMissile());
                    List<String> culprits = new ArrayList<>();
                    culprits.addAll(Arrays.asList(args).subList(1, args.length));
                    missileEndpoint.shootAt(culprits);
                    break;
                case "say":
                    TtsEndpoint ttsEndpoint = new TtsEndpoint(config.getTts());
                    List<String> words = new ArrayList<>(Arrays.asList(args));
                    words.remove(0);
                    ttsEndpoint.say(words);
                    break;
                default:
                    MissileLauncher.Command direction =
                            MissileLauncher.Command.valueOf(args[0].toUpperCase());
                    int time = Integer.parseInt(args[1]);
                    getMissileController().move(direction, time);
                    break;
            }
        }
    }

    private static MissileController getMissileController() throws Exception {
        return new MissileController(MissileLauncher.findMissileLauncher());
    }
}
