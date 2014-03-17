package com.tngtech.jenkins.notification.model

class Config {
    String rssFeedUrl
    Map<String, List<List>> locations
    long defaultDelay
    List<String> endpoints
    String clewareUsbSwitchBinary
    List<String> projectNameFilter

    TtsConfig tts = new TtsConfig()
}
