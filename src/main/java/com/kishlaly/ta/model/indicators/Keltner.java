package com.kishlaly.ta.model.indicators;

public class Keltner {

    private double low;
    private double middle;
    private double top;

    public Keltner(final double low, final double middle, final double top) {
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
}
