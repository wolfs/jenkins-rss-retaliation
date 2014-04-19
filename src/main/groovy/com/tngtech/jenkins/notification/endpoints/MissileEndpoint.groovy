package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.MissileConfig
import com.tngtech.missile.usb.MissileController
import com.tngtech.missile.usb.MissileLauncher
import org.apache.camel.Body
import org.apache.camel.Handler

class MissileEndpoint extends BaseEndpoint {

    private MissileConfig config

    MissileEndpoint(MissileConfig config) {
        this.config = config
    }

    @Override
    void process(BuildInfo buildInfo) throws Exception {
        String status = buildInfo.result
        if (status.toUpperCase().startsWith('FAIL')) {
            def culprits = buildInfo.culprits
            shootAt(culprits.collect { it.id })
        }
    }

    public void shootAt(List<String> culprits) {
        def locations = config.locations
        def toShootAt = culprits.collect {
            locations[it]
        }.findAll().toSet()

        if (!toShootAt) {
            toShootAt = [locations.unknown]
        }

        def launcher = MissileLauncher.findMissileLauncher()
        if (launcher == null) {
            throw new RuntimeException("Missile Launcher not connected!")
        } else {
            def controller = new MissileController(launcher)
            toShootAt.each { location ->
                controller.run(location)
            }
        }
    }
}
