package com.kishlaly.ta.model;

/**
 * @author Vladimir Kishlaly
 * @since 16.11.2021
 */
public class HistogramQuote {

    public double histogramValue;
    public QuoteJava quote;

    public HistogramQuote(final double histogramValue, final QuoteJava quote) {
        this.histogramValue = histogramValue;
        this.quote = quote;
    }

    public double getHistogramValue() {
        return this.histogramValue;
    }

    public void setHistogramValue(final double histogramValue) {
        this.histogramValue = histogramValue;
    }

    public QuoteJava getQuote() {
        return this.quote;
    }

    public void setQuote(final QuoteJava quote) {
        this.quote = quote;
    }
}
