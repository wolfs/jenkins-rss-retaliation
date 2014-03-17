package com.tngtech.jenkins.notification.model

import groovy.transform.Canonical

@Canonical
class BuildInfo {
    Project project
    String status
    List<Culprit> culprits
}
