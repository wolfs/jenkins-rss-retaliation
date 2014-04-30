package com.tngtech.jenkins.notification.endpoints

import static com.tngtech.jenkins.notification.model.Result.*
import static java.nio.file.Files.exists

import com.tngtech.jenkins.notification.camel.AllBuildInfosHolder
import com.tngtech.jenkins.notification.model.BuildHistory
import com.tngtech.jenkins.notification.model.Result
import com.tngtech.jenkins.notification.model.TrafficLightConfig

import java.nio.file.Paths

class TrafficLightEndpoint extends BaseEndpoint {

    private Map<Result, String> statusCommandMap = new HashMap<>()

    private String binaryPath
    private Result trafficLightStatus

    TrafficLightEndpoint(TrafficLightConfig trafficLightConfig) {
        if (trafficLightConfig != null) {
            this.binaryPath = trafficLightConfig.clewareUsbSwitchBinary
        }

        statusCommandMap[SUCCESS] = 'G'
        statusCommandMap[UNSTABLE] = 'Y'
        statusCommandMap[FAILURE] = 'R'
    }

    @Override
    void process(BuildHistory buildHistory) throws Exception {
        updateLight()
    }

    void updateLight() throws Exception {
        setTo(allBuildInfosHolder.allBuildInfos.overallResult)
    }

    @Override
    void init(AllBuildInfosHolder allBuildInfosHolder) {
        super.init(allBuildInfosHolder)
        updateLight()
    }

    private void setTo(Result status) {
        trafficLightStatus = status
        if (binaryPath == null) {
            return
        }

        def binary = Paths.get(binaryPath)
        if (!exists(binary)) {
            return
        }
        def process = new ProcessBuilder().command(binary.toString(), statusCommandMap.get(status)).start()
        process.waitFor()
    }

    Result getTrafficLightStatus() {
        trafficLightStatus
    }
}
