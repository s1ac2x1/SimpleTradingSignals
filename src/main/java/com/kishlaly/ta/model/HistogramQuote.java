package com.kishlaly.ta.model;

/**
 * @author Vladimir Kishlaly
 * @since 16.11.2021
 */
public class HistogramQuote {

    public double histogramValue;
    public Quote quote;

    public HistogramQuote(final double histogramValue, final Quote quote) {
        this.histogramValue = histogramValue;
        this.quote = quote;
    }

    public double getHistogramValue() {
        return this.histogramValue;
    }

    public void setHistogramValue(final double histogramValue) {
        this.histogramValue = histogramValue;
    }

    public Quote getQuote() {
        return this.quote;
    }

    public void setQuote(final Quote quote) {
        this.quote = quote;
    }
}
