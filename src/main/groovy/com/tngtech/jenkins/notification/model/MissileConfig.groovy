package com.tngtech.jenkins.notification.model

class MissileConfig {
    Map<String, List<List>> locations
    Set<Result> whenToShoot = [Result.FAILURE, Result.UNSTABLE]

    void setWhenToShoot(List<String> whenToShootStrings) {
        whenToShoot = whenToShootStrings.collect { Result.valueOf(it) } as Set<Result>
    }
}
