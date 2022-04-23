package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.EntityWithDate;

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
}
