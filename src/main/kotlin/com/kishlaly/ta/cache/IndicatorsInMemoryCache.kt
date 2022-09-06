package com.kishlaly.ta.cache

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.kishlaly.ta.cache.keys.*
import com.kishlaly.ta.model.AbstractModel
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

        fun putMACD(symbol: String, timeframe: Timeframe, data: List<MACD>) {
            macd[MACDKey(symbol, timeframe)] = data
        }

        fun putKeltner(symbol: String, timeframe: Timeframe, data: List<Keltner>) {
            keltner[KeltnerKey(symbol, timeframe)] = data
        }

        fun putATR(symbol: String, timeframe: Timeframe, period: Int, data: List<ATR>) {
            atr[ATRKey(symbol, timeframe, period)] = data
        }

        fun putStoch(symbol: String, timeframe: Timeframe, data: List<Stochastic>) {
            stochastic[StochKey(symbol, timeframe)] = data
        }

        fun putEFI(symbol: String, timeframe: Timeframe, data: List<ElderForceIndex>) {
            efi[EFIKey(symbol, timeframe)] = data
        }

        fun putBollinger(symbol: String, timeframe: Timeframe, data: List<Bollinger>) {
            bollinger[BollingerKey(symbol, timeframe)] = data
        }

        fun getEMA(symbol: String, timeframe: Timeframe, period: Int): List<EMA> {
            val cached = ema.getOrDefault(EMAKey(symbol, timeframe, period), emptyList())
            val json = gson.toJson(cached)
            val copy = gson.fromJson<List<EMA>>(json, object : TypeToken<List<EMA>>() {}.type)
            return copy.sortedBy { it.timestamp }
        }

        inline fun <reified T> copy(source: List<T>, destination: List<T>) {

        }

    }

}