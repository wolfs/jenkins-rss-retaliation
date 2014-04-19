package com.tngtech.jenkins.notification.camel

import com.tngtech.jenkins.notification.camel.AllBuildInfosHolder
import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Project
import com.tngtech.jenkins.notification.model.Result
import spock.lang.Specification

class AllBuildInfosHolderTest extends Specification {
    def 'will update internal map correctly'() {
        given:
        def holder = new AllBuildInfosHolder();

        when:
        holder.process(buildInfoFor("a", Result.SUCCESS))
        holder.process(buildInfoFor("b", Result.FAILURE))

        then:
        holder.buildJobsStatus.allJobsResults == [ "a" : Result.SUCCESS, "b" : Result.FAILURE ]
    }

    def BuildInfo buildInfoFor(String project, Result result) {
        return new BuildInfo(null, new Project(project, project), result);
    }
}
