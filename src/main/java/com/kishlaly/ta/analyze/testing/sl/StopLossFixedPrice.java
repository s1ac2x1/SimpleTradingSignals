package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;

import java.util.Comparator;

/**
 * SL выбирается на N ниже самого низкого quote.low из LAST_QUOTES_TO_FIND_MIN столбиков перед сигнальной котировкой
 */
public class StopLossFixedPrice extends StopLossStrategy {

    private static int LAST_QUOTES_TO_FIND_MIN = 20;

    public StopLossFixedPrice(Object config) {
        super(config, false);
    }

    @Override
    public double calculate(SymbolData data, int signalIndex) {
        Quote quoteWithMinimalLow = data.quotes.subList(signalIndex - LAST_QUOTES_TO_FIND_MIN, signalIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        double distance = (double) config;
        return quoteWithMinimalLow.getLow() - distance;
    }

    @Override
    public String printConfig() {
        return String.valueOf((double) config);
    }
}
