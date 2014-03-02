package com.tngtech.missile.usb

import com.tngtech.jenkins.notification.Config
import com.tngtech.jenkins.notification.ConfigLoader
import org.junit.Test

class MissileControllerTest {

    @Test
    void commands_are_ran() {
        def controller = createController()
        controller.run([
                ["zero"],
                ["right", 3000],
                ["up", 600],
                ["fire"]
        ])
    }

    @Test
    void commands_can_be_run_from_config() {
        Config config = new ConfigLoader().load()
        def controller = createController()
        controller.run(config.locations.'stefan.wolf')

    }

    private createController() {
        new MissileController(MissileLauncher.findMissileLauncher())
    }
}
