package com.tngtech.jenkins.notification

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Culprit
import com.tngtech.jenkins.notification.model.Project
import groovyx.net.http.RESTClient
import org.apache.abdera.i18n.iri.IRI

import java.util.regex.Matcher

class BuildInfoViaRestProvider {

    BuildInfo getBuildInfo(IRI linkToBuild) {
        String url = linkToBuild.toASCIIString()
        def (baseUrl, buildId) = findBuildId(url)

        def buildData = queryRestApiForJson(url, 'tree=result,culprits[fullName,id]')

        def culprits = buildData.culprits.collect { culprit ->
            new Culprit(id: culprit.id, fullName: culprit.fullName)
        }
        def result = buildData.result

        def projectData = queryRestApiForJson(baseUrl, 'tree=displayName,name,builds[number,url]')
        Project project = new Project(name: projectData.name, displayName: projectData.displayName)

        return new BuildInfo(
                culprits: culprits,
                status: result,
                project: project)
    }

    def queryRestApiForJson(String url, String queryString) {
        println url
        RESTClient client = new RESTClient(url)
        def resp = client.get(path: 'api/json', queryString: queryString)
        return resp.responseData
    }

    def findBuildId(String url) {
        Matcher matcher = (url =~ '(.*/)([0-9]+)/?$')

        def match = matcher[0]
        int buildId = Integer.valueOf(match[2])
        String baseUrl = match[1]
        return [baseUrl, buildId]
    }
}
