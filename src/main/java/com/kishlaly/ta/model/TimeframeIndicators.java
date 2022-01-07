package com.kishlaly.ta.model;

import com.kishlaly.ta.model.indicators.Indicator;

public class TimeframeIndicators {

    public Timeframe timeframe;
    public Indicator[] indicators;

    public TimeframeIndicators(final Timeframe timeframe, final Indicator[] indicators) {
        this.timeframe = timeframe;
        this.indicators = indicators;
    }
}
