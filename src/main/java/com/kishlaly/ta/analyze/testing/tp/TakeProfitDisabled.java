package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolData;

public class TakeProfitDisabled extends TakeProfitStrategy {

    public TakeProfitDisabled() {
        super(null, false);
    }

    @Override
    public double calcualte(SymbolData data, int signalIndex) {
        return -1;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String toString() {
        return "TP disabled";
    }
}
