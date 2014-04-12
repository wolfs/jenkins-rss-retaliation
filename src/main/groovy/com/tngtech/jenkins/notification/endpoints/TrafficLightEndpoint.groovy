package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.TrafficLightConfig
import com.tngtech.jenkins.notification.status.BuildStatus

import java.nio.file.Paths

import static com.tngtech.jenkins.notification.status.BuildStatus.*
import static java.nio.file.Files.exists

class TrafficLightEndpoint extends BaseEndpoint {

    private Map<BuildStatus, String> statusCommandMap = new HashMap<>();

    private String binaryPath;
    private BuildStatus trafficLightStatus;

    public TrafficLightEndpoint(TrafficLightConfig trafficLightConfig) {
        if (trafficLightConfig != null) {
            this.binaryPath = trafficLightConfig.clewareUsbSwitchBinary;
        }

        statusCommandMap[SUCCESS] = "G";
        statusCommandMap[UNSTABLE] = "Y";
        statusCommandMap[FAIL] = "R";
    }

    @Override
    void processUpdate(BuildInfo buildInfo) throws Exception {
        setTo(buildInfo.overallJobsStatus.status);
    }

    @Override
    void processInitial(BuildInfo buildInfo) throws Exception {
        setTo(buildInfo.overallJobsStatus.status);
    }

    private void setTo(BuildStatus status) {
        trafficLightStatus = status;
        if (binaryPath == null) return;

        def binary = Paths.get(binaryPath)
        if (! exists(binary)) {
            return;
        }
        new ProcessBuilder().command(binary.toString(), statusCommandMap.get(status)).start()
    }

    BuildStatus getTrafficLightStatus() {
        return trafficLightStatus
    }
}
