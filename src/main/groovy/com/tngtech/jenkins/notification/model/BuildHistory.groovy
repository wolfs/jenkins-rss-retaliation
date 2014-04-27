package com.tngtech.jenkins.notification.model

import groovy.transform.Canonical

@Canonical
class BuildHistory {
    BuildInfo currentBuild
    BuildInfo lastBuild

    BuildHistory nextBuild(BuildInfo nextBuild) {
        lastBuild = currentBuild
        currentBuild = nextBuild
        this
    }

    Result getCurrentResult() {
        currentBuild.result
    }

    boolean hasResultChanged() {
        currentBuild?.result != lastBuild?.result
    }
}
