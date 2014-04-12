package com.tngtech.jenkins.notification.status

import com.tngtech.jenkins.notification.model.BuildInfo
import org.apache.camel.Body
import org.apache.camel.Handler

class BuildJobsStatusHolder {
    private Map<String, BuildStatus> buildJobStatus = new HashMap<>()

    @Handler
    BuildInfo process(@Body BuildInfo buildInfo) throws Exception {
        updateStatusMapWith(buildInfo)

        buildInfo.setOverallJobsStatus(new BuildJobsStatus(new HashMap<String, BuildStatus>(buildJobStatus)));

        return buildInfo;
    }

    private void updateStatusMapWith(BuildInfo buildInfo) {
        for (BuildStatus buildStatus : BuildStatus.values()) {
            if (buildInfo.status.startsWith(buildStatus.name())) {
                buildJobStatus.put(buildInfo.project.name, buildStatus);
            }
        }
    }
}
