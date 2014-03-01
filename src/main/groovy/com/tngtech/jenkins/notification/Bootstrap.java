package com.tngtech.jenkins.notification;

import com.tngtech.missile.usb.MissileLauncher;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.joda.time.DateTime;

import java.io.File;

public class Bootstrap {

    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        String rssFeedUrl = System.getProperty("jenkins.url", "http://localhost:8080/job/property-loader/rssFailed");
        if (args.length == 1) {
            String command = args[0];
            if ("fire".equals(command)) {
                MissileLauncher.findMissileLauncher().fire();
            } else if ("ledOn".equals(command)) {
                MissileLauncher.findMissileLauncher().ledOn();
            } else if ("ledOff".equals(command)) {
                MissileLauncher.findMissileLauncher().ledOff();
            } else {
                new CamelApplication(rssFeedUrl).run();
            }
        }
        if (args.length == 2) {
            MissileLauncher.Direction direction =
                    MissileLauncher.Direction.valueOf(args[0].toUpperCase());
            int time = Integer.valueOf(args[1]);
            MissileLauncher.findMissileLauncher().move(direction, time);
        }
    }
}
