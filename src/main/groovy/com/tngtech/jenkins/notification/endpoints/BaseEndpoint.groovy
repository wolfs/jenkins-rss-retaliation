package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo

abstract class BaseEndpoint implements FeedbackEndpoint {

    @Override
    void process(BuildInfo buildInfo) throws Exception {
        if (buildInfo.isInitialParsing) {
            processInitial(buildInfo)
        } else {
            processUpdate(buildInfo)
        }
    }

    abstract void processUpdate(BuildInfo buildInfo) throws Exception;
    void processInitial(BuildInfo buildInfo) throws  Exception {};
}
