package com.kishlaly.ta.analyze.testing;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.ATR;
import com.kishlaly.ta.utils.IndicatorUtils;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public enum StopLossStrategy {

    FIXED(StopLossStrategy::calculateWithFixedPrice, 0.27, false),
    VOLATILE_ATR(StopLossStrategy::calculateVolativeATR, null, true); // двигается вниз !!!

    private BiFunction<SymbolData, Integer, Double> calculation;
    private Object config;
    private boolean isVolatile;

    StopLossStrategy(final BiFunction<SymbolData, Integer, Double> calculation, Object config, final boolean isVolatile) {
        this.calculation = calculation;
        this.config = config;
        this.isVolatile = isVolatile;
    }

    public double calculate(SymbolData data, int signalIndex) {
        return calculation.apply(data, signalIndex);
    }

    public String printConfig() {
        switch (this) {
            case FIXED:
                return String.valueOf((double) config);
            case VOLATILE_ATR:
                return "";
            default:
                return "";
        }
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public boolean isVolatile() {
        return this.isVolatile;
    }

    // SL выбирается на N центов ниже самого низкого quote.low из десяти столбиков перед сигнальной котировкой
    private static double calculateWithFixedPrice(SymbolData data, int signalIndex) {
        Quote quoteWithMinimalLow = data.quotes.subList(signalIndex - 10, signalIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        double distance = (double) FIXED.config;
        return quoteWithMinimalLow.getLow() - distance;
    }

    // SL = Current low – (2 × ATR)
    private static double calculateVolativeATR(SymbolData data, int signalIndex) {
        Quote signal = data.quotes.get(signalIndex);
        List<ATR> atrs = IndicatorUtils.buildATR(data.quotes, 22);
        return signal.getLow() - (2 * atrs.get(signalIndex).getValue());
    }

}
