package com.tngtech.jenkins.notification;

import com.tngtech.jenkins.notification.endpoints.MissileEndpoint;
import com.tngtech.jenkins.notification.endpoints.TrafficLightEndpoint;
import com.tngtech.jenkins.notification.endpoints.TtsEndpoint;
import com.tngtech.jenkins.notification.model.Config;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.main.Main;
<<<<<<< HEAD
import org.apache.camel.model.RouteDefinition;
=======
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;
>>>>>>> 139e0f80414bbffc5937409d885b7e31db71bad7

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.camel.processor.idempotent.FileIdempotentRepository.fileIdempotentRepository;

public class CamelApplication extends Main {
    public static final String ENTRY_TO_BUILD_INFO_BEAN = "entryToBuildInfo";
    public static final String PROJECT_FILTER_BEAN = "projectFilter";

    public static final String MISSILE_ENDPOINT = "missileEndpoint";
    public static final String TTS_ENDPOINT = "ttsEndpoint";
    public static final String TRAFFIC_LIGHT_ENDPOINT = "trafficLightEndpoint";
    private Config config;

    CamelApplication(Config config) {
        this.config = config;
    }

    @Override
    public void run() throws Exception {
        CamelContext camelContext = createCamelContext();
        camelContexts.add(camelContext);
        super.run();
    }

    protected CamelContext createCamelContext() throws Exception {

        SimpleRegistry registry = new SimpleRegistry();
        registry.put(ENTRY_TO_BUILD_INFO_BEAN, new EntryToBuildInfo(new BuildInfoViaRestProvider()));
<<<<<<< HEAD
        registry.put(PROJECT_FILTER_BEAN, new ProjectFilter(config.getProjectNameFilter()));
        registry.put(MISSILE_ENDPOINT, new MissileEndpoint(config.getLocations()));
=======
        registry.put(MISSILE_ENDPOINT, new MissileEndpoint(config.getMissile()));
>>>>>>> 139e0f80414bbffc5937409d885b7e31db71bad7
        registry.put(TTS_ENDPOINT, new TtsEndpoint(config.getTts()));
        registry.put(TRAFFIC_LIGHT_ENDPOINT, new TrafficLightEndpoint(config.getClewareUsbSwitchBinary(),
                config.getProjectNameFilter()));

        context = new DefaultCamelContext(registry);

        context.addRoutes(createRoutes());

        return context;
    }

    private CamelContext context;

    protected RouteBuilder createRoutes() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                Date nowDate = new Date();
                String now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(nowDate);
                fromF("atom:%s?splitEntries=true&consumer.delay=%s&lastUpdate=%s",
                        config.getRssFeedUrl(),
<<<<<<< HEAD
                        config.getDefaultDelay(),
                        now)
                        .idempotentConsumer(simple("${body.id}"), fileIdempotentRepository(new File("idrepo")))
=======
                        config.getPollInterval(),
                        now).id("atom")
                        .idempotentConsumer(simple("${body.id}"), FileIdempotentRepository.fileIdempotentRepository(new File("idrepo")))
>>>>>>> 139e0f80414bbffc5937409d885b7e31db71bad7
                        .toF("bean:%s", ENTRY_TO_BUILD_INFO_BEAN)
                        .filter().method(PROJECT_FILTER_BEAN, "isValidProject")
                        .to("log:com.tngtech.jenkins.notification?showAll=true&multiline=true")
                        .to("seda:feeds");

                List<String> endpoints = new ArrayList<>();
                for (String endpoint: config.getFeedbackDevices()) {
                    endpoints.add(String.format("bean:%sEndpoint", endpoint));
                }
                MulticastDefinition multicast = from("seda:feeds").multicast();
                if (config.isFeedbackInParallel()) {
                    multicast.parallelProcessing();
                }
                multicast.to(endpoints.toArray(new String[] {}));
            }
        };
    }
}
