package com.kishlaly.ta.analyze.candles;

import com.kishlaly.ta.model.SymbolDataJava;

public interface CandlePattern {

    CandleResult check(SymbolDataJava data);

}
