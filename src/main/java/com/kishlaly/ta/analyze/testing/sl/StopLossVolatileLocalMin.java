package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;

import java.util.Comparator;

/**
 * SL выбирается на N центов ниже самого низкого quote.low из N столбиков перед текущей котировкой
 */
public class StopLossVolatileLocalMin extends StopLossStrategy {

    public StopLossVolatileLocalMin(Object config) {
        super(config, true);
    }

    @Override
    public double calculate(SymbolData data, int currentQuoteIndex) {
        Quote quoteWithMinimalLow = data.quotes.subList(currentQuoteIndex - 5, currentQuoteIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        double distance = (double) config;
        return quoteWithMinimalLow.getLow() - distance;
    }

    @Override
    public String printConfig() {
        return String.valueOf((double) config);
    }
}
