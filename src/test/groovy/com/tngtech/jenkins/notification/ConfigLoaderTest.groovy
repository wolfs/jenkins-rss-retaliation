package com.tngtech.jenkins.notification

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Config
import com.tngtech.jenkins.notification.model.Project
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class ConfigLoaderTest {

    @Test
    void config_is_loaded() {
        Config config = new ConfigLoader().load();
        def someUser = config.missile.locations.someUser
        assertNotNull(someUser)
        assertEquals('The build some is broken!', config.tts.message.call(new BuildInfo(project: new Project(displayName: 'some'))).toString())
    }
}
