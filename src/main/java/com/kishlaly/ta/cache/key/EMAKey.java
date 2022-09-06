package com.kishlaly.ta.cache.key;

import com.kishlaly.ta.model.TimeframeJava;

public class EMAKey extends BaseKey {

    public EMAKey(final String symbol, final TimeframeJava timeframe, final int period) {
        super(symbol, timeframe, period);
    }

}
