package com.tngtech.jenkins.notification

import com.tngtech.missile.usb.MissileController
import com.tngtech.missile.usb.MissileLauncher
import groovy.json.JsonSlurper
import org.apache.camel.Exchange
import org.apache.camel.Processor

class MissileEndpoint implements Processor {

    private Map<String, List<List>> locations

    MissileEndpoint(Map<String, List<List>> locations) {
        this.locations = locations
    }

    @Override
    void process(Exchange exchange) throws Exception {
        def json = new JsonSlurper().parseText(exchange.getIn().getBody(String.class))
        String status = json.build.status
        if (status.toUpperCase().startsWith('FAIL')) {
            def culprits = json.build.culprits
            shootAt(culprits)
        }
    }

    public void shootAt(List<String> culprits) {
        def toShootAt = culprits.collect {
            locations[it]
        }.findAll().toSet()

        if (!toShootAt) {
            toShootAt = [locations.unknown]
        }

        def launcher = MissileLauncher.findMissileLauncher()
        if (launcher == null) {
            throw new RuntimeException("Missile Launcher not connected!")
        }
        def controller = new MissileController(launcher)
        toShootAt.each { location ->
            controller.run(location)
        }
    }
}
