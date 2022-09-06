package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolDataJava;

public abstract class TakeProfitStrategy {

    protected Object config;
    protected boolean enabled = true;
    protected boolean isVolatile;

    public TakeProfitStrategy(final Object config, final boolean isVolatile) {
        this.config = config;
        this.isVolatile = isVolatile;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public Object getConfig() {
        return this.config;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public abstract double calcualte(SymbolDataJava data, int signalIndex);

    public boolean isVolatile() {
        return this.isVolatile;
    }

    public abstract String toString();

}
