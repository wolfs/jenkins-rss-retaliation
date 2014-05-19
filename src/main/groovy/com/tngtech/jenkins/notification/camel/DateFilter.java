package com.tngtech.jenkins.notification.camel;

import org.apache.abdera.model.Entry;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DateFilter {
    public static final Logger LOG = LoggerFactory.getLogger(DateFilter.class);
    public static final long MILLIS_IN_HOUR = 60 * 60 * 1000;

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

        if (updated == null) {
            return true;
        }

        boolean valid = !referenceDate.after(updated);

        if (valid) {
            long updatedMillis = updated.getTime();
            long referenceMillis = referenceDate.getTime();
            referenceDate = new Date(Math.max(referenceMillis, updatedMillis - MILLIS_IN_HOUR));
        }

        return valid;
    }
}
