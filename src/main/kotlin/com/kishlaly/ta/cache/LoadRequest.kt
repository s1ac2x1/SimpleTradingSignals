package com.kishlaly.ta.cache

import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.model.indicators.Indicator

class LoadRequest(
        val cacheType: CacheType,
        val timeframe: Timeframe,
        val symbols: List<String>,
        var config: Map<String, String> = mutableMapOf(),
        var indicator: Indicator? = null) {
}