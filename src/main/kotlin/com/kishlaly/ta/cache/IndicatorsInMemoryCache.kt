package com.kishlaly.ta.cache

import com.google.gson.Gson
import com.kishlaly.ta.cache.keys.ATRKey
import com.kishlaly.ta.model.indicators.ATR
import java.util.concurrent.ConcurrentHashMap

class IndicatorsInMemoryCache {

    companion object {
        private val gson: Gson = Gson()
        private val atr = ConcurrentHashMap<ATRKey, List<ATR>>()

    }

}