package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.IndicatorUtils;

public class StopLossVolatileKeltnerBottom extends StopLossStrategy {

    public StopLossVolatileKeltnerBottom(Object config) {
        super(config, true);
    }

    @Override
    public double calculate(SymbolData data, int signalIndex) {
        Keltner keltner = IndicatorUtils.buildKeltnerChannels(data.symbol, data.quotes).get(signalIndex);
        int bottomRatio = (int) getConfig();
        double middle = keltner.getMiddle();
        double bottom = keltner.getLow();
        double diff = middle - bottom;
        double ratio = diff / 100 * bottomRatio;
        return middle - ratio;
    }

    @Override
    public String printConfig() {
        return (int) getConfig() + "% of top";
    }
}
