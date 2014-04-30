package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildHistory
import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.MissileConfig
import com.tngtech.missile.usb.MissileController
import com.tngtech.missile.usb.MissileLauncher

class MissileEndpoint extends BaseEndpoint {

    private MissileConfig config


    MissileEndpoint(MissileConfig config) {
        this.config = config
    }

    @Override
    void process(BuildHistory buildHistory) throws Exception {
        BuildInfo buildInfo = buildHistory.currentBuild
        if (buildHistory.hasResultChanged() &&
                config.whenToShoot.contains(buildInfo.result)) {
            def culprits = buildInfo.culprits
            shootAt(culprits*.id)
        }
    }

    void shootAt(List<String> culprits) {
        def locations = config.locations
        def toShootAt = culprits.collect {
            locations[it]
        }.findAll().toSet()

        if (!toShootAt) {
            toShootAt = [locations?.unknown].findAll()
        }

        def launcher = MissileLauncher.findMissileLauncher()
        if (launcher == null) {
            throw new IllegalStateException('Missile Launcher not connected!')
        } else {
            def controller = new MissileController(launcher)
            toShootAt.each { location ->
                controller.run(location)
            }
        }
    }
}
