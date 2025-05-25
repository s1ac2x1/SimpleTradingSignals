package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.EntityWithDate;

public class ADX extends EntityWithDate {
    private final double value;

    public ADX(final Long timestamp, final Double value) {
        super(timestamp);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public boolean valuesPresent() {
        return !Double.isNaN(value);
    }

}
