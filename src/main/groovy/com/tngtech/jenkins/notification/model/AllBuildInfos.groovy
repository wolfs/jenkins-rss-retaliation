package com.tngtech.jenkins.notification.model

import static com.tngtech.jenkins.notification.model.Result.ABORTED
import static com.tngtech.jenkins.notification.model.Result.NOT_BUILT
import static com.tngtech.jenkins.notification.model.Result.SUCCESS

class AllBuildInfos {
    private Map<String, Result> allJobsResults;
    private Set<Result> resultsToIgnore = EnumSet.of(ABORTED, NOT_BUILT)

    public AllBuildInfos(Map<String, Result> allJobsResults) {
        this.allJobsResults = allJobsResults;
    }

    public Result getOverallResult() {
        Result highest = SUCCESS
        for (Result status : allJobsResults.values()) {
            if ((status.ordinal > highest.ordinal) && (!resultsToIgnore.contains(status))) {
                highest = status;
            }
        }

        return highest;
    }

    @Override
    public String toString() {
        return "AllBuildInfos{" +
                "allJobsResults=" + allJobsResults +
                ",overallResult=" + overallResult +
                '}';
    }
}
