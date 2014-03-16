package com.tngtech.jenkins.notification;

import com.tngtech.jenkins.notification.endpoints.FeedbackEndpoint;
import com.tngtech.jenkins.notification.model.BuildInfo;
import com.tngtech.jenkins.notification.model.Config;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.*;

public class CamelApplicationTest extends CamelTestSupport {

    public static final int DEFAULT_TIMEOUT_MILLIS = 1000;
    private SimpleRegistry registry = new SimpleRegistry();

    @Produce(uri = "direct:atom")
    protected ProducerTemplate producer;

    @Mock
    private FeedbackEndpoint missileEndpoint;
    @Mock
    private FeedbackEndpoint someEndpoint;
    @Mock
    private EntryToBuildInfo entryToBuildInfo;
    @Mock
    private BuildInfo buildInfo;
    @Mock
    private Entry entry;

    private CountDownLatch missileCountdown = new CountDownLatch(1);
    private CountDownLatch someCountdown = new CountDownLatch(1);
    private Config config;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected void doPreSetup() throws Exception {
        config = new Config();
        ArrayList endpoints = new ArrayList<>();
        endpoints.add("missile");
        endpoints.add("some");
        config.setFeedbackDevices(endpoints);
        config.setRssFeedUrl("http://localhost:8080/ci/rssAll");

        config.setFeedbackInParallel(testName.getMethodName().contains("parallel"));
    }

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        registry.put("missileEndpoint", missileEndpoint);
        registry.put("someEndpoint", someEndpoint);
        registry.put(CamelApplication.ENTRY_TO_BUILD_INFO_BEAN, entryToBuildInfo);

        given(entry.getId()).willReturn(new IRI(UUID.randomUUID().toString()));
        given(entryToBuildInfo.process(entry)).willReturn(buildInfo);

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:atom");
            }
        });
    }

    @Test
    public void endpoints_are_called_in_order_for_sequential_processing() throws Exception {
        prepareEndpointWithCountdown(missileEndpoint, missileCountdown);

        context.start();

        producer.sendBody(entry);

        verify(missileEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildInfo);
        verify(someEndpoint, never()).process(buildInfo);

        missileCountdown.countDown();

        verify(someEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildInfo);
    }

    @Test
    public void endpoints_are_called_in_order_for_parallel_processing() throws Exception {
        prepareEndpointWithCountdown(missileEndpoint, missileCountdown);
        prepareEndpointWithCountdown(someEndpoint, someCountdown);

        context.start();

        producer.sendBody(entry);

        verify(missileEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildInfo);
        verify(someEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildInfo);

        missileCountdown.countDown();
        someCountdown.countDown();
    }

    @Test
    public void each_message_is_processed_only_once() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(2).create();
        context.start();


        producer.sendBody(entry);
        producer.sendBody(entry);

        verify(missileEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildInfo);
        verify(someEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildInfo);

        assertTrue(notify.matches(DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));

        verifyNoMoreInteractions(missileEndpoint, someEndpoint);
    }

    private void prepareEndpointWithCountdown(FeedbackEndpoint mockEndpoint, final CountDownLatch latch) throws Exception {
        willAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                latch.await();
                return null;
            }
        }).given(mockEndpoint).process(buildInfo);
    }


    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new CamelApplication(config).createRoutes();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = new DefaultCamelContext(registry);
        context.setLazyLoadTypeConverters(isLazyLoadingTypeConverter());
        return context;
    }
}
