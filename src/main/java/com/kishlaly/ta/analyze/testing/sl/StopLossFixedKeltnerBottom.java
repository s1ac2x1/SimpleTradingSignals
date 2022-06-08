package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Keltner;
import com.kishlaly.ta.utils.IndicatorUtils;

import java.util.List;

/**
 * SL is chosen at the lower point of the Keltner channel, which exists in the signal quote
 */
public class StopLossFixedKeltnerBottom extends StopLossStrategy {

    public StopLossFixedKeltnerBottom() {
        super(null, false);
    }

    @Override
    public double calculate(SymbolData data, int signalIndex) {
        List<Keltner> keltnerChannels = IndicatorUtils.buildKeltnerChannels(data.symbol, data.quotes);
        return keltnerChannels.get(signalIndex).getLow();
    }

    @Override
    public String toString() {
        return "SL [Fixed] Keltner 100% bottom";
    }
}
