package com.tngtech.jenkins.notification;

import com.tngtech.jenkins.notification.model.BuildInfo;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.camel.Body;
import org.apache.camel.Handler;

import java.util.Date;

public class EntryToBuildInfo {
    private BuildInfoViaRestProvider provider;
    private Date startDate = new Date();

    public EntryToBuildInfo(BuildInfoViaRestProvider provider) {
        this.provider = provider;
    }

    @Handler
    public BuildInfo process(@Body Entry entry) throws Exception {
        IRI link = entry.getAlternateLink().getHref();
        BuildInfo buildInfo = provider.getBuildInfo(link);
        buildInfo.setFeedMessage(entry.getTitle());
        buildInfo.setIsInitialParsing(entry.getUpdated().before(startDate));
        return buildInfo;
    }
}
