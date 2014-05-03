package com.tngtech.jenkins.notification.camel;

import com.tngtech.jenkins.notification.BuildInfoViaRestProvider;
import com.tngtech.jenkins.notification.endpoints.FeedbackEndpoint;
import com.tngtech.jenkins.notification.model.*;
import org.apache.abdera.model.Entry;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class CamelIntegrationTest extends CamelTestSupport {
    private SimpleRegistry registry = new SimpleRegistry();

    @Mock
    private FeedbackEndpoint missileEndpoint;
    @Mock
    private FeedbackEndpoint someEndpoint;
    @Mock
    private FeedbackEndpoint inactiveEndpoint;
    @Mock
    private EntryToBuildInfo entryToBuildInfo;
    @Mock
    private BuildInfoViaRestProvider buildInfoViaRestProvider;
    @Mock
    private DateFilter dateFilter;

    private AllBuildInfosHolder allBuildInfosHolder = new AllBuildInfosHolder();

    private Config config;
    @InjectMocks
    private CamelApplication camelApplication;

    @Override
    protected void doPreSetup() throws Exception {
        config = new Config();
        List<String> endpoints = new ArrayList<>();
        endpoints.add("missile");
        endpoints.add("some");
        config.setFeedbackDevices(endpoints);
        config.setRssFeedUrl(getClass().getClassLoader().getResource("rssAll.xml").toURI().toASCIIString());

        config.setFeedbackInParallel(testName.getMethodName().contains("parallel"));
        config.setPollInterval(100);

        camelApplication = new CamelApplication(config);

        MockitoAnnotations.initMocks(this);
        registry.put("missileEndpoint", missileEndpoint);
        registry.put("someEndpoint", someEndpoint);
        registry.put("inactiveEndpoint", inactiveEndpoint);
        registry.put(CamelApplication.ENTRY_TO_BUILD_INFO_BEAN, entryToBuildInfo);
        registry.put(CamelApplication.BUILD_JOB_STATUS_HOLDER, allBuildInfosHolder);
        registry.put(CamelApplication.DATE_FILTER_BEAN, new DateFilter(new Date(0)));

        given(entryToBuildInfo.process(any(Entry.class))).willAnswer(new Answer<BuildInfo>() {
            @Override
            public BuildInfo answer(InvocationOnMock invocation) throws Throwable {
                Entry entry = (Entry) invocation.getArguments()[0];
                String feedMessage = entry.getTitle();
                return createBuildInfoFromFeedMessage(feedMessage);
            }
        });
    }

    private BuildInfo createBuildInfoFromFeedMessage(String feedMessage) {
        BuildInfo buildInfo = new BuildInfo();
        Project project = new Project();
        project.setName(feedMessage.split(" ")[0]);
        buildInfo.setProject(project);
        Result result = Result.UNSTABLE;
        if (feedMessage.contains("Stabil")) {
            result = Result.SUCCESS;
        }
        buildInfo.setResult(result);
        buildInfo.setFeedMessage(feedMessage);

        return buildInfo;
    }

    @Test
    public void rss_is_read() throws Exception {
        context.start();

        verify(missileEndpoint, timeout(10000).times(40)).process(any(BuildHistory.class));
        BuildInfo currentBuild = createBuildInfoFromFeedMessage("resultSetter #33 (Defekt seit diesem Build.)");
        BuildInfo previousBuild = createBuildInfoFromFeedMessage("resultSetter #32 (Wieder normal)");
        Assertions.assertThat(allBuildInfosHolder.getAllBuildInfos().getJobsHistory())
                .containsOnly(
                        entry("resultSetter", new BuildHistory(currentBuild, previousBuild)),
                        entry("resultSetterViaGit", new BuildHistory(
                                createBuildInfoFromFeedMessage("resultSetterViaGit #23 (1 Test schlägt immer noch fehl)"),
                                createBuildInfoFromFeedMessage("resultSetterViaGit #22 (1 Test schlägt immer noch fehl)")))
                );

        Assertions.assertThat(allBuildInfosHolder.getAllBuildInfos().getOverallResult()).isEqualTo(Result.UNSTABLE);
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
