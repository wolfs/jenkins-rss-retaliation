package com.tngtech.jenkins.notification.camel

import com.tngtech.jenkins.notification.model.BuildHistory
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
        holder.process(buildInfoFor("a", Result.FAILURE))

        then:
        holder.allBuildInfos.jobsHistory == [ a : historyFor('a', Result.SUCCESS, Result.FAILURE), b: historyFor('b', Result.FAILURE)]
    }

    private BuildHistory historyFor(String projectName, Result... results) {
        BuildHistory history = new BuildHistory()
        results.each { result -> history.nextBuild(buildInfoFor(projectName, result)) }

        history
    }

    def BuildInfo buildInfoFor(String project, Result result) {
        return new BuildInfo(null, new Project(project, project), result);
    }
}
