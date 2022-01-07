package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.utils.Numbers;

import static com.kishlaly.ta.model.Quote.exchangeTimezome;
import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

public class Stoch {
    // in epoch seconds
    private Long timestamp;
    private String nativeDate;
    private String myDate;

    private Double slowD;
    private Double slowK;

    public Stoch() {
    }

    public Stoch(final Long timestamp, final Double slowD, final Double slowK) {
        this.timestamp = timestamp;
        this.slowD = Numbers.round(slowD);
        this.slowK = Numbers.round(slowK);
        this.nativeDate = getTimeInExchangeZone(timestamp, exchangeTimezome).toString();
        this.myDate = getBarTimeInMyZone(timestamp, exchangeTimezome).toString();
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getSlowD() {
        return this.slowD;
    }

    public void setSlowD(final Double slowD) {
        this.slowD = slowD;
    }

    public Double getSlowK() {
        return this.slowK;
    }

    public void setSlowK(final Double slowK) {
        this.slowK = slowK;
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
