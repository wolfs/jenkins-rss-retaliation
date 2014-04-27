package com.tngtech.jenkins.notification.model

import groovy.transform.Canonical

@Canonical
class BuildInfo {
    String feedMessage
    Project project
    Result result
    String buildNumber
    List<Culprit> culprits


    @Override
    String toString() {
        'BuildInfo{' +
                "culprits=${culprits*.id}}" +
                ", project=${project.name}" +
                ", result=${result}" +
                ", buildNumber='${buildNumber}'" +
                '}'
    }
}
