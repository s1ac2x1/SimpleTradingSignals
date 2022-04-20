package com.kishlaly.ta.analyze.candles;

import com.kishlaly.ta.analyze.candles.CandleResult;
import com.kishlaly.ta.model.SymbolData;

public interface CandlePattern {

    CandleResult check(SymbolData data);

}
