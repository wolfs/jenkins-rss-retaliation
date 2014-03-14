package com.tngtech.jenkins.notification.model

class Config {
    String rssFeedUrl
    Map<String, List<List>> locations
    long defaultDelay
    List<String> endpoints

    TtsConfig tts = new TtsConfig()
}
