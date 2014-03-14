package com.tngtech.jenkins.notification;

import com.tngtech.jenkins.notification.endpoints.MissileEndpoint;
import com.tngtech.jenkins.notification.model.Config;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.main.Main;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamelApplication extends Main {
    public static final String ENTRY_TO_BUILD_INFO_BEAN = "entryToBuildInfo";
    public static final String MISSILE_ENDPOINT = "missileEndpoint";
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
        registry.put(MISSILE_ENDPOINT, new MissileEndpoint(config.getLocations()));

        context = new DefaultCamelContext(registry);

        context.addRoutes(createMyRoutes());

        return context;
    }

    private CamelContext context;

    protected RouteBuilder createMyRoutes() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                Date nowDate = new Date();
                String now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(nowDate);
                fromF("atom:%s?splitEntries=true&consumer.delay=%s&lastUpdate=%s",
                        config.getRssFeedUrl(),
                        config.getDefaultDelay(),
                        now)
                        .idempotentConsumer(simple("${body.id}"), FileIdempotentRepository.fileIdempotentRepository(new File("idrepo")))
                        .toF("bean:%s", ENTRY_TO_BUILD_INFO_BEAN)
                        .to("log:com.tngtech.jenkins.notification?showAll=true&multiline=true")
                        .to("seda:feeds");

                RouteDefinition feeds = from("seda:feeds");
                for (String endpoint: config.getEndpoints()) {
                    feeds.toF("bean:%s", endpoint + "Endpoint");
                }
            }
        };
    }
}
