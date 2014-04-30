package com.tngtech.jenkins.notification.camel;

import com.tngtech.jenkins.notification.BuildInfoViaRestProvider;
import com.tngtech.jenkins.notification.endpoints.FeedbackEndpoint;
import com.tngtech.jenkins.notification.endpoints.MissileEndpoint;
import com.tngtech.jenkins.notification.endpoints.TrafficLightEndpoint;
import com.tngtech.jenkins.notification.endpoints.TtsEndpoint;
import com.tngtech.jenkins.notification.model.BuildInfo;
import com.tngtech.jenkins.notification.model.Config;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.main.Main;
import org.apache.camel.model.MulticastDefinition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.camel.processor.idempotent.MemoryIdempotentRepository.memoryIdempotentRepository;

public class CamelApplication extends Main {
    public static final String ENTRY_TO_BUILD_INFO_BEAN = "entryToBuildInfo";

    private static final String MISSILE_ENDPOINT = "missileEndpoint";
    private static final String TTS_ENDPOINT = "ttsEndpoint";
    private static final String TRAFFIC_LIGHT_ENDPOINT = "trafficLightEndpoint";
    public static final String BUILD_JOB_STATUS_HOLDER = "buildJobStatusHolder";
    private Config config;
    private BuildInfoViaRestProvider buildInfoViaRestProvider = new BuildInfoViaRestProvider();
    private AllBuildInfosHolder buildJobsStatusHolder = new AllBuildInfosHolder();
    Date lastUpdate = new Date();

    private CamelApplication() {
    }

    public CamelApplication(Config config) {
        this.config = config;
    }

    @Override
    public void run() throws Exception {
        CamelContext camelContext = createCamelContext();
        camelContexts.add(camelContext);
        super.run();
    }

    CamelContext createCamelContext() throws Exception {

        SimpleRegistry registry = new SimpleRegistry();
        registry.put(ENTRY_TO_BUILD_INFO_BEAN, new EntryToBuildInfo(new BuildInfoViaRestProvider()));
        registry.put(MISSILE_ENDPOINT, new MissileEndpoint(config.getMissile()));
        registry.put(TTS_ENDPOINT, new TtsEndpoint(config.getTts()));
        registry.put(TRAFFIC_LIGHT_ENDPOINT, new TrafficLightEndpoint(config.getTrafficLight()));
        registry.put(BUILD_JOB_STATUS_HOLDER, buildJobsStatusHolder);

        init(registry);

        CamelContext context = new DefaultCamelContext(registry);

        context.addRoutes(createRoutes());

        return context;
    }

    void init(SimpleRegistry registry) {
        initStatusHolder();
        initEndpoints(config.getEndpoints(), registry);
    }

    private void initStatusHolder() {
        List<BuildInfo> initalData = buildInfoViaRestProvider.queryInitalData(extractBaseUrl(config.getRssFeedUrl()));
        for (BuildInfo buildInfo : initalData) {
            buildJobsStatusHolder.process(buildInfo);
        }
    }

    private String extractBaseUrl(String rssFeedUrl) {
        return rssFeedUrl.replaceFirst("/rss[^/]*$", "/");
    }

    private void initEndpoints(List<String> endpoints, Map<String, Object> registry) {
        for (String endpointName : endpoints) {
            FeedbackEndpoint endpoint = (FeedbackEndpoint) registry.get(endpointName);
            endpoint.init(buildJobsStatusHolder);
        }
    }

    RouteBuilder createRoutes() {
        return new RouteBuilder() {
            public void configure() throws Exception {
                String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(lastUpdate);
                fromF("atom:%s?splitEntries=true&lastUpdate=%s&sortEntries=true&throttleEntries=false&consumer.delay=%d",
                        config.getRssFeedUrl(), dateString, config.getPollInterval())
                        .id("atom")
                        .idempotentConsumer(
                                simple("${body.id}"),
                                memoryIdempotentRepository())
                        .removeOnFailure(false)
                        .to("seda:feeds");

                MulticastDefinition multicast = from("seda:feeds")
                        .toF("bean:%s", ENTRY_TO_BUILD_INFO_BEAN)
                        .toF("bean:%s", BUILD_JOB_STATUS_HOLDER)
                        .to("log:com.tngtech.jenkins.notification?showBody=true&multiline=true")
                        .multicast();

                List<String> endpoints = new ArrayList<>();
                for (String endpoint : config.getEndpoints()) {
                    endpoints.add(String.format("bean:%s", endpoint));
                }
                if (config.isFeedbackInParallel()) {
                    multicast.parallelProcessing();
                }
                multicast.to(endpoints.toArray(new String[endpoints.size()]));
            }
        };
    }
}
