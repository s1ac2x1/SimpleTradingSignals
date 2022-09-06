package com.kishlaly.ta.cache

import com.google.gson.Gson
import com.kishlaly.ta.cache.keys.*
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.model.indicators.*
import java.util.concurrent.ConcurrentHashMap

class IndicatorsInMemoryCache {

    companion object {
        private val gson: Gson = Gson()
        private val ema = ConcurrentHashMap<EMAKey, List<EMA>>()
        private val macd = ConcurrentHashMap<MACDKey, List<MACD>>()
        private val keltner = ConcurrentHashMap<KeltnerKey, List<Keltner>>()
        private val atr = ConcurrentHashMap<ATRKey, List<ATR>>()
        private val stochastic = ConcurrentHashMap<StochKey, List<Stochastic>>()
        private val bollinger = ConcurrentHashMap<BollingerKey, List<Bollinger>>()
        private val efi = ConcurrentHashMap<EFIKey, List<ElderForceIndex>>()

        fun putEMA(symbol: String, timeframe: Timeframe, period: Int, data: List<EMA>) {
            ema[EMAKey(symbol, timeframe, period)] = data
        }


    }

}