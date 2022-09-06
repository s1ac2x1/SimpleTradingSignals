package com.kishlaly.ta.cache.key;

import com.kishlaly.ta.model.TimeframeJava;

import java.util.Objects;

public class BaseKeyJava {

    protected String symbol;
    protected TimeframeJava timeframe;
    protected int period;

    public BaseKeyJava(final String symbol, final TimeframeJava timeframe, int period) {
        this.symbol = symbol;
        this.timeframe = timeframe;
        this.period = period;
    }

    public BaseKeyJava(final String symbol, final TimeframeJava timeframe) {
        this(symbol, timeframe, 0);
    }

    public String getSymbol() {
        return symbol;
    }

    public TimeframeJava getTimeframe() {
        return this.timeframe;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final BaseKeyJava baseKey = (BaseKeyJava) o;
        return this.period == baseKey.period && this.symbol.equals(baseKey.symbol) && this.timeframe == baseKey.timeframe;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.symbol, this.timeframe, this.period);
    }
}
