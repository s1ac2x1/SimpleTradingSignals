package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.AbstractModelJava;

public class KeltnerJava extends AbstractModelJava {

    private double low;
    private double middle;
    private double top;

    public KeltnerJava(final Long timestamp, final double low, final double middle, final double top) {
        super(timestamp);
        this.low = low;
        this.middle = middle;
        this.top = top;
    }

    public double getLow() {
        return this.low;
    }

    public double getMiddle() {
        return this.middle;
    }

    public double getTop() {
        return this.top;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public boolean valuesPresent() {
        return !Double.isNaN(low) && !Double.isNaN(middle) && !Double.isNaN(top);
    }

}
