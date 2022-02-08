package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolData;

public class TakeProfitDisabled extends TakeProfitStrategy {

    public TakeProfitDisabled() {
        super(null);
    }

    @Override
    public double calcualte(SymbolData data, int signalIndex) {
        return -1;
    }

    @Override
    public String printConfig() {
        return "";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
