package com.tngtech.jenkins.notification.model

class Config {
    String rssFeedUrl
    long pollInterval = 60000
    List<String> feedbackDevices
    boolean feedbackInParallel = false

    TtsConfig tts = new TtsConfig()
    MissileConfig missile = new MissileConfig()
    TrafficLightConfig trafficLight = new TrafficLightConfig()

    List<String> getEndpoints() {
        feedbackDevices.collect { "${it}Endpoint".toString() }
    }
}
