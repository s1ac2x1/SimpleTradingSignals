package com.kishlaly.ta.model;

import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

public abstract class AbstractModel {

    // in epoch seconds
    protected Long timestamp;

    public static String exchangeTimezome = "US/Eastern";
    protected String nativeDate;
    protected String myDate;

    public AbstractModel(final Long timestamp) {
        this.timestamp = timestamp;
        this.nativeDate = getTimeInExchangeZone(timestamp, exchangeTimezome).toString();
        this.myDate = getBarTimeInMyZone(timestamp, exchangeTimezome).toString();
    }

    public Long getTimestamp() {
        return this.timestamp;
    }
}
