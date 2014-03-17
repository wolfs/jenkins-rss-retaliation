package com.tngtech.jenkins.notification.model

class Config {
    String rssFeedUrl
<<<<<<< HEAD
    Map<String, List<List>> locations
    long defaultDelay
    List<String> endpoints
    String clewareUsbSwitchBinary
    List<String> projectNameFilter
=======
    long pollInterval = 60000
    List<String> feedbackDevices
    boolean feedbackInParallel = false
>>>>>>> 139e0f80414bbffc5937409d885b7e31db71bad7

    TtsConfig tts = new TtsConfig()
    MissileConfig missile = new MissileConfig()
}
