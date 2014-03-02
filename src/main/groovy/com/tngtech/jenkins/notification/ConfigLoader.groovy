package com.tngtech.jenkins.notification

import groovy.transform.CompileStatic

class ConfigLoader {

    ConfigLoader() {
    }

    public Config load() {
        URL url = Bootstrap.class.getProtectionDomain().getCodeSource().getLocation();
        File appHome = new File(url.toURI()).getParentFile().getParentFile();
        File confFile = new File(new File(appHome, "conf"), "config.groovy");

        URL configUrl

        if (confFile.exists()) {
            configUrl = confFile.toURI().toURL();
        } else {
            configUrl = getClass().getResource('/config.groovy');
        }

        new ConfigSlurper().parse(configUrl);
    }
}
