package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.TrafficLightConfig
import com.tngtech.jenkins.notification.camel.AllBuildInfosHolder
import com.tngtech.jenkins.notification.model.Result

import java.nio.file.Paths

import static com.tngtech.jenkins.notification.model.Result.*
import static java.nio.file.Files.exists

class TrafficLightEndpoint extends BaseEndpoint {

    private Map<Result, String> statusCommandMap = new HashMap<>();

    private String binaryPath;
    private Result trafficLightStatus;

    public TrafficLightEndpoint(TrafficLightConfig trafficLightConfig) {
        if (trafficLightConfig != null) {
            this.binaryPath = trafficLightConfig.clewareUsbSwitchBinary;
        }

        statusCommandMap[SUCCESS] = "G";
        statusCommandMap[UNSTABLE] = "Y";
        statusCommandMap[FAILURE] = "R";
    }

    @Override
    void process(BuildInfo buildInfo) throws Exception {
        updateLight();
    }

    void updateLight() throws Exception {
        setTo(allBuildInfosHolder.buildJobsStatus.overallResult);
    }

    @Override
    void init(AllBuildInfosHolder allBuildInfosHolder) {
        super.init(allBuildInfosHolder)
        updateLight();
    }

    private void setTo(Result status) {
        trafficLightStatus = status;
        if (binaryPath == null) return;

        def binary = Paths.get(binaryPath)
        if (! exists(binary)) {
            return;
        }
        new ProcessBuilder().command(binary.toString(), statusCommandMap.get(status)).start()
    }

    Result getTrafficLightStatus() {
        return trafficLightStatus
    }
}
