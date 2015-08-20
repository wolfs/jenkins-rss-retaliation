package com.tngtech.jenkins.notification.model

class TtsConfig {
    String lang = 'en'
    Closure message = { BuildInfo info ->
        ['The build',  info.project.displayName, 'is',  info.result.toString().toLowerCase()]
    }
    String voiceDir = 'voice'
    final File appHome

    TtsConfig() {
        URL url = TtsConfig.protectionDomain.codeSource.location
        appHome = new File(url.toURI()).parentFile.parentFile

    }

    File getVoiceDir() {
        new File(appHome, voiceDir)
    }
}
