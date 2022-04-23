package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.EntityWithDate;

import java.time.ZonedDateTime;

public class ElderForceIndex extends EntityWithDate {

    private double value;

    public ElderForceIndex(final Long timestamp, final double value) {
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

    public static String getExchangeTimezome() {
        return EntityWithDate.exchangeTimezome;
    }

    public static void setExchangeTimezome(final String exchangeTimezome) {
        EntityWithDate.exchangeTimezome = exchangeTimezome;
    }

    public ZonedDateTime getNativeDate() {
        return this.nativeDate;
    }

    public void setNativeDate(final ZonedDateTime nativeDate) {
        this.nativeDate = nativeDate;
    }

    public ZonedDateTime getMyDate() {
        return this.myDate;
    }

    public void setMyDate(final ZonedDateTime myDate) {
        this.myDate = myDate;
    }

}
