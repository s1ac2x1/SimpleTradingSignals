package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Keltner;

import java.util.function.BiFunction;

public enum TakeProfitStrategy {

    KELTNER(TakeProfitStrategy::calculateFromKeltner);

    public static int keltnerTopRatio = 100; // % от значения верхнего канала

    private BiFunction<SymbolData, Integer, Double> calculation;

    TakeProfitStrategy(final BiFunction<SymbolData, Integer, Double> calculation) {
        this.calculation = calculation;
    }

    public double calcualte(SymbolData data, int signalIndex) {
        return calculation.apply(data, signalIndex);
    }

    private static double calculateFromKeltner(SymbolData data, int signalIndex) {
        Keltner keltner = (Keltner) data.indicators.get(Indicator.KELTNER).get(signalIndex);
        return keltner.getTop() / 100 * keltnerTopRatio;
    }
}
