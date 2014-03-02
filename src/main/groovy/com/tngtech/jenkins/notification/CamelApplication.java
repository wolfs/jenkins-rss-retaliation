package com.tngtech.jenkins.notification;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.main.Main;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamelApplication extends Main {
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

        // First we register a blog service in our bean registry
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("xmlToJsonConverter", new XmlToJsonConverter());
        if (!config.isUsePythonScript()) {
            registry.put("missileEndpoint", new MissileEndpoint(config.getLocations()));
        }
//        registry.put("blogService", new BlogService());

        // Then we create the camel context with our bean registry
        context = new DefaultCamelContext(registry);

        // Then we add all the routes we need using the route builder DSL syntax
        context.addRoutes(createMyRoutes());

        return context;
    }

    private CamelContext context;

    /**
     * This is the route builder where we create our routes using the Camel DSL
     */
    protected RouteBuilder createMyRoutes() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                Date nowDate = new Date();
                String now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(nowDate);
                from("atom:" + config.getRssFeedUrl() + "?splitEntries=true&consumer.delay=" + config.getDefaultDelay() + "&lastUpdate=" + now)
                        .idempotentConsumer(simple("${body.id}"), FileIdempotentRepository.fileIdempotentRepository(new File("idrepo")))
                        .to("xmlToJsonConverter")
                        .to("log:com.tngtech.jenkins.notification?showAll=true&multiline=true")
                        .to("seda:feeds");

                String missileEndpoint;
                if (config.isUsePythonScript()) {
                    missileEndpoint = "netty:udp://localhost:22222?textline=true";
                } else {
                    missileEndpoint = "missileEndpoint";
                }
                from("seda:feeds")
                        .throttle(1).timePeriodMillis(1000)
                        .to(missileEndpoint);
            }
        };
    }
}
