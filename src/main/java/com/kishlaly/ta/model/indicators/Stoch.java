package com.kishlaly.ta.model.indicators;

import com.kishlaly.ta.model.EntityWithDate;

public class Stoch extends EntityWithDate {
    private Double slowD;
    private Double slowK;

    public Stoch(final Long timestamp, final Double slowD, final Double slowK) {
        super(timestamp);
        this.slowD = slowD;
        this.slowK = slowK;
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

    public boolean valuesPresent() {
        return !Double.isNaN(slowD) && !Double.isNaN(slowK);
    }

}
