package com.kishlaly.ta.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

public abstract class EntityWithDate implements Serializable {

    // in epoch seconds
    protected Long timestamp;

    public static String exchangeTimezome = "US/Eastern";
    public ZonedDateTime nativeDate;
    public ZonedDateTime myDate;

    public EntityWithDate(final Long timestamp) {
        this.timestamp = timestamp;
        this.nativeDate = getTimeInExchangeZone(timestamp, exchangeTimezome);
        this.myDate = getBarTimeInMyZone(timestamp, exchangeTimezome);
    }

}
