package com.kishlaly.ta.cache

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.Timeframe

class QuotesInMemoryCache {

    companion object {

        private val cache = mutableMapOf<Key, List<Quote>>()
        private val gson = Gson()

        fun put(symbol: String, timeframe: Timeframe, quotes: List<Quote>) {
            cache[Key(symbol, timeframe)] = quotes
        }

        operator fun get(symbol: String, timeframe: Timeframe): List<Quote> {
            val cached = cache.getOrDefault(Key(symbol, timeframe), emptyList())
            return if (cached.isEmpty()) {
                emptyList()
            } else {
                val json = gson.toJson(cached)
                gson.fromJson<List<Quote>>(json, object : TypeToken<List<Quote>>() {}.type).sortedBy { it.timestamp }
            }
        }

        fun clear() {
            cache.clear()
        }

    }

}

data class Key(val symbol: String, val timeframe: Timeframe) {
}
