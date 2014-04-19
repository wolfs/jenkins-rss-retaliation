package com.tngtech.jenkins.notification.status

import static com.tngtech.jenkins.notification.status.BuildStatus.SUCCESS

class BuildJobsStatus {
    private Map<String, BuildStatus> allJobsStatusMap;

    public BuildJobsStatus(Map<String, BuildStatus> allJobsStatusMap) {
        this.allJobsStatusMap = allJobsStatusMap;
    }

    public BuildStatus getStatus() {
        BuildStatus highest = SUCCESS
        for (BuildStatus status : allJobsStatusMap.values()) {
            if (status.ordinal() > highest.ordinal()) {
                highest = status;
            }
        }

        return highest;
    }

    Map<String, BuildStatus> getAllJobsStatusMap() {
        return allJobsStatusMap
    }


    @Override
    public String toString() {
        return "BuildJobsStatus{" +
                "allJobsStatusMap=" + allJobsStatusMap +
                ",overallStatus=" + status +
                '}';
    }
}
