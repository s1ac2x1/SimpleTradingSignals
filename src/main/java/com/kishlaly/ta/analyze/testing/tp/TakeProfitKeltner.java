package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.Keltner;

public class TakeProfitKeltner extends TakeProfitStrategy {

    public TakeProfitKeltner(Object config) {
        super(config);
    }

    @Override
    public double calcualte(SymbolData data, int signalIndex) {
        Keltner keltner = (Keltner) data.indicators.get(Indicator.KELTNER).get(signalIndex);
        int keltnerTopRatio = (int) getConfig();
        double middle = keltner.getMiddle();
        double top = keltner.getTop();
        double diff = top - middle;
        double ratio = diff / 100 * keltnerTopRatio;
        return middle + ratio;
    }

    @Override
    public String printConfig() {
        return (int) getConfig() + "% of top";
    }
}
