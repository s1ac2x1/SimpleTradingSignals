package com.kishlaly.ta.cache.keys

import com.kishlaly.ta.model.Timeframe

open class BaseKey(open val symbol: String,
                   open val timeframe: Timeframe,
                   open val period: Int = 0) {

}