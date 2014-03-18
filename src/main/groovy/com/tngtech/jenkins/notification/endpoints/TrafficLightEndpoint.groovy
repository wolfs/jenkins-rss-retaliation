package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.TrafficLightConfig
import org.apache.camel.Body

import java.nio.file.Paths

import static java.nio.file.Files.exists

class TrafficLightEndpoint implements FeedbackEndpoint {

    enum Status {
        STABLE("G"), UNSTABLE("Y"), FAIL("R")

        private String command;

        private Status(String command) {
            this.command = command;
        }
    }
    private String binaryPath;
    private Map<String, Status> projectStatus = new HashMap<>()

    public TrafficLightEndpoint(TrafficLightConfig trafficLightConfig) {
        this.binaryPath = trafficLightConfig.clewareUsbSwitchBinary;
    }

    @Override
    void process(@Body BuildInfo buildInfo) throws Exception {
        def status = buildInfo.status.toUpperCase();

        for (Status buildStatus : Status.values()) {
            if (status.startsWith(buildStatus.name())) {
                projectStatus.put(buildInfo.project.name, buildStatus);
            }
        }


        if (status.startsWith("FAIL")) {
            projectStatus.put(buildInfo.project.name, Status.FAIL);
        }

        updateLight()
    }

    private void updateLight() {
        Status highest = Status.STABLE
        for (Status status : projectStatus.values()) {
            if (status.ordinal() > highest.ordinal()) {
                highest = status;
            }
        }

        setTo(highest)
    }

    private void setTo(Status status) {
        if (binaryPath == null) return;

        def binary = Paths.get(binaryPath)
        if (! exists(binary)) {
            return;
        }
        new ProcessBuilder().command(binary.toString(), status.command).start()
    }
}
