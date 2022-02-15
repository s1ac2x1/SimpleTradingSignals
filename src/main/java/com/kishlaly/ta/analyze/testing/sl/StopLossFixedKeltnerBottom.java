package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.IndicatorUtils;

import java.util.List;

/**
 * SL выбирается в нижней точке канала Кельтнера, который существует в сигнальной котировке
 */
public class StopLossFixedKeltnerBottom extends StopLossStrategy {

    public StopLossFixedKeltnerBottom() {
        super(null, false);
    }

    @Override
    public double calculate(SymbolData data, int signalIndex) {
        List<Keltner> keltnerChannels = IndicatorUtils.buildKeltnerChannels(data.quotes);
        return keltnerChannels.get(signalIndex).getLow();
    }

    @Override
    public String printConfig() {
        return "";
    }
}
