package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.utils.Numbers;

import static com.kishlaly.ta.model.Quote.exchangeTimezome;
import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;
import static com.kishlaly.ta.utils.Numbers.round;

public class MACD {
    // in epoch seconds
    private Long timestamp;
    private String nativeDate;
    private String myDate;

    private Double macd;
    private Double signal;
    private Double histogram;

    public MACD() {
    }

    public MACD(final Long timestamp, final Double macd, final Double signal, final Double histogram) {
        this.timestamp = timestamp;
        this.macd = macd;
        this.signal = signal;
        this.histogram = Numbers.round(histogram);
        this.nativeDate = getTimeInExchangeZone(timestamp, exchangeTimezome).toString();
        this.myDate = getBarTimeInMyZone(timestamp, exchangeTimezome).toString();
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
}
