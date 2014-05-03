package com.tngtech.jenkins.notification.model

import static com.tngtech.jenkins.notification.model.Result.ABORTED
import static com.tngtech.jenkins.notification.model.Result.NOT_BUILT
import static com.tngtech.jenkins.notification.model.Result.SUCCESS

import org.apache.commons.collections.map.UnmodifiableMap

class AllBuildInfos {
    private Map<String, BuildHistory> jobsHistory
    private Set<Result> resultsToIgnore = EnumSet.of(ABORTED, NOT_BUILT)

    AllBuildInfos(Map<String, BuildHistory> jobsHistory) {
        this.jobsHistory = jobsHistory
    }

    Result getOverallResult() {
        Result highest = SUCCESS
        for (BuildHistory history : jobsHistory.values()) {
            Result currentResult = history.currentResult
            if ((currentResult.ordinal > highest.ordinal) && (!resultsToIgnore.contains(currentResult))) {
                highest = currentResult
            }
        }

        highest
    }

    @Override
    String toString() {
        "AllBuildInfos{jobsHistory=${jobsHistory}, overallResult=${overallResult}}"
    }

    Map<String, BuildHistory> getJobsHistory() {
        UnmodifiableMap.decorate(jobsHistory)
    }
}
