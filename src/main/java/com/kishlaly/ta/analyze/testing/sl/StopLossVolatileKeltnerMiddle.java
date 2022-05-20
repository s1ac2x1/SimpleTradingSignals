package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.IndicatorUtils;

public class StopLossVolatileKeltnerMiddle extends StopLossStrategy {

    public StopLossVolatileKeltnerMiddle() {
        super(null, true);
    }

    @Override
    public double calculate(SymbolData data, int signalIndex) {
        Keltner keltner = IndicatorUtils.buildKeltnerChannels(data.symbol, data.quotes).get(signalIndex);
        return keltner.getMiddle();
    }

    @Override
    public String toString() {
        return "SL volatile Keltner middle";
    }
}
