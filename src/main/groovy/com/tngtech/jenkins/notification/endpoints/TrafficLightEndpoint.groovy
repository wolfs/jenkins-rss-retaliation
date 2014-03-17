package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo
import org.apache.camel.Body

import java.nio.file.Paths

import static java.nio.file.Files.exists

class TrafficLightEndpoint implements FeedbackEndpoint {

    private String binaryPath;
    private List<String> projectFilter;
    private String lastStatus;

    public TrafficLightEndpoint(String binaryPath, List<String> projectFilter) {
        this.binaryPath = binaryPath;
        this.projectFilter = projectFilter
        setTo("G")
    }

    @Override
    void process(@Body BuildInfo buildInfo) throws Exception {
        def status = buildInfo.status.toUpperCase();
        String targetColor;
        if (status.startsWith("FAIL")) {
            if (lastStatus != null && ! lastStatus.equals(status)) {
                setTo("-p 1 1 1")
            }
            targetColor = "R";
        } else if (status.startsWith("UNSTABLE")) {
            targetColor = "Y";
        } else {
            targetColor = "G"
        }

        setTo(targetColor);
        lastStatus = status;
    }

    private void setTo(String targetColor) {
        if (binaryPath == null) return;

        def binary = Paths.get(binaryPath)
        if (! exists(binary)) {
            return;
        }
        new ProcessBuilder().command(binary.toString(), targetColor).start()
    }
}
