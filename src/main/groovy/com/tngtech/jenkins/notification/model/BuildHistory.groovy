package com.tngtech.jenkins.notification.model

import groovy.transform.Immutable

@Immutable(copyWith = true, knownImmutableClasses = [BuildInfo])
class BuildHistory {
    BuildInfo currentBuild
    BuildInfo lastBuild

    BuildHistory nextBuild(BuildInfo nextBuild) {
        this.copyWith(lastBuild: currentBuild, currentBuild: nextBuild)
    }

    Result getCurrentResult() {
        currentBuild.result
    }

    boolean hasResultChanged() {
        currentBuild?.result != lastBuild?.result
    }
}
