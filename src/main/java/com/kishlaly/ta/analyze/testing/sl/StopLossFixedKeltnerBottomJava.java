package com.kishlaly.ta.analyze.testing.sl;

import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.KeltnerJava;
import com.kishlaly.ta.utils.IndicatorUtilsJava;

import java.util.List;

/**
 * SL is chosen at the lower point of the Keltner channel, which exists in the signal quote
 */
public class StopLossFixedKeltnerBottomJava extends StopLossStrategyJava {

    public StopLossFixedKeltnerBottomJava() {
        super(null, false);
    }

    @Override
    public double calculate(SymbolDataJava data, int signalIndex) {
        List<KeltnerJava> keltnerChannels = IndicatorUtilsJava.buildKeltnerChannels(data.symbol, data.quotes);
        return keltnerChannels.get(signalIndex).getLow();
    }

    @Override
    public String toString() {
        return "SL [Fixed] Keltner 100% bottom";
    }
}
