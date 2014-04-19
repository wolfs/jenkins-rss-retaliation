package com.tngtech.jenkins.notification.camel

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.AllBuildInfos
import com.tngtech.jenkins.notification.model.Result
import org.apache.camel.Body
import org.apache.camel.Handler

class AllBuildInfosHolder {
    private Map<String, Result> job2Result = new HashMap<>()

    @Handler
    void process(@Body BuildInfo buildInfo) {
        updateStatusMapWith(buildInfo)
    }

    public AllBuildInfos getBuildJobsStatus() {
        return new AllBuildInfos(new HashMap<String, Result>(job2Result));
    }

    private void updateStatusMapWith(BuildInfo buildInfo) {
        job2Result.put(buildInfo.project.name, buildInfo.result);
    }
}
