package com.kishlaly.ta.model;

import com.kishlaly.ta.analyze.TaskResultCode;

public class TaskResult {

    private Quote lastChartQuote;
    private TaskResultCode code;

    public TaskResult(final Quote lastChartQuote, final TaskResultCode code) {
        this.lastChartQuote = lastChartQuote;
        this.code = code;
    }

    public Quote getLastChartQuote() {
        return this.lastChartQuote;
    }

    public TaskResultCode getCode() {
        return this.code;
    }

    public boolean isSignal() {
        return code == TaskResultCode.SIGNAL;
    }

}
