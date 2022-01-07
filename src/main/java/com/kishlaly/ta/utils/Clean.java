package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.SymbolData;

public class Clean {

    public static void clear(SymbolData symbolData) {
        symbolData.quotes.clear();
        symbolData.indicators.clear();
    }

}
