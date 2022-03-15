package com.kishlaly.ta.model;

import com.kishlaly.ta.analyze.BlockResultCode;

public class BlockResult {

    private Quote lastChartQuote;
    private BlockResultCode code;

    public BlockResult(final Quote lastChartQuote, final BlockResultCode code) {
        this.lastChartQuote = lastChartQuote;
        this.code = code;
    }

    public Quote getLastChartQuote() {
        return this.lastChartQuote;
    }

    public void setLastChartQuote(final Quote lastChartQuote) {
        this.lastChartQuote = lastChartQuote;
    }

    public BlockResultCode getCode() {
        return this.code;
    }

    public boolean isOk() {
        return code == BlockResultCode.OK;
    }

}
