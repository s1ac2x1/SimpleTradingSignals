package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.ATR;
import com.kishlaly.ta.utils.IndicatorUtils;

import java.util.List;

/**
 * SL = Current low – (2 × ATR)
 */
public class StopLossVolatileATR extends StopLossStrategy {

    public StopLossVolatileATR() {
        super(null, true);
    }

    @Override
    public double calculate(SymbolData data, int currentQuoteIndex) {
        Quote signal = data.quotes.get(currentQuoteIndex);
        List<ATR> atrs = IndicatorUtils.buildATR(data.symbol, data.quotes, 22);
        return signal.getLow() - (2 * atrs.get(currentQuoteIndex).getValue());
    }

    @Override
    public String toString() {
        return "SL volatile ATR";
    }
}
