package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.KeltnerJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;

/**
 * TP at % level from the middle to the top of the Keltner channel
 */
public class TakeProfitFixedKeltnerTopJava extends TakeProfitStrategyJava {

    public TakeProfitFixedKeltnerTopJava(Object config) {
        super(config, false);
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
        return "TP [Fixed] Keltner " + getConfig() + "% top ";
    }
}
