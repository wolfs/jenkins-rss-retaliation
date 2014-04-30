package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.*
import spock.lang.Ignore
import spock.lang.Specification

class TtsEndpointTest extends Specification {

    @Ignore
    def 'Output is read from configuration'() {
        when:
        def tts = new TtsEndpoint(new TtsConfig())
        tts.process(new BuildHistory(new BuildInfo(project: new Project(displayName: 'some-project'), result: Result.SUCCESS), null))

        then:
        println 'You heard a sound'
    }
}
