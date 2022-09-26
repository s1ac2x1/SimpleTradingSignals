package com.kishlaly.ta.cache

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
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
            val get = get(ema, EMAKey(symbol, timeframe, period)) as ArrayList<LinkedTreeMap<Any, Any>>
            return get.map { EMA((it["timestamp"] as Double).toLong(), it["value"] as Double) }
                .toList().sortedBy { it.timestamp }
        }

        fun putMACD(symbol: String, timeframe: Timeframe, data: List<MACD>) {
            macd[MACDKey(symbol, timeframe)] = data
        }

        fun getMACD(symbol: String, timeframe: Timeframe): List<MACD> {
            val get = get(macd, MACDKey(symbol, timeframe)) as ArrayList<LinkedTreeMap<Any, Any>>
            return get.map {
                MACD(
                    (it["timestamp"] as Double).toLong(), it["macd"] as Double, it["signal"] as Double,
                    it["histogram"] as Double
                )
            }.toList().sortedBy { it.timestamp }
        }

        fun putKeltner(symbol: String, timeframe: Timeframe, data: List<Keltner>) {
            keltner[KeltnerKey(symbol, timeframe)] = data
        }

        fun getKeltner(symbol: String, timeframe: Timeframe): List<Keltner> {
            val get = get(keltner, KeltnerKey(symbol, timeframe)) as ArrayList<LinkedTreeMap<Any, Any>>
            val sortedBy = get.map {
                Keltner(
                    (it["timestamp"] as Double).toLong(),
                    it["low"] as Double,
                    it["middle"] as Double,
                    it["top"] as Double
                )
            }.toList().sortedBy { it.timestamp }
            return sortedBy
        }

        fun putATR(symbol: String, timeframe: Timeframe, period: Int, data: List<ATR>) {
            atr[ATRKey(symbol, timeframe, period)] = data
        }

        fun getATR(symbol: String, timeframe: Timeframe, period: Int): List<ATR> {
            val get = get(atr, ATRKey(symbol, timeframe, period)) as ArrayList<LinkedTreeMap<Any, Any>>
            return get.map { ATR((it["timestamp"] as Double).toLong(), it["value"] as Double) }
                .toList().sortedBy { it.timestamp }
        }

        fun putStoch(symbol: String, timeframe: Timeframe, data: List<Stochastic>) {
            stochastic[StochKey(symbol, timeframe)] = data
        }

        fun getStoch(symbol: String, timeframe: Timeframe): List<Stochastic> {
            val get = get(stochastic, StochKey(symbol, timeframe)) as ArrayList<LinkedTreeMap<Any, Any>>
            return get.map {
                Stochastic(
                    (it["timestamp"] as Double).toLong(),
                    it["slowD"] as Double,
                    it["slowK"] as Double
                )
            }.toList().sortedBy { it.timestamp }
        }

        fun putEFI(symbol: String, timeframe: Timeframe, data: List<ElderForceIndex>) {
            efi[EFIKey(symbol, timeframe)] = data
        }

        fun getEFI(symbol: String, timeframe: Timeframe): List<ElderForceIndex> {
            val get = get(efi, EFIKey(symbol, timeframe)) as ArrayList<LinkedTreeMap<Any, Any>>
            return get.map {
                ElderForceIndex(
                    (it["timestamp"] as Double).toLong(),
                    it["value"] as Double
                )
            }.toList().sortedBy { it.timestamp }
        }

        fun putBollinger(symbol: String, timeframe: Timeframe, data: List<Bollinger>) {
            bollinger[BollingerKey(symbol, timeframe)] = data
        }

        fun getBollinger(symbol: String, timeframe: Timeframe): List<Bollinger> {
            val get = get(bollinger, BollingerKey(symbol, timeframe)) as ArrayList<LinkedTreeMap<Any, Any>>
            return get.map {
                Bollinger(
                    (it["timestamp"] as Double).toLong(),
                    it["bottom"] as Double,
                    it["middle"] as Double,
                    it["top"] as Double
                )
            }.toList().sortedBy { it.timestamp }
        }

        private fun <T : AbstractModel, K : BaseKey> get(map: ConcurrentHashMap<K, List<T>>, key: K): List<T> {
            return copy(map.getOrDefault(key, emptyList()))
        }

        private fun <T : AbstractModel> copy(source: List<T>): List<T> {
            return gson.fromJson<List<T>>(gson.toJson(source), object : TypeToken<List<T>>() {}.type)
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