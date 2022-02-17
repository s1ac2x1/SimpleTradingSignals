package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolData;

public abstract class StopLossStrategy {

    protected Object config;
    protected boolean isVolatile;

    public StopLossStrategy(final Object config, boolean isVolatile) {
        this.config = config;
        this.isVolatile = isVolatile;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public Object getConfig() {
        return this.config;
    }

    public boolean isVolatile() {
        return this.isVolatile;
    }

    public abstract double calculate(SymbolData data, int signalIndex);

    public abstract String printConfig();

    public String toString() {
        return getClass().getSimpleName() + ", " + printConfig();
    }

}
