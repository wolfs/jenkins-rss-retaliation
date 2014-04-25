package com.tngtech.jenkins.notification.camel

import com.tngtech.jenkins.notification.model.BuildHistory
import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.AllBuildInfos
import org.apache.camel.Body
import org.apache.camel.Handler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AllBuildInfosHolder {
    private static final Logger LOG = LoggerFactory.getLogger(AllBuildInfosHolder)
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
        LOG.info('Updated Jobs History to \n{}', jobsHistory.collect { proj, hist ->
            "${proj}: ${hist?.currentBuild?.result}"
        }.join('\n'))
    }

    private BuildHistory getHistoryForBuildInfo(BuildInfo buildInfo) {
        jobsHistory.get(getKey(buildInfo)) ?: new BuildHistory()
    }

    private String getKey(BuildInfo buildInfo) {
        buildInfo.project.name
    }
}
