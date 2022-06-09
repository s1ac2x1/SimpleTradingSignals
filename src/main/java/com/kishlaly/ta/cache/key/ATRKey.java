package com.kishlaly.ta.cache.key;

import com.kishlaly.ta.model.Timeframe;

public class ATRKey extends BaseKey {

    public ATRKey(final String symbol, final Timeframe timeframe, final int period) {
        super(symbol, timeframe, period);
    }

}
