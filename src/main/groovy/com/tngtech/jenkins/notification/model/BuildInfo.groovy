package com.tngtech.jenkins.notification.model

import com.tngtech.jenkins.notification.status.BuildJobsStatus
import groovy.transform.Canonical

@Canonical
class BuildInfo {
    String feedMessage
    Project project
    String status
    String buildNumber
    List<Culprit> culprits
    boolean isInitialParsing
    BuildJobsStatus overallJobsStatus;
}
