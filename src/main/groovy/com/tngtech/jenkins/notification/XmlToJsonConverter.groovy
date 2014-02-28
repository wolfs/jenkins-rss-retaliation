package com.tngtech.jenkins.notification

import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient
import org.apache.camel.Body
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.language.XPath
import org.apache.commons.codec.Charsets

class XmlToJsonConverter implements Processor {
    void process(@Body String url) throws Exception {
        System.out.println(url)
    }

    @Override
    void process(Exchange exchange) throws Exception {
        def body = exchange.getIn().getBody(String.class)
        def parsedBody =new XmlSlurper().parseText(body)
        String link = parsedBody.link.@href
        def client = new RESTClient(link)
        def resp = client.get(path: 'api/json', queryString: 'tree=result,culprits[fullName,id]')

        def data = resp.responseData
        def names = data.culprits.id
        def result = data.result
        println(names)

        def jsonBuilder = new groovy.json.JsonBuilder()
        jsonBuilder.build(
                status: result,
                phase: 'FINISHED',
                culprits: names
        )


        def json = jsonBuilder.toString()
        exchange.getIn().setBody(json, String.class)
    }
}
