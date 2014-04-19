package com.tngtech.jenkins.notification.model

import spock.lang.Specification
import spock.lang.Unroll

import static com.tngtech.jenkins.notification.model.Result.ABORTED
import static com.tngtech.jenkins.notification.model.Result.FAILURE
import static com.tngtech.jenkins.notification.model.Result.NOT_BUILT
import static com.tngtech.jenkins.notification.model.Result.SUCCESS
import static com.tngtech.jenkins.notification.model.Result.UNSTABLE

class AllBuildInfosTest extends Specification {
    @Unroll
    def 'Overall result for #results is #overallResult'(List<Result> results, Result overallResult) {
        given:
        int count = 0
        def map = results.collectEntries { ["project${count++}", it] }
        def buildJobsStatus = new AllBuildInfos(map);

        when:
        def status = buildJobsStatus.overallResult;

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
