// File: config/codenarc/rules.groovy

ruleset {
    ruleset('rulesets/basic.xml')
    ruleset('rulesets/braces.xml')
    ruleset('rulesets/concurrency.xml')
    ruleset('rulesets/convention.xml')
    ruleset('rulesets/design.xml') {
        exclude 'PrivateFieldCouldBeFinal'
        exclude 'AbstractClassWithoutAbstractMethod'
    }
    ruleset('rulesets/dry.xml') {
        exclude 'DuplicateNumberLiteral'
    }
    ruleset('rulesets/groovyism.xml')
    ruleset('rulesets/exceptions.xml')
    ruleset('rulesets/formatting.xml') {
        exclude 'ClassJavadoc'
        SpaceAroundMapEntryColon {
            characterAfterColonRegex = /\s/
        }
    }
    ruleset('rulesets/imports.xml') {
        exclude 'NoWildcardImports'
    }
    ruleset('rulesets/logging.xml') {
        'Println' priority: 1
        'PrintStackTrace' priority: 1
    }
    ruleset('rulesets/naming.xml')
    ruleset('rulesets/size.xml') {
        exclude 'CrapMetric'
    }
    ruleset('rulesets/serialization.xml')
    ruleset('rulesets/unnecessary.xml')
    ruleset('rulesets/unused.xml')
}
