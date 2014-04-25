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
    public String toString() {
        return "BuildInfo{" +
                "culprits=" + culprits.collect { it.id } +
                ", project=" + project.name +
                ", result=" + result +
                ", buildNumber='" + buildNumber + '\'' +
                '}';
    }
}
