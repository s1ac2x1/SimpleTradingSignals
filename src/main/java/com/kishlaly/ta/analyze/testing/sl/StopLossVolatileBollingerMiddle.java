package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Bollinger;
import com.kishlaly.ta.utils.IndicatorUtils;

public class StopLossVolatileBollingerMiddle extends StopLossStrategy {

    public StopLossVolatileBollingerMiddle() {
        super(null, true);
    }

    @Override
    public double calculate(SymbolData data, int signalIndex) {
        Bollinger bollinger = IndicatorUtils.buildBollingerBands(data.symbol, data.quotes).get(signalIndex);
        return bollinger.getMiddle();
    }

    @Override
    public String toString() {
        return "SL volatile Bollinger middle";
    }
}
