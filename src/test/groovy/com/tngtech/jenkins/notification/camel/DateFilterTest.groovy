package com.tngtech.jenkins.notification.camel

import org.apache.abdera.model.Entry
import spock.lang.Specification
import spock.lang.Unroll

class DateFilterTest extends Specification {

    @Unroll
    def 'Filter yields #valid for reference dates #reference, #published, #updated'(
            reference, published, updated, valid) {
        setup:
        def entry = Mock(Entry)
        entry.published >> date(published)
        entry.updated >> date(updated)
        def filter = new DateFilter(date(reference))

        expect:
        filter.isValid(entry) == valid

        where:
        reference || published || updated || valid
        1          | 2          | null     | true
        1          | 1          | null     | true
        2          | 1          | null     | false
        2          | 1          | 3        | true
        2          | 3          | 1        | false
        2          | null       | 1        | false
        2          | null       | 2        | true
        2          | null       | 3        | true
        4          | null       | null     | true
    }

    @Unroll
    def 'Filter yields #valid for the sequence #seq of millis'(seq, valid) {
        setup:
        def filter = new DateFilter(date(0))

        when:

        def lastValid = false
        seq.each { millis ->
            def entry = Mock(Entry)
            entry.updated >> date(millis * 30 * 60 * 1000)
            lastValid = filter.isValid(entry)
        }

        then:
        lastValid == valid

        where:
        seq         || valid
        [1, 2, 3]    | true
        [1, 4, 1]    | false
        [1, 5, 2, 1] | false
    }

    def date(input) {
        input == null ? null : new Date(input)
    }
}
