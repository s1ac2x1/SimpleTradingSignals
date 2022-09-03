package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.AbstractModelJava;

public class ATR extends AbstractModelJava {

    private double value;

    public ATR(final Long timestamp, final double value) {
        super(timestamp);
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
