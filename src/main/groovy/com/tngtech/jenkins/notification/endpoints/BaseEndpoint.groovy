package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.camel.AllBuildInfosHolder

abstract class BaseEndpoint implements FeedbackEndpoint {

    protected AllBuildInfosHolder allBuildInfosHolder

    @Override
    void init(AllBuildInfosHolder allBuildInfosHolder) {
        this.allBuildInfosHolder = allBuildInfosHolder
    }
}
