package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;

import java.util.Comparator;
import java.util.function.BiFunction;

public enum StopLossStrategy {

    FIXED(StopLossStrategy::calculateWithFixedPrice, 0.27);

    private BiFunction<SymbolData, Integer, Double> calculation;
    private Object config;

    StopLossStrategy(final BiFunction<SymbolData, Integer, Double> calculation, Object config) {
        this.calculation = calculation;
        this.config = config;
    }

    public double calculate(SymbolData data, int signalIndex) {
        return calculation.apply(data, signalIndex);
    }

    public String printConfig() {
        switch (this) {
            case FIXED:
                return String.valueOf((double) config);
            default:
                return "";
        }
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    // SL выбирается на N центов ниже самого низкого quote.low из десяти столбиков перед сигнальной котировкой
    private static double calculateWithFixedPrice(SymbolData data, int signalIndex) {
        Quote quoteWithMinimalLow = data.quotes.subList(signalIndex - 10, signalIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        double distance = (double) FIXED.config;
        return quoteWithMinimalLow.getLow() - distance;
    }

}
