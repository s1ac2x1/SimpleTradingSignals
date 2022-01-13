package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.model.Quote;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public enum StopLossStrategy {

    // SL выбирается на N центов ниже самого низкого quote.low из десяти столбиков перед сигнальной котировкой
    FIXED(StopLossStrategy::calculateWithFixedPrice);

    public static double distance = 0.27;

    private BiFunction<List<Quote>, Integer, Double> calculation;

    StopLossStrategy(final BiFunction<List<Quote>, Integer, Double> calculation) {
        this.calculation = calculation;
    }

    public double calculate(List<Quote> quotes, int signalIndex) {
        return calculation.apply(quotes, signalIndex);
    }

    private static double calculateWithFixedPrice(List<Quote> quotes, int signalIndex) {
        Quote quoteWithMinimalLow = quotes.subList(signalIndex - 10, signalIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        return quoteWithMinimalLow.getLow() - distance;
    }
}
