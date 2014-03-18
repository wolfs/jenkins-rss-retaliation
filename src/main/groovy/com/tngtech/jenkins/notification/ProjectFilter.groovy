package com.tngtech.jenkins.notification

import com.tngtech.jenkins.notification.model.BuildInfo
import org.apache.camel.Body

class ProjectFilter {
    private List<String> filter

    public ProjectFilter(List<String> filter) {
        this.filter = filter;
    }

    public boolean isValidProject(@Body BuildInfo buildInfo) {
        if (filter == null || filter.size() == 0) return true;

        return filter.contains(buildInfo.getProject().name);
    }
}
