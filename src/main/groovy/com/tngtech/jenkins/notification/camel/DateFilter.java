package com.tngtech.jenkins.notification.camel;

import org.apache.abdera.model.Entry;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DateFilter {
    public static final Logger LOG = LoggerFactory.getLogger(DateFilter.class);

    private Date referenceDate;

    public DateFilter(Date referenceDate) {
        this.referenceDate = (Date) referenceDate.clone();
    }

    @Handler
    public boolean isValid(Entry entry) {
        Date updated = entry.getUpdated();
        if (updated == null) {
            // never been updated so get published date
            updated = entry.getPublished();
        }
        return (updated == null || !referenceDate.after(updated));
    }
}
