package com.tngtech.jenkins.notification.model

import static com.tngtech.jenkins.notification.model.Result.*

import spock.lang.Specification
import spock.lang.Unroll

class AllBuildInfosTest extends Specification {
    @Unroll
    def 'Overall result for #results is #overallResult'(List<Result> results, Result overallResult) {
        given:
        int count = 0
        def map = results.collectEntries {
            ["project${count++}", new BuildHistory([:]).nextBuild(new BuildInfo(result: it))]
        }
        def buildJobsStatus = new AllBuildInfos(map)

        when:
        def status = buildJobsStatus.overallResult

        then:
        status == overallResult

        where:
        results                      || overallResult
        [SUCCESS, SUCCESS, SUCCESS]   | SUCCESS
        [UNSTABLE, UNSTABLE, SUCCESS] | UNSTABLE
        [SUCCESS, UNSTABLE, FAILURE]  | FAILURE
        [SUCCESS, ABORTED, UNSTABLE]  | UNSTABLE
        [SUCCESS, NOT_BUILT, SUCCESS] | SUCCESS
    }
}
