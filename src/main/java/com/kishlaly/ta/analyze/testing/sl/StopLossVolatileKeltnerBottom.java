package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.IndicatorUtils;

import java.util.List;

public class StopLossVolatileKeltnerBottom extends StopLossStrategy {

    public StopLossVolatileKeltnerBottom() {
        super(null, true);
    }

    @Override
    public double calculate(SymbolData data, int currentQuoteIndex) {
        List<Keltner> keltnerChannels = IndicatorUtils.buildKeltnerChannels(data.quotes);
        return keltnerChannels.get(currentQuoteIndex).getLow();
    }

    @Override
    public String printConfig() {
        return "";
    }
}
