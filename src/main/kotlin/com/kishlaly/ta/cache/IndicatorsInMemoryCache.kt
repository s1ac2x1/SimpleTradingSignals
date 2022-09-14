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

        fun getEMA(symbol: String, timeframe: Timeframe, period: Int): List<EMA> {
            return get(ema, EMAKey(symbol, timeframe, period))
        }

        fun putMACD(symbol: String, timeframe: Timeframe, data: List<MACD>) {
            macd[MACDKey(symbol, timeframe)] = data
        }

        fun getMACD(symbol: String, timeframe: Timeframe): List<MACD> {
            return get(macd, MACDKey(symbol, timeframe))
        }

        fun putKeltner(symbol: String, timeframe: Timeframe, data: List<Keltner>) {
            keltner[KeltnerKey(symbol, timeframe)] = data
        }

        fun getKeltner(symbol: String, timeframe: Timeframe): List<Keltner> {
            return get(keltner, KeltnerKey(symbol, timeframe))
        }

        fun putATR(symbol: String, timeframe: Timeframe, period: Int, data: List<ATR>) {
            atr[ATRKey(symbol, timeframe, period)] = data
        }

        fun getATR(symbol: String, timeframe: Timeframe, period: Int): List<ATR> {
            return get(atr, ATRKey(symbol, timeframe, period))
        }

        fun putStoch(symbol: String, timeframe: Timeframe, data: List<Stochastic>) {
            stochastic[StochKey(symbol, timeframe)] = data
        }

        fun getStoch(symbol: String, timeframe: Timeframe): List<Stochastic> {
            return get(stochastic, StochKey(symbol, timeframe))
        }

        fun putEFI(symbol: String, timeframe: Timeframe, data: List<ElderForceIndex>) {
            efi[EFIKey(symbol, timeframe)] = data
        }

        fun getEFI(symbol: String, timeframe: Timeframe): List<ElderForceIndex> {
            return get(efi, EFIKey(symbol, timeframe))
        }

        fun putBollinger(symbol: String, timeframe: Timeframe, data: List<Bollinger>) {
            bollinger[BollingerKey(symbol, timeframe)] = data
        }

        fun getBollinger(symbol: String, timeframe: Timeframe): List<Bollinger> {
            return get(bollinger, BollingerKey(symbol, timeframe))
        }

        private fun <T : AbstractModel, K : BaseKey> get(map: ConcurrentHashMap<K, List<T>>, key: K): List<T> {
            return copy(map.getOrDefault(key, emptyList()))
        }

        private fun <T : AbstractModel> copy(source: List<T>): List<T> {
            val copy = gson.fromJson<List<T>>(gson.toJson(source), object : TypeToken<List<T>>() {}.type)
            return copy.sortedBy { it.timestamp }
        }

        fun clear() {
            ema.clear()
            macd.clear()
            keltner.clear()
            atr.clear()
            stochastic.clear()
        }

    }

}