package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Keltner;

import java.util.function.BiFunction;

public enum TakeProfitStrategy {

    KELTNER(TakeProfitStrategy::calculateFromKeltner, 100);

    private BiFunction<SymbolData, Integer, Double> calculation;
    private Object config;

    TakeProfitStrategy(final BiFunction<SymbolData, Integer, Double> calculation, Object config) {
        this.calculation = calculation;
        this.config = config;
    }

    public double calcualte(SymbolData data, int signalIndex) {
        return calculation.apply(data, signalIndex);
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public String printConfig() {
        switch (this) {
            case KELTNER:
                return (int) config + "% of top";
            default:
                return "";
        }
    }

    private static double calculateFromKeltner(SymbolData data, int signalIndex) {
        Keltner keltner = (Keltner) data.indicators.get(Indicator.KELTNER).get(signalIndex);
        int keltnerTopRatio = (int) KELTNER.config;
        return keltner.getTop() / 100 * keltnerTopRatio;
    }

}
