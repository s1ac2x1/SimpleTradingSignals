package com.kishlaly.ta.model

import com.kishlaly.ta.model.indicators.Indicator

data class TimeframeIndicators(val timeframe: Timeframe, val indicators: Array<Indicator>) {
}