package com.kishlaly.ta.model.indicators;

public class Keltner {

    // in epoch seconds
    private Long timestamp;
    private double low;
    private double middle;
    private double top;

    public Keltner(final Long timestamp, final double low, final double middle, final double top) {
        this.timestamp = timestamp;
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
}
