// File: config/codenarc/Testrules.groovy

ruleset {
    ruleset('file:config/codenarc/rules.groovy') {
        exclude 'MethodName'
        exclude 'Println'
        exclude 'FactoryMethodName'
    }
}

