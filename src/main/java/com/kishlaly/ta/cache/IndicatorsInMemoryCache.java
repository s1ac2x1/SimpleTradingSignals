package com.kishlaly.ta.cache;

import com.google.gson.Gson;
import com.kishlaly.ta.cache.key.*;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class IndicatorsInMemoryCache {

    private static Gson gson = new Gson();

    private static ConcurrentHashMap<EMAKey, List<EMA>> ema = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<MACDKey, List<MACD>> macd = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<KeltnerKEY, List<Keltner>> keltner = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<ATRKey, List<ATR>> atr = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<StochKey, List<Stoch>> stoch = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<BollingerKey, List<Bollinger>> bollinger = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<EFIKey, List<ElderForceIndex>> efi = new ConcurrentHashMap<>();

    public static void putEMA(String symbol, Timeframe timeframe, int period, List<EMA> data) {
        ema.put(new EMAKey(symbol, timeframe, period), data);
    }

    public static void putMACD(String symbol, Timeframe timeframe, List<MACD> data) {
        macd.put(new MACDKey(symbol, timeframe), data);
    }

    public static void putKeltner(String symbol, Timeframe timeframe, List<Keltner> data) {
        keltner.put(new KeltnerKEY(symbol, timeframe), data);
    }

    public static void putATR(String symbol, Timeframe timeframe, int period, List<ATR> data) {
        atr.put(new ATRKey(symbol, timeframe, period), data);
    }

    public static void putStoch(String symbol, Timeframe timeframe, List<Stoch> data) {
        stoch.put(new StochKey(symbol, timeframe), data);
    }

    public static void putEFI(String symbol, Timeframe timeframe, List<ElderForceIndex> data) {
        efi.put(new EFIKey(symbol, timeframe), data);
    }

    public static void putBollinger(String symbol, Timeframe timeframe, List<Bollinger> data) {
        bollinger.put(new BollingerKey(symbol, timeframe), data);
    }

    public static List<EMA> getEMA(String symbol, Timeframe timeframe, int period) {
        List<EMA> cached = ema.getOrDefault(new EMAKey(symbol, timeframe, period), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<EMA> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<EMA>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(EMA::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static List<MACD> getMACD(String symbol, Timeframe timeframe) {
        List<MACD> cached = macd.getOrDefault(new MACDKey(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<MACD> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<MACD>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(MACD::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static List<Keltner> getKeltner(String symbol, Timeframe timeframe) {
        List<Keltner> cached = keltner.getOrDefault(new KeltnerKEY(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<Keltner> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<Keltner>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(Keltner::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static List<ATR> getATR(String symbol, Timeframe timeframe, int period) {
        List<ATR> cached = atr.getOrDefault(new ATRKey(symbol, timeframe, period), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<ATR> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<ATR>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(ATR::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static List<Stoch> getStoch(String symbol, Timeframe timeframe) {
        List<Stoch> cached = stoch.getOrDefault(new StochKey(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<Stoch> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<Stoch>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(Stoch::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }

    public static List<Bollinger> getBollinger(String symbol, Timeframe timeframe) {
        List<Bollinger> cached = bollinger.getOrDefault(new BollingerKey(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<Bollinger> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<Bollinger>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(Bollinger::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }

    public static List<ElderForceIndex> getEFI(String symbol, Timeframe timeframe) {
        List<ElderForceIndex> cached = efi.getOrDefault(new EFIKey(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<ElderForceIndex> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<ElderForceIndex>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(ElderForceIndex::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static void clear() {
        ema.clear();
        macd.clear();
        keltner.clear();
        atr.clear();
        stoch.clear();
    }

}
