package com.kishlaly.ta.model;

import com.kishlaly.ta.model.indicators.IndicatorJava;

public class TimeframeIndicatorsJava {

    public TimeframeJava timeframe;
    public IndicatorJava[] indicators;

    public TimeframeIndicatorsJava(final TimeframeJava timeframe, final IndicatorJava[] indicators) {
        this.timeframe = timeframe;
        this.indicators = indicators;
    }
}
