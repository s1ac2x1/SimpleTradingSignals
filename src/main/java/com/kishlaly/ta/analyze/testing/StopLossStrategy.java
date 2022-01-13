package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;

import java.util.Comparator;
import java.util.function.BiFunction;

public enum StopLossStrategy {

    FIXED(StopLossStrategy::calculateWithFixedPrice);

    public static double distance = 0.27;

    private BiFunction<SymbolData, Integer, Double> calculation;

    StopLossStrategy(final BiFunction<SymbolData, Integer, Double> calculation) {
        this.calculation = calculation;
    }

    public double calculate(SymbolData data, int signalIndex) {
        return calculation.apply(data, signalIndex);
    }

    // SL выбирается на N центов ниже самого низкого quote.low из десяти столбиков перед сигнальной котировкой
    private static double calculateWithFixedPrice(SymbolData data, int signalIndex) {
        Quote quoteWithMinimalLow = data.quotes.subList(signalIndex - 10, signalIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        return quoteWithMinimalLow.getLow() - distance;
    }
}
