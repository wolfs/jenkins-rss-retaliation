package com.tngtech.jenkins.notification.camel;

import com.tngtech.jenkins.notification.BuildInfoViaRestProvider;
import com.tngtech.jenkins.notification.model.BuildInfo;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.camel.Body;
import org.apache.camel.Handler;

public class EntryToBuildInfo {
    private BuildInfoViaRestProvider provider;

    public EntryToBuildInfo(BuildInfoViaRestProvider provider) {
        this.provider = provider;
    }

    @Handler
    public BuildInfo process(@Body Entry entry) {
        IRI link = entry.getAlternateLink().getHref();
        BuildInfo buildInfo = provider.getBuildInfo(link);
        buildInfo.setFeedMessage(entry.getTitle());
        return buildInfo;
    }
}
