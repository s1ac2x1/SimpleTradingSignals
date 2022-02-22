package com.kishlaly.ta.model.indicators;

public class ATR {

    // in epoch seconds
    private Long timestamp;

    private double value;

    public ATR(final Long timestamp, final double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public double getValue() {
        return this.value;
    }

    public boolean valuesPresent() {
        return !Double.isNaN(value);
    }

}
