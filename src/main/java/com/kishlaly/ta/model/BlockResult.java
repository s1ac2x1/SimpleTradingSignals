package com.kishlaly.ta.model;

public class BlockResult {

    private QuoteJava lastChartQuote;
    private BlockResultCode code;

    public BlockResult(final QuoteJava lastChartQuote, final BlockResultCode code) {
        this.lastChartQuote = lastChartQuote;
        this.code = code;
    }

    public QuoteJava getLastChartQuote() {
        return this.lastChartQuote;
    }

    public void setLastChartQuote(final QuoteJava lastChartQuote) {
        this.lastChartQuote = lastChartQuote;
    }

    public BlockResultCode getCode() {
        return this.code;
    }

    public boolean isOk() {
        return code == BlockResultCode.OK;
    }

}
