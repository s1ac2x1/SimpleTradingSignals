package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.BollingerJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;

public class StopLossVolatileBollingerMiddle extends StopLossStrategy {

    public StopLossVolatileBollingerMiddle() {
        super(null, true);
    }

    @Override
    public double calculate(SymbolDataJava data, int signalIndex) {
        BollingerJava bollinger = IndicatorUtilsJava.buildBollingerBands(data.symbol, data.quotes).get(signalIndex);
        return bollinger.getMiddle();
    }

    @Override
    public String toString() {
        return "SL volatile Bollinger middle";
    }
}
