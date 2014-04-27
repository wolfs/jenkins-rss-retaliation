package com.tngtech.jenkins.notification

import com.tngtech.jenkins.notification.model.Config

class ConfigLoader {

    Config load() {
        URL url = Bootstrap.protectionDomain.codeSource.location
        File appHome = new File(url.toURI()).parentFile.parentFile
        File confFile = new File(new File(appHome, 'conf'), 'config.groovy')

        URL configUrl

        if (System.properties.containsKey('config')) {
            configUrl = new File(System.getProperty('config')).toURI().toURL()
        } else if (confFile.exists()) {
            configUrl = confFile.toURI().toURL()
        } else {
            configUrl = getClass().getResource('/config.groovy')
        }

        new ConfigSlurper().parse(configUrl)
    }
}
