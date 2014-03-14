package com.tngtech.jenkins.notification

import com.tngtech.jenkins.notification.model.Config
import org.junit.Test

import static org.junit.Assert.assertNotNull

class ConfigLoaderTest {

    @Test
    void config_is_loaded() {
        Config config = new ConfigLoader().load();
        def stefan = config.locations.stefan
        assertNotNull(stefan)

        println config
        println config.locations
    }
}
