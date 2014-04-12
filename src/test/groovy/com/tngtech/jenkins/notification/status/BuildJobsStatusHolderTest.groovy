package com.tngtech.jenkins.notification.status

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Project
import spock.lang.Specification

class BuildJobsStatusHolderTest extends Specification {
    def 'will update internal map correctly'() {
        given:
        def holder = new BuildJobsStatusHolder();

        when:
        holder.process(buildInfoFor("a", BuildStatus.SUCCESS.name()))
        def result = holder.process(buildInfoFor("b", BuildStatus.FAIL.name()))

        then:
        result.overallJobsStatus.allJobsStatusMap == [ "a" : BuildStatus.SUCCESS, "b" : BuildStatus.FAIL ]
    }

    def BuildInfo buildInfoFor(String project, String status) {
        return new BuildInfo(null, new Project(project, project), status);
    }
}
