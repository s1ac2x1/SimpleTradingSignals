package com.kishlaly.ta.model;

import java.time.ZonedDateTime;

import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

public abstract class EntityWithDate {

    // in epoch seconds
    protected Long timestamp;

    public static String exchangeTimezome = "US/Eastern";
    protected ZonedDateTime nativeDate;
    protected ZonedDateTime myDate;

    public EntityWithDate(final Long timestamp) {
        this.timestamp = timestamp;
        this.nativeDate = getTimeInExchangeZone(timestamp, exchangeTimezome);
        this.myDate = getBarTimeInMyZone(timestamp, exchangeTimezome);
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
