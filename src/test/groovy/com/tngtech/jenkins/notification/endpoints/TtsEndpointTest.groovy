package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Config
import com.tngtech.jenkins.notification.model.Project
import com.tngtech.jenkins.notification.model.TtsConfig
import spock.lang.Ignore
import spock.lang.Specification

class TtsEndpointTest extends Specification {

    @Ignore
    def 'Output is read from configuration'() {
        when:
        def tts = new TtsEndpoint(new TtsConfig())
        tts.process(new BuildInfo(project: new Project(displayName: 'some-project')))

        then:
        println 'You heard a sound'
    }
}
