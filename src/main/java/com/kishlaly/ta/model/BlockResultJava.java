package com.kishlaly.ta.model;

public class BlockResultJava {

    private QuoteJava lastChartQuote;
    private BlockResultCodeJava code;

    public BlockResultJava(final QuoteJava lastChartQuote, final BlockResultCodeJava code) {
        this.lastChartQuote = lastChartQuote;
        this.code = code;
    }

    public QuoteJava getLastChartQuote() {
        return this.lastChartQuote;
    }

    public void setLastChartQuote(final QuoteJava lastChartQuote) {
        this.lastChartQuote = lastChartQuote;
    }

    public BlockResultCodeJava getCode() {
        return this.code;
    }

    public boolean isOk() {
        return code == BlockResultCodeJava.OK;
    }

}
