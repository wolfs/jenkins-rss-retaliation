package com.tngtech.jenkins.notification.camel;

import com.tngtech.jenkins.notification.BuildInfoViaRestProvider;
import com.tngtech.jenkins.notification.endpoints.FeedbackEndpoint;
import com.tngtech.jenkins.notification.model.*;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

public class CamelApplicationTest extends CamelTestSupport {

    private static final int DEFAULT_TIMEOUT_MILLIS = 1000;
    private static final String VIEW_BASE_URL = "http://localhost:8080/ci/";

    private SimpleRegistry registry = new SimpleRegistry();

    @Produce(uri = "direct:atom")
    private ProducerTemplate producer;

    @Mock
    private FeedbackEndpoint missileEndpoint;
    @Mock
    private FeedbackEndpoint someEndpoint;
    @Mock
    private FeedbackEndpoint inactiveEndpoint;
    @Mock
    private EntryToBuildInfo entryToBuildInfo;
    @Mock
    private BuildInfo buildInfo;
    private BuildHistory buildHistory = new BuildHistory(null, null);
    @Mock
    private Entry entry;
    @Mock
    private DateFilter dateFilter;
    @Mock
    private AllBuildInfosHolder statusHolder;
    @Mock
    private BuildInfoViaRestProvider buildInfoViaRestProvider;

    private CountDownLatch missileCountdown = new CountDownLatch(1);
    private CountDownLatch someCountdown = new CountDownLatch(1);
    private Config config;
    @InjectMocks
    private CamelApplication camelApplication;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected void doPreSetup() throws Exception {
        config = new Config();
        List<String> endpoints = new ArrayList<>();
        endpoints.add("missile");
        endpoints.add("some");
        config.setFeedbackDevices(endpoints);
        config.setRssFeedUrl(VIEW_BASE_URL + "rssAll");

        config.setFeedbackInParallel(testName.getMethodName().contains("parallel"));

        camelApplication = new CamelApplication(config);
    }

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        registry.put("missileEndpoint", missileEndpoint);
        registry.put("someEndpoint", someEndpoint);
        registry.put("inactiveEndpoint", inactiveEndpoint);
        registry.put(CamelApplication.ENTRY_TO_BUILD_INFO_BEAN, entryToBuildInfo);
        registry.put(CamelApplication.BUILD_JOB_STATUS_HOLDER, statusHolder);
        registry.put(CamelApplication.DATE_FILTER_BEAN, dateFilter);

        given(entry.getId()).willReturn(new IRI(UUID.randomUUID().toString()));
        given(entryToBuildInfo.process(entry)).willReturn(buildInfo);
        given(statusHolder.process(buildInfo)).willReturn(buildHistory);
        given(dateFilter.isValid(entry)).willReturn(true);

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:atom");
            }
        });
    }

    @Test
    public void BuildJobsStatus_is_queried_via_REST() throws Exception {
        List<BuildInfo> buildInfos = new ArrayList<>();
        buildInfos.add(buildInfoFor("project1", Result.SUCCESS));
        buildInfos.add(buildInfoFor("project2", Result.FAILURE));
        given(buildInfoViaRestProvider.queryInitalData(VIEW_BASE_URL)).willReturn(buildInfos);

        camelApplication.init(registry);

        verify(buildInfoViaRestProvider).queryInitalData(VIEW_BASE_URL);
        for (BuildInfo buildInfo : buildInfos) {
            verify(statusHolder).process(buildInfo);
        }
    }

    @Test
    public void BuildJobsStatus_is_injected_into_active_Endpoints() throws Exception {
        camelApplication.init(registry);

        verify(missileEndpoint).init(statusHolder);
        verify(someEndpoint).init(statusHolder);
        verify(inactiveEndpoint, never()).init(statusHolder);
    }

    private BuildInfo buildInfoFor(String project, Result result) {
        return new BuildInfo(null, new Project(project, project), result);
    }

    @Test
    public void endpoints_are_called_in_order_for_sequential_processing() throws Exception {
        prepareEndpointWithCountdown(missileEndpoint, missileCountdown);

        context.start();

        producer.sendBody(entry);

        verify(missileEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildHistory);
        verify(someEndpoint, never()).process(buildHistory);

        missileCountdown.countDown();

        verify(someEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildHistory);
    }

    @Test
    public void endpoints_are_called_in_order_for_parallel_processing() throws Exception {
        prepareEndpointWithCountdown(missileEndpoint, missileCountdown);
        prepareEndpointWithCountdown(someEndpoint, someCountdown);

        context.start();

        producer.sendBody(entry);

        verify(missileEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildHistory);
        verify(someEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildHistory);

        missileCountdown.countDown();
        someCountdown.countDown();
    }

    @Test
    public void each_message_is_processed_only_once() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(2).create();
        context.start();


        producer.sendBody(entry);
        producer.sendBody(entry);

        verify(missileEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildHistory);
        verify(someEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildHistory);

        assertTrue(notify.matches(DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));

        verifyNoMoreInteractions(missileEndpoint, someEndpoint);
    }

    @Test
    public void on_error_the_message_is_added_to_idRepo() throws Exception {
        willThrow(NullPointerException.class).given(missileEndpoint).process(any(BuildHistory.class));

        NotifyBuilder notify = new NotifyBuilder(context).from("direct:atom").whenDone(2).create();

        context.start();

        producer.sendBody(entry);
        verify(missileEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildHistory);
        verify(someEndpoint, timeout(DEFAULT_TIMEOUT_MILLIS)).process(buildHistory);

        producer.sendBody(entry);

        assertTrue(notify.matches(DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        Thread.sleep(1000);

        verifyNoMoreInteractions(missileEndpoint, someEndpoint);
    }

    private void prepareEndpointWithCountdown(FeedbackEndpoint mockEndpoint, final CountDownLatch latch) throws Exception {
        willAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                latch.await();
                return null;
            }
        }).given(mockEndpoint).process(buildHistory);
    }


    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return camelApplication.createRoutes();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = new DefaultCamelContext(registry);
        context.setLazyLoadTypeConverters(isLazyLoadingTypeConverter());
        return context;
    }
}
