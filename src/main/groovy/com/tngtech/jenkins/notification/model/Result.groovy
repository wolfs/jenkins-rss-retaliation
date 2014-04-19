package com.tngtech.jenkins.notification.model

enum Result {
    SUCCESS(0), UNSTABLE(1), FAILURE(2), NOT_BUILT(3), ABORTED(4)

    int ordinal;

    private Result(int ordinal) {
        this.ordinal = ordinal;
    }

    static fromString(String result) {
        String upperCaseResult = result.toUpperCase()
        if (values().collect { it.name() }.contains(upperCaseResult)) {
            return valueOf(upperCaseResult)
        } else {
            return null
        }
    }
}
