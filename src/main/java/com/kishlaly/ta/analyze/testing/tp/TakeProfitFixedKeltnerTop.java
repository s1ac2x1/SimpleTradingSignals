package com.kishlaly.ta.analyze.testing.tp;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.IndicatorUtils;

/**
 * TP на уровне % от середины до вершины канала Кельтнера
 */
public class TakeProfitFixedKeltnerTop extends TakeProfitStrategy {

    public TakeProfitFixedKeltnerTop(Object config) {
        super(config, false);
    }

    @Override
    public double calcualte(SymbolData data, int signalIndex) {
        Keltner keltner = IndicatorUtils.buildKeltnerChannels(data.symbol, data.quotes).get(signalIndex);
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
