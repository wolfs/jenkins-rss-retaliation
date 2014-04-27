package com.tngtech.missile.usb

import com.tngtech.missile.usb.MissileLauncher.Command

class MissileController {

    private MissileLauncher launcher

    MissileController(MissileLauncher launcher) {
        this.launcher = launcher
    }

    void run(List<List> commands) {
        commands.each { command ->
            String method = command.head()
            def args = command.tail()
            if (MissileLauncher.directionCommands*.toString().contains(method.toUpperCase())) {
                this.move(Command.valueOf(method.toUpperCase()), Long.valueOf(args[0]))
            } else {
                this."$method"(*args)
            }
        }
    }

    void move(Command directionCommand, long millis) throws Exception {
        launcher.execute(directionCommand)
        Thread.sleep(millis)
        launcher.execute(Command.STOP)
    }

    void fire() throws Exception {
        launcher.execute(Command.FIRE)
        Thread.sleep(4500)
    }

    void ledOn() throws Exception {
        launcher.execute(Command.LED_ON)
    }

    void ledOff() throws Exception {
        launcher.execute(Command.LED_OFF)
    }

    void zero() throws Exception {
        move(Command.LEFT, 6000)
        move(Command.DOWN, 1500)
    }

}
