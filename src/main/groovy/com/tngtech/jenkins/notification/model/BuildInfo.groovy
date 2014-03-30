package com.tngtech.jenkins.notification.model

import groovy.transform.Canonical

@Canonical
class BuildInfo {
    String feedMessage
    Project project
    String status
    String buildNumber
    List<Culprit> culprits
    boolean isInitialParsing
}
