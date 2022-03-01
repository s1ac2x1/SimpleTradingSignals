package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;

import java.util.Comparator;

/**
 * SL выбирается на N ниже самого низкого quote.low из 20 столбиков перед текущей котировкой
 */
public class StopLossVolatileLocalMin extends StopLossStrategy {

    public static final int QUOTES_TO_FIND_MIN = 20;

    public StopLossVolatileLocalMin(Object config) {
        super(config, true);
    }

    @Override
    public double calculate(SymbolData data, int currentQuoteIndex) {
        Quote quoteWithMinimalLow = data.quotes.subList(currentQuoteIndex - QUOTES_TO_FIND_MIN, currentQuoteIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        double distance = (double) config;
        return quoteWithMinimalLow.getLow() - distance;
    }

    @Override
    public String toString() {
        return "SL volatile local min";
    }
}
