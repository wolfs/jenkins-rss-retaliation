package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.endpoints.TrafficLightEndpoint.Status
import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Project
import spock.lang.Specification

class TrafficLightEndpointTest extends Specification {
    def 'TrafficLight status is FAIL upon receiving a failing build'() {
        given:
        def endpoint = new TrafficLightEndpoint(null)

        when:
        endpoint.process(buildInfoFor("a", Status.FAIL.name()))

        then:
        endpoint.trafficLightStatus == Status.FAIL
    }

    def 'TrafficLight status is FAIL upon receiving a UNSTABLE project while being FAILED'() {
        given:
        def endpoint = new TrafficLightEndpoint(null)
        endpoint.process(buildInfoFor("a", Status.FAIL.name()))

        when:
        endpoint.process(buildInfoFor("b", Status.UNSTABLE.name()))

        then:
        endpoint.trafficLightStatus == Status.FAIL
    }

    def 'TrafficLight status is UNSTABLE upon receiving a UNSTABLE project while being STABLE'() {
        given:
        def endpoint = new TrafficLightEndpoint(null)
        endpoint.process(buildInfoFor("a", Status.SUCCESS.name()))

        when:
        endpoint.process(buildInfoFor("b", Status.UNSTABLE.name()))

        then:
        endpoint.trafficLightStatus == Status.UNSTABLE
    }

    def 'TrafficLight status is UNSTABLE upon receiving a UNSTABLE project while being FAILED'() {
        given:
        def endpoint = new TrafficLightEndpoint(null)
        endpoint.process(buildInfoFor("a", Status.FAIL.name()))

        when:
        endpoint.process(buildInfoFor("a", Status.UNSTABLE.name()))

        then:
        endpoint.trafficLightStatus == Status.UNSTABLE
    }

    def BuildInfo buildInfoFor(String project, String status) {
        return new BuildInfo(null, new Project(project, project), status);
    }
}
