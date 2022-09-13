package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolDataJava;

public class TakeProfitDisabledJava extends TakeProfitStrategyJava {

    public TakeProfitDisabledJava() {
        super(null, false);
    }

    @Override
    public double calcualte(SymbolDataJava data, int signalIndex) {
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
