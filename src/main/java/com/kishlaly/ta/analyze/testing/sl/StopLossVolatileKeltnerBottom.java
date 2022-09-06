package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.KeltnerJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;

public class StopLossVolatileKeltnerBottom extends StopLossStrategy {

    public StopLossVolatileKeltnerBottom(Object config) {
        super(config, true);
    }

    @Override
    public double calculate(SymbolDataJava data, int signalIndex) {
        KeltnerJava keltner = IndicatorUtilsJava.buildKeltnerChannels(data.symbol, data.quotes).get(signalIndex);
        int bottomRatio = (int) getConfig();
        double middle = keltner.getMiddle();
        double bottom = keltner.getLow();
        double diff = middle - bottom;
        double ratio = diff / 100 * bottomRatio;
        return middle - ratio;
    }

    @Override
    public String toString() {
        return "SL volatile Keltner " + getConfig() + "% bottom";
    }
}
