package com.tngtech.jenkins.notification.camel

import com.tngtech.jenkins.notification.model.BuildHistory
import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.AllBuildInfos
import org.apache.camel.Body
import org.apache.camel.Handler

class AllBuildInfosHolder {
    private Map<String, BuildHistory> jobsHistory = new HashMap<>()

    @Handler
    void process(@Body BuildInfo buildInfo) {
        updateJobsHistory(buildInfo)
    }

    AllBuildInfos getAllBuildInfos() {
        return new AllBuildInfos(new HashMap<String, BuildHistory>(jobsHistory));
    }

    boolean hasResultChanged(BuildInfo buildInfo) {
        getHistoryForBuildInfo(buildInfo).hasResultChanged()
    }

    private void updateJobsHistory(BuildInfo buildInfo) {
        BuildHistory history = getHistoryForBuildInfo(buildInfo)
        jobsHistory.put(getKey(buildInfo), history.nextBuild(buildInfo))
    }

    private BuildHistory getHistoryForBuildInfo(BuildInfo buildInfo) {
        jobsHistory.get(getKey(buildInfo)) ?: new BuildHistory()
    }

    private String getKey(BuildInfo buildInfo) {
        buildInfo.project.name
    }
}
