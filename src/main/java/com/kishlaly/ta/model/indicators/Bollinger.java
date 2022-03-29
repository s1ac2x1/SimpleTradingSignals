package com.kishlaly.ta.model.indicators;

public class Bollinger {

    private Long timestamp;

    private double bottom;
    private double middle;
    private double top;

    public Bollinger() {
    }

    public Bollinger(final Long timestamp, final double bottom, final double middle, final double top) {
        this.timestamp = timestamp;
        this.bottom = bottom;
        this.middle = middle;
        this.top = top;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public double getBottom() {
        return this.bottom;
    }

    public void setBottom(final double bottom) {
        this.bottom = bottom;
    }

    public double getMiddle() {
        return this.middle;
    }

    public void setMiddle(final double middle) {
        this.middle = middle;
    }

    public double getTop() {
        return this.top;
    }

    public void setTop(final double top) {
        this.top = top;
    }
}