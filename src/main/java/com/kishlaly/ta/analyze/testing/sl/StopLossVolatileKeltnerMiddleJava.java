package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.KeltnerJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;

public class StopLossVolatileKeltnerMiddleJava extends StopLossStrategyJava {

    public StopLossVolatileKeltnerMiddleJava() {
        super(null, true);
    }

    @Override
    public double calculate(SymbolDataJava data, int signalIndex) {
        KeltnerJava keltner = IndicatorUtilsJava.buildKeltnerChannels(data.symbol, data.quotes).get(signalIndex);
        return keltner.getMiddle();
    }

    @Override
    public String toString() {
        return "SL volatile Keltner middle";
    }
}
