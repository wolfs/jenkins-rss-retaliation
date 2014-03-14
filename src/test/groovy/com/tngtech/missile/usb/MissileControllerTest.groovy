package com.tngtech.missile.usb

import com.tngtech.jenkins.notification.model.Config
import com.tngtech.jenkins.notification.ConfigLoader
import spock.lang.Ignore
import spock.lang.Specification

class MissileControllerTest extends Specification {


    def 'Commands are run from List'() {
        setup:
        def launcher = Mock(MissileLauncher)
        def controller = new MissileController(launcher)

        when:
        controller.run([
                ['left', 1],
                ['right', 1],
                ['up', 1],
                ['down', 1],
                ['fire'],
                ['ledOn'],
                ['ledOff'],
                ['zero']
        ])

        then:
        1 * launcher.execute(MissileLauncher.Command.LEFT)
        then:
        1 * launcher.execute(MissileLauncher.Command.RIGHT)
        then:
        1 * launcher.execute(MissileLauncher.Command.UP)
        then:
        1 * launcher.execute(MissileLauncher.Command.DOWN)
        then:
        1 * launcher.execute(MissileLauncher.Command.FIRE)
        then:
        1 * launcher.execute(MissileLauncher.Command.LED_ON)
        then:
        1 * launcher.execute(MissileLauncher.Command.LED_OFF)
        then:
        1 * launcher.execute(MissileLauncher.Command.LEFT)
        1 * launcher.execute(MissileLauncher.Command.DOWN)

    }

    @Ignore("Needs a real launcher")
    def commands_are_ran() {
        when:
        def controller = createController()
        controller.run([
                ['zero'],
                ['right', 2000],
                ['up', 300],
                ['fire'],
                ['right', 1000],
                ['fire']
        ])
        then:
        println 'It ran!'
    }

    void commands_can_be_run_from_config() {
        Config config = new ConfigLoader().load()
        def controller = createController()
        controller.run(config.locations.'stefan.wolf')

    }

    private createController() {
        new MissileController(MissileLauncher.findMissileLauncher())
    }
}
