package com.tngtech.missile.usb

class MissileController {

    private MissileLauncher launcher

    public MissileController(MissileLauncher launcher) {
        this.launcher = launcher
    }

    public void run(List<List> commands) {
        commands.each { command ->
            String method = command.head()
            def args = command.tail()
            if (MissileLauncher.Direction.values()*.toString().contains(method.toUpperCase())) {
                launcher.move(MissileLauncher.Direction.valueOf(method.toUpperCase()), args[0])
            } else {
                launcher."$method"(*args)
            }
        }
    }
}
