package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.KeltnerJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;

/**
 * TP at % of the top of the Keltner channel at each new point
 */
public class TakeProfitVolatileKeltnerTop extends TakeProfitStrategyJava {

    public TakeProfitVolatileKeltnerTop(Object config) {
        super(config, true);
    }

    @Override
    public double calcualte(SymbolDataJava data, int signalIndex) {
        KeltnerJava keltner = IndicatorUtilsJava.buildKeltnerChannels(data.symbol, data.quotes).get(signalIndex);
        int keltnerTopRatio = (int) getConfig();
        double middle = keltner.getMiddle();
        double top = keltner.getTop();
        double diff = top - middle;
        double ratio = diff / 100 * keltnerTopRatio;
        return middle + ratio;
    }

    @Override
    public String toString() {
        return "TP volatile " + getConfig() + "% Keltner top";
    }
}
