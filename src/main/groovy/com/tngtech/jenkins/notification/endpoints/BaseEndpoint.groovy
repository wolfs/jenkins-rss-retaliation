package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo
import org.apache.camel.Body

abstract class BaseEndpoint implements FeedbackEndpoint {

    @Override
    void process(@Body BuildInfo buildInfo) throws Exception {
        if (buildInfo.isInitialParsing) {
            processInitial(buildInfo)
        } else {
            processUpdate(buildInfo)
        }
    }

    abstract void processUpdate(BuildInfo buildInfo) throws Exception;
    void processInitial(BuildInfo buildInfo) throws  Exception {};
}
