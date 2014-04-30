package com.tngtech.jenkins.notification.endpoints;

import com.tngtech.jenkins.notification.camel.AllBuildInfosHolder;
import com.tngtech.jenkins.notification.model.BuildHistory;
import org.apache.camel.Body;
import org.apache.camel.Handler;

public interface FeedbackEndpoint {
    @Handler
    void process(@Body BuildHistory buildHistory) throws Exception;

    void init(AllBuildInfosHolder allBuildInfosHolder);
}
