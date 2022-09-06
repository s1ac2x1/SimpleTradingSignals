package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;

import java.util.Comparator;

/**
 * SL is selected by {config} below the lowest quote.low of the {LAST_QUOTES_TO_FIND_MIN} bars before the signal quote
 */
public class StopLossFixedPrice extends StopLossStrategy {

    public static int LAST_QUOTES_TO_FIND_MIN = 20;

    public StopLossFixedPrice(Object config) {
        super(config, false);
    }

    @Override
    public double calculate(SymbolData data, int signalIndex) {
        QuoteJava quoteWithMinimalLow = data.quotes.subList(signalIndex - LAST_QUOTES_TO_FIND_MIN, signalIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        double distance = (double) config;
        return quoteWithMinimalLow.getLow() - distance;
    }

    @Override
    public String toString() {
        return "SL [Fixed] price " + (double) config;
    }
}
