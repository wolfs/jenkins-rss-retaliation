package com.tngtech.jenkins.notification.model

class TtsConfig {
    String lang = "en"
    Closure message = { BuildInfo info ->
        "The build ${info.project.displayName} is broken!"
    }
}
