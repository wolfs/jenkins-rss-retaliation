package com.tngtech.jenkins.notification.model

import com.tngtech.jenkins.notification.model.Culprit
import groovy.transform.Canonical

@Canonical
class BuildInfo {
    Project project
    String status
    List<Culprit> culprits
}
