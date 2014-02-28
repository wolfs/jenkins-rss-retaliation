package com.tngtech.jenkins.notification;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.joda.time.DateTime;

import java.io.File;

public class Bootstrap {

    private CamelContext context;
    private String rssFeedUrl;

    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.rssFeedUrl = System.getProperty("jenkins.url", "http://localhost:8080/job/property-loader/rssFailed");
        if (args.length == 1) {
            bootstrap.rssFeedUrl = args[0];
        }
        CamelContext camelContext = bootstrap.createCamelContext();
        camelContext.start();
    }

    protected CamelContext createCamelContext() throws Exception {

        // First we register a blog service in our bean registry
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("xmlToJsonConverter", new XmlToJsonConverter());
//        registry.put("blogService", new BlogService());

        // Then we create the camel context with our bean registry
        context = new DefaultCamelContext(registry);

        // Then we add all the routes we need using the route builder DSL syntax
        context.addRoutes(createMyRoutes());

        return context;
    }

    /**
     * This is the route builder where we create our routes using the Camel DSL
     */
    protected RouteBuilder createMyRoutes() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                // We pool the atom feeds from the source for further processing in the seda queue
                // we set the delay to 1 second for each pool as this is a unit test also and we can
                // not wait the default poll interval of 60 seconds.
                // Using splitEntries=true will during polling only fetch one Atom Entry at any given time.
                // As the feed.atom file contains 7 entries, using this will require 7 polls to fetch the entire
                // content. When Camel have reach the end of entries it will refresh the atom feed from URI source
                // and restart - but as Camel by default uses the UpdatedDateFilter it will only deliver new
                // blog entries to "seda:feeds". So only when James Straham updates his blog with a new entry
                // Camel will create an exchange for the seda:feeds.
//                from("atom:http://localhost:8080/rssAll?splitEntries=true&consumer.delay=1000")
                String now = DateTime.now().toString("yyyy-MM-dd'T'HH:mm:ss");
                from("atom:" + rssFeedUrl + "?splitEntries=true&consumer.delay=1000&lastUpdate=" + now)
                        .idempotentConsumer(simple("${body.id}"), FileIdempotentRepository.fileIdempotentRepository(new File("idrepo")))
                        .to("xmlToJsonConverter")
                        .to("log:com.tngtech.jenkins.notification?showAll=true&multiline=true")
                        .to("seda:feeds");

//                // From the feeds we filter each blot entry by using our blog service class
//                from("seda:feeds").filter().method("blogService", "isGoodBlog").to("seda:goodBlogs");

                // And the good blogs is moved to a mock queue as this sample is also used for unit testing
                // this is one of the strengths in Camel that you can also use the mock endpoint for your
                // unit tests
                from("seda:feeds")
                        .throttle(1).timePeriodMillis(1000)
                        .to("log:com.tngtech.jenkins.notification.udp?showAll=true&multiline=true")
                        .to("netty:udp://localhost:22222?textline=true");
            }
        };
    }
}
