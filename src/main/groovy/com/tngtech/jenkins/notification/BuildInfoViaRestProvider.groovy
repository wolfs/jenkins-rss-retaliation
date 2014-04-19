package com.tngtech.jenkins.notification

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Culprit
import com.tngtech.jenkins.notification.model.Project
import com.tngtech.jenkins.notification.model.Result
import groovyx.net.http.RESTClient
import org.apache.abdera.i18n.iri.IRI

import java.util.regex.Matcher

class BuildInfoViaRestProvider {

    private static final String PROJECT_DATA='displayName,name'
    private static final String BUILD_DATA='number,result,culprits[fullName,id]'

    BuildInfo getBuildInfo(IRI linkToBuild) {
        String url = linkToBuild.toASCIIString()
        String baseUrl = extractBaseUrl(url)

        def buildData = queryRestApiForJson(url, "tree=${BUILD_DATA}")
        def projectData = queryRestApiForJson(baseUrl, "tree=${PROJECT_DATA}")

        return createBuildInfo(buildData, projectData)
    }


    public List<BuildInfo> queryInitalData(String url) {
        def viewData = queryRestApiForJson(url, "tree=jobs[${PROJECT_DATA},lastBuild[${BUILD_DATA}]]")

        return viewData.jobs.findAll { it.lastBuild }.collect { job ->
            createBuildInfo(job.lastBuild, job)
        }
    }

    private BuildInfo createBuildInfo(buildData, projectData) {
        def culprits = buildData.culprits.collect { culprit ->
            new Culprit(id: culprit.id, fullName: culprit.fullName)
        }
        def result = Result.fromString(buildData.result)

        Project project = new Project(name: projectData.name, displayName: projectData.displayName)

        return new BuildInfo(
                culprits: culprits,
                result: result,
                project: project,
                buildNumber: buildData.number
        )
    }

    def queryRestApiForJson(String url, String queryString) {
        RESTClient client = new RESTClient(url)
        def resp = client.get(path: 'api/json', queryString: queryString)
        return resp.responseData
    }

    String extractBaseUrl(String url) {
        Matcher matcher = (url =~ '(.*/)([0-9]+)/?$')

        def match = matcher[0]
        String baseUrl = match[1]
        return baseUrl
    }
}
