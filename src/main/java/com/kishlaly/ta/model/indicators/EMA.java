package com.kishlaly.ta.model.indicators;

import static com.kishlaly.ta.model.Quote.exchangeTimezome;
import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

public class EMA {
    // in epoch seconds
    private Long timestamp;
    private String nativeDate;
    private String myDate;

    private Double value;

    public EMA() {
    }

    public EMA(final Long timestamp, final Double value) {
        this.timestamp = timestamp;
        this.value = value;
        this.nativeDate = getTimeInExchangeZone(timestamp, exchangeTimezome).toString();
        this.myDate = getBarTimeInMyZone(timestamp, exchangeTimezome).toString();
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public Double getValue() {
        return this.value;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
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

    public void setValue(final Double value) {
        this.value = value;
    }

    public boolean valuesPresent() {
        return !Double.isNaN(value);
    }
}
