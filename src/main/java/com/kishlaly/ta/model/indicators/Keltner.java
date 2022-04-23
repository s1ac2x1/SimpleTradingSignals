package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.EntityWithDate;

import java.time.ZonedDateTime;

public class Keltner extends EntityWithDate {

    private double low;
    private double middle;
    private double top;

    public Keltner(final Long timestamp, final double low, final double middle, final double top) {
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
