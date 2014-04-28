// File: config/codenarc/rules.groovy

ruleset {
    ruleset('rulesets/basic.xml')
    ruleset('rulesets/braces.xml')
    ruleset('rulesets/exceptions.xml')
    ruleset('rulesets/imports.xml')
    ruleset('rulesets/logging.xml') {
        'Println' priority: 1
        'PrintStackTrace' priority: 1
    }
    ruleset('rulesets/naming.xml')
    ruleset('rulesets/unnecessary.xml')
    ruleset('rulesets/unused.xml')
}
