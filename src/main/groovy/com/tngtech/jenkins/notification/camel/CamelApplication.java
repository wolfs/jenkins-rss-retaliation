package com.tngtech.jenkins.notification.camel;

import com.tngtech.jenkins.notification.BuildInfoViaRestProvider;
import com.tngtech.jenkins.notification.endpoints.FeedbackEndpoint;
import com.tngtech.jenkins.notification.endpoints.MissileEndpoint;
import com.tngtech.jenkins.notification.endpoints.TrafficLightEndpoint;
import com.tngtech.jenkins.notification.endpoints.TtsEndpoint;
import com.tngtech.jenkins.notification.model.BuildInfo;
import com.tngtech.jenkins.notification.model.Config;
import com.tngtech.jenkins.notification.camel.AllBuildInfosHolder;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.main.Main;
import org.apache.camel.model.MulticastDefinition;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.camel.processor.idempotent.FileIdempotentRepository.fileIdempotentRepository;

public class CamelApplication extends Main {
    public static final String ENTRY_TO_BUILD_INFO_BEAN = "entryToBuildInfo";

    public static final String MISSILE_ENDPOINT = "missileEndpoint";
    public static final String TTS_ENDPOINT = "ttsEndpoint";
    public static final String TRAFFIC_LIGHT_ENDPOINT = "trafficLightEndpoint";
    public static final String BUILD_JOB_STATUS_HOLDER = "buildJobStatusHolder";
    private Config config;
    private BuildInfoViaRestProvider buildInfoViaRestProvider = new BuildInfoViaRestProvider();
    private AllBuildInfosHolder buildJobsStatusHolder = new AllBuildInfosHolder();

    private CamelApplication() {}

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

    RouteBuilder createRoutes() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                Date date = new Date();
                String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);

                fromF("atom:%s?splitEntries=true&lastUpdate=%s&throttleEntries=false",
                        config.getRssFeedUrl(), dateString)
                        .id("atom")
                        .idempotentConsumer(simple("${body.id}"), fileIdempotentRepository(new File("initialRepo")))
                        .toF("bean:%s", ENTRY_TO_BUILD_INFO_BEAN)
                        .toF("bean:%s", BUILD_JOB_STATUS_HOLDER)
                        .to("log:com.tngtech.jenkins.notification?showAll=true&multiline=true")
                        .to("seda:feeds");

                List<String> endpoints = new ArrayList<>();
                for (String endpoint : config.getEndpoints()) {
                    endpoints.add(String.format("bean:%s", endpoint));
                }
                MulticastDefinition multicast = from("seda:feeds").multicast();
                if (config.isFeedbackInParallel()) {
                    multicast.parallelProcessing();
                }
                multicast.to(endpoints.toArray(new String[endpoints.size()]));
            }
        };
    }
}
