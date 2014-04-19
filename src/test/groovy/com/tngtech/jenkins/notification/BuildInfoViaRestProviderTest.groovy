package com.tngtech.jenkins.notification

import org.apache.abdera.i18n.iri.IRI
import spock.lang.Ignore
import spock.lang.Specification

public class BuildInfoViaRestProviderTest extends Specification {

    def 'Build Id should be extracted from URL'() {
        when:
        BuildInfoViaRestProvider buildInfoViaRestProvider = new BuildInfoViaRestProvider();

        then:
        buildInfoViaRestProvider.extractBaseUrl('http://localhost:8080/job/downstream/13/') == 'http://localhost:8080/job/downstream/'
    }

    @Ignore
    def 'Build info is found'() {
        when:
        BuildInfoViaRestProvider buildInfoViaRestProvider = new BuildInfoViaRestProvider()

        then:
        println buildInfoViaRestProvider.getBuildInfo(new IRI('http://localhost:8080/job/downstream/13/'))
    }

    @Ignore
    def 'Build infos are extracted from view'() {
        when:
        BuildInfoViaRestProvider buildInfoViaRestProvider = new BuildInfoViaRestProvider()

        then:
        println buildInfoViaRestProvider.queryInitalData('http://localhost:8080/view/ci/')
    }

}
