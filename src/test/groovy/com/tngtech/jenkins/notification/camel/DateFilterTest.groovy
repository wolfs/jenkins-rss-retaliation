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

    def date(input) {
        input == null ? null : new Date(input)
    }
}
