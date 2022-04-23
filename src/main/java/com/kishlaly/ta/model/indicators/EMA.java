package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.EntityWithDate;

import java.time.ZonedDateTime;

import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

public class EMA extends EntityWithDate {

    private Double value;

    public EMA(final Long timestamp, final Double value) {
        super(timestamp);
        this.value = value;
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

    public void setValue(final Double value) {
        this.value = value;
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
