package com.kishlaly.ta.cache

import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.model.indicators.Indicator

class LoadRequest(
        val timeframe: Timeframe,
        val symbols: List<String>,
        val cacheType: CacheType,
        var config: Map<String, String> = mutableMapOf(),
        var indicator: Indicator? = null) {
}