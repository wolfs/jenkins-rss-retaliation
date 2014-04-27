package com.tngtech.jenkins.notification.model

enum Result {
    SUCCESS(0), UNSTABLE(1), FAILURE(2), NOT_BUILT(3), ABORTED(4)

    final int ordinal

    private Result(int ordinal) {
        this.ordinal = ordinal
    }

    static fromString(String result) {
        String upperCaseResult = result?.toUpperCase()
        values()*.name().contains(upperCaseResult) ?
            valueOf(upperCaseResult) : null
    }
}
