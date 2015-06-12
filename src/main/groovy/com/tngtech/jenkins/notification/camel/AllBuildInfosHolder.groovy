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
    private Map<String, BuildHistory> jobsHistory = [:]

    @Handler
    BuildHistory process(@Body BuildInfo buildInfo) {
        synchronized (jobsHistory) {
            updateJobsHistory(buildInfo)
        }
    }

    AllBuildInfos getAllBuildInfos() {
        synchronized (jobsHistory) {
            new AllBuildInfos(new HashMap<String, BuildHistory>(jobsHistory))
        }
    }

    private BuildHistory updateJobsHistory(BuildInfo buildInfo) {
        BuildHistory updatedHistory = getUpdatedHistoryForBuildInfo(buildInfo)
        jobsHistory.put(getKey(buildInfo), updatedHistory)
        LOG.trace('Updated Jobs History to \n{}', jobsHistory.collect { project, history ->
            "${project}: ${history?.currentBuild?.result}"
        }.join('\n'))
        updatedHistory
    }

    private BuildHistory getUpdatedHistoryForBuildInfo(BuildInfo buildInfo) {
        new BuildHistory(buildInfo, jobsHistory.get(getKey(buildInfo))?.currentBuild)
    }

    private String getKey(BuildInfo buildInfo) {
        buildInfo.project.name
    }
}
