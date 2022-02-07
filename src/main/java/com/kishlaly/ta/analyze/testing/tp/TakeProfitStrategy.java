package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolData;

public abstract class TakeProfitStrategy {

    private Object config;

    public TakeProfitStrategy(final Object config) {
        this.config = config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public Object getConfig() {
        return this.config;
    }

    public abstract double calcualte(SymbolData data, int signalIndex);

    public abstract String printConfig();

}
