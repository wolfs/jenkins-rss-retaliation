package com.tngtech.jenkins.notification.model

import com.tngtech.jenkins.notification.model.Result
import spock.lang.Specification
import spock.lang.Unroll

class ResultTest extends Specification {

    @Unroll
    def 'String #result.name() is converted to enum constant'(Result result) {
        expect:
        Result.fromString(result.name()) == result

        where:
        result << Result.values()
    }

    def 'Unknown String is converted to null'() {
        expect:
        Result.fromString('SomeString') == null
    }
}
