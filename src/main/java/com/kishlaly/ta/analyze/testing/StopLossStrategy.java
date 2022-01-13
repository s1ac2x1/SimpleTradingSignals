package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.model.Quote;

import java.util.List;
import java.util.function.BiFunction;

public enum StopLossStrategy {

    FIXED(StopLossStrategy::calculateWithFixedPrice, 0.27);

    private BiFunction<List<Quote>, Integer, Double> calculation;
    private double distance;

    StopLossStrategy(final BiFunction<List<Quote>, Integer, Double> calculation, final double distance) {
        this.calculation = calculation;
        this.distance = distance;
    }

    private static double calculateWithFixedPrice(List<Quote> quotes, int signalIndex) {
        return 0;
    }
}
