package com.tngtech.jenkins.notification.camel

import com.tngtech.jenkins.notification.model.BuildHistory
import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Project
import com.tngtech.jenkins.notification.model.Result
import spock.lang.Specification

class AllBuildInfosHolderTest extends Specification {
    def 'will update internal map correctly'() {
        given:
        def holder = new AllBuildInfosHolder()

        when:
        holder.process(buildInfoFor('a', Result.SUCCESS))
        holder.process(buildInfoFor('b', Result.FAILURE))
        holder.process(buildInfoFor('a', Result.FAILURE))

        then:
        holder.allBuildInfos.jobsHistory == [
                a: historyFor('a', Result.SUCCESS, Result.FAILURE),
                b: historyFor('b', Result.FAILURE)]
    }

    private BuildHistory historyFor(String projectName, Result... results) {
        BuildHistory history = new BuildHistory(null, null)
        results.inject(history) {
            currentHistory, result -> currentHistory.nextBuild(buildInfoFor(projectName, result))
        }
    }

    BuildInfo buildInfoFor(String project, Result result) {
        new BuildInfo(null, new Project(project, project), result)
    }
}
