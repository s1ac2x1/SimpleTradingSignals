package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.AbstractModelJava;

import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

public class Stoch extends AbstractModelJava {
    private Double slowD;
    private Double slowK;

    public Stoch(final Long timestamp, final Double slowD, final Double slowK) {
        super(timestamp);
        this.slowD = slowD;
        this.slowK = slowK;
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

    public boolean valuesPresent() {
        return !Double.isNaN(slowD) && !Double.isNaN(slowK);
    }

}
