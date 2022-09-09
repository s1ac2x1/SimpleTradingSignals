package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;

import java.util.Comparator;

/**
 * SL is selected by {config} below the lowest quote.low of the {QUOTES_TO_FIND_MIN} bars before the current quote
 */
public class StopLossVolatileLocalMin extends StopLossStrategyJava {

    public static int QUOTES_TO_FIND_MIN = 20;

    public StopLossVolatileLocalMin(Object config) {
        super(config, true);
    }

    @Override
    public double calculate(SymbolDataJava data, int currentQuoteIndex) {
        QuoteJava quoteWithMinimalLow = data.quotes.subList(currentQuoteIndex - QUOTES_TO_FIND_MIN, currentQuoteIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        double distance = (double) config;
        return quoteWithMinimalLow.getLow() - distance;
    }

    @Override
    public String toString() {
        return "SL volatile local min";
    }
}
