package com.kishlaly.ta.cache.key;

import com.kishlaly.ta.model.Timeframe;

import java.util.Objects;

public class BaseKey {

    protected String symbol;
    protected Timeframe timeframe;
    protected int period;

    public BaseKey(final String symbol, final Timeframe timeframe, int period) {
        this.symbol = symbol;
        this.timeframe = timeframe;
        this.period = period;
    }

    public BaseKey(final String symbol, final Timeframe timeframe) {
        this(symbol, timeframe, 0);
    }

    public String getSymbol() {
        return symbol;
    }

    public Timeframe getTimeframe() {
        return this.timeframe;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final BaseKey baseKey = (BaseKey) o;
        return this.period == baseKey.period && this.symbol.equals(baseKey.symbol) && this.timeframe == baseKey.timeframe;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.symbol, this.timeframe, this.period);
    }
}
