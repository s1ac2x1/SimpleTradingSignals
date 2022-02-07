package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;

import java.util.Comparator;

/**
 * SL выбирается на N центов ниже самого низкого quote.low из N столбиков перед сигнальной котировкой
 */
public class StopLossFixedPrice extends StopLossStrategy {

    public StopLossFixedPrice(Object config) {
        super(config, false);
    }

    @Override
    public double calculate(SymbolData data, int signalIndex) {
        Quote quoteWithMinimalLow = data.quotes.subList(signalIndex - 20, signalIndex).stream().min(Comparator.comparingDouble(quote -> quote.getLow())).get();
        double distance = (double) config;
        return quoteWithMinimalLow.getLow() - distance;
    }

    @Override
    public String printConfig() {
        return String.valueOf((double) config);
    }
}
