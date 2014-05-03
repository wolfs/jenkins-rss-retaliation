package com.tngtech.jenkins.notification

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Config
import com.tngtech.jenkins.notification.model.Project
import com.tngtech.jenkins.notification.model.Result
import org.junit.Test

class ConfigLoaderTest {

    @Test
    void config_is_loaded() {
        Config config = new ConfigLoader().load()
        def someUser = config.missile.locations.someUser
        assertNotNull(someUser)
        assertEquals('The build some is unstable!',
                config.tts.message.call(new BuildInfo(
                        project: new Project(displayName: 'some'),
                        result: Result.UNSTABLE)).toString())
    }
}
