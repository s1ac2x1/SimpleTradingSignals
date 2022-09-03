package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.AbstractModel;

public class MACD extends AbstractModel {

    private Double macd;
    private Double signal;
    private Double histogram;

    public MACD(final Long timestamp, final Double macd, final Double signal, final Double histogram) {
        super(timestamp);
        this.macd = macd;
        this.signal = signal;
        this.histogram = histogram;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getMacd() {
        return this.macd;
    }

    public void setMacd(final Double macd) {
        this.macd = macd;
    }

    public Double getSignal() {
        return this.signal;
    }

    public void setSignal(final Double signal) {
        this.signal = signal;
    }

    public Double getHistogram() {
        return this.histogram;
    }

    public void setHistogram(final Double histogram) {
        this.histogram = histogram;
    }

    public String getNativeDate() {
        return this.nativeDate;
    }

    public void setNativeDate(final String nativeDate) {
        this.nativeDate = nativeDate;
    }

    public String getMyDate() {
        return this.myDate;
    }

    public void setMyDate(final String myDate) {
        this.myDate = myDate;
    }

    public boolean valuesPresent() {
        return !Double.isNaN(macd) && !Double.isNaN(signal) && !Double.isNaN(histogram);
    }
}
