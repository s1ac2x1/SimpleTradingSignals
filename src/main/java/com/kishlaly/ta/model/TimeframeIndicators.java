package com.kishlaly.ta.model;

import com.kishlaly.ta.model.indicators.IndicatorJava;

public class TimeframeIndicators {

    public TimeframeJava timeframe;
    public IndicatorJava[] indicators;

    public TimeframeIndicators(final TimeframeJava timeframe, final IndicatorJava[] indicators) {
        this.timeframe = timeframe;
        this.indicators = indicators;
    }
}
