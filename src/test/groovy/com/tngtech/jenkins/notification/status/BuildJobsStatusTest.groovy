package com.tngtech.jenkins.notification.status

import spock.lang.Specification

class BuildJobsStatusTest extends Specification {
    def 'will calculate overall state correctly for SUCCESS'() {
        given:
        def map = [ "a" : BuildStatus.SUCCESS, "b" : BuildStatus.SUCCESS, "c" : BuildStatus.SUCCESS ];
        def buildJobsStatus = new BuildJobsStatus(map);

        when:
        def status = buildJobsStatus.status;

        then:
        status == BuildStatus.SUCCESS
    }

    def 'will calculate overall state correctly for UNSTABLE'() {
        given:
        def map = [ "a" : BuildStatus.SUCCESS, "b" : BuildStatus.UNSTABLE, "c" : BuildStatus.SUCCESS ];
        def buildJobsStatus = new BuildJobsStatus(map);

        when:
        def status = buildJobsStatus.status;

        then:
        status == BuildStatus.UNSTABLE
    }

    def 'will calculate overall state correctly for FAIL'() {
        given:
        def map = [ "a" : BuildStatus.FAIL, "b" : BuildStatus.UNSTABLE, "c" : BuildStatus.SUCCESS ];
        def buildJobsStatus = new BuildJobsStatus(map);

        when:
        def status = buildJobsStatus.status;

        then:
        status == BuildStatus.FAIL
    }
}
