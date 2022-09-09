package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolDataJava;

public abstract class StopLossStrategyJava {

    protected Object config;
    protected boolean isVolatile;

    public StopLossStrategyJava(final Object config, boolean isVolatile) {
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

    public abstract double calculate(SymbolDataJava data, int signalIndex);

    public abstract String toString();

}
