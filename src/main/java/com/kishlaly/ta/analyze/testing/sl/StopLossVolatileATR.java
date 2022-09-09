package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.ATRJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;

import java.util.List;

/**
 * SL = Current low – (2 × ATR)
 */
public class StopLossVolatileATR extends StopLossStrategyJava {

    public StopLossVolatileATR() {
        super(null, true);
    }

    @Override
    public double calculate(SymbolDataJava data, int currentQuoteIndex) {
        QuoteJava signal = data.quotes.get(currentQuoteIndex);
        List<ATRJava> atrs = IndicatorUtilsJava.buildATR(data.symbol, data.quotes, 22);
        return signal.getLow() - (2 * atrs.get(currentQuoteIndex).getValue());
    }

    @Override
    public String toString() {
        return "SL volatile ATR";
    }
}
