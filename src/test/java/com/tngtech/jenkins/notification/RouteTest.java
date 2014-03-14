package com.tngtech.jenkins.notification;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Ignore;
import org.junit.Test;

public class RouteTest {
    @Test
    @Ignore("Currently no tests for the route itself")
    public void testFiltering() throws Exception {
        // create and start Camel
        CamelContext context = new CamelApplication(null).createCamelContext();
        context.start();

        // Get the mock endpoint
        MockEndpoint mock = context.getEndpoint("mock:result", MockEndpoint.class);

        // There should be at least two good blog entries from the feed
        mock.expectedMinimumMessageCount(2);

        // Asserts that the above expectations is true, will throw assertions exception if it failed
        // Camel will default wait max 20 seconds for the assertions to be true, if the conditions
        // is true sooner Camel will continue
        mock.assertIsSatisfied();

        // stop Camel after use
        context.stop();
    }
}
