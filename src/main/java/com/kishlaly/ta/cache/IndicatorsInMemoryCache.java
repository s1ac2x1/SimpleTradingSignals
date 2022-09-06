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

    private static ConcurrentHashMap<EMAKey, List<EMAJava>> ema = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<MACDKey, List<MACD>> macd = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<KeltnerKEY, List<KeltnerJava>> keltner = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<ATRKey, List<ATRJava>> atr = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<StochKey, List<Stoch>> stoch = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<BollingerKey, List<BollingerJava>> bollinger = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<EFIKey, List<ElderForceIndexJava>> efi = new ConcurrentHashMap<>();

    public static void putEMA(String symbol, Timeframe timeframe, int period, List<EMAJava> data) {
        ema.put(new EMAKey(symbol, timeframe, period), data);
    }

    public static void putMACD(String symbol, Timeframe timeframe, List<MACD> data) {
        macd.put(new MACDKey(symbol, timeframe), data);
    }

    public static void putKeltner(String symbol, Timeframe timeframe, List<KeltnerJava> data) {
        keltner.put(new KeltnerKEY(symbol, timeframe), data);
    }

    public static void putATR(String symbol, Timeframe timeframe, int period, List<ATRJava> data) {
        atr.put(new ATRKey(symbol, timeframe, period), data);
    }

    public static void putStoch(String symbol, Timeframe timeframe, List<Stoch> data) {
        stoch.put(new StochKey(symbol, timeframe), data);
    }

    public static void putEFI(String symbol, Timeframe timeframe, List<ElderForceIndexJava> data) {
        efi.put(new EFIKey(symbol, timeframe), data);
    }

    public static void putBollinger(String symbol, Timeframe timeframe, List<BollingerJava> data) {
        bollinger.put(new BollingerKey(symbol, timeframe), data);
    }

    public static List<EMAJava> getEMA(String symbol, Timeframe timeframe, int period) {
        List<EMAJava> cached = ema.getOrDefault(new EMAKey(symbol, timeframe, period), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<EMAJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<EMAJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(EMAJava::getTimestamp));
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


    public static List<KeltnerJava> getKeltner(String symbol, Timeframe timeframe) {
        List<KeltnerJava> cached = keltner.getOrDefault(new KeltnerKEY(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<KeltnerJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<KeltnerJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(KeltnerJava::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static List<ATRJava> getATR(String symbol, Timeframe timeframe, int period) {
        List<ATRJava> cached = atr.getOrDefault(new ATRKey(symbol, timeframe, period), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<ATRJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<ATRJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(ATRJava::getTimestamp));
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

    public static List<BollingerJava> getBollinger(String symbol, Timeframe timeframe) {
        List<BollingerJava> cached = bollinger.getOrDefault(new BollingerKey(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<BollingerJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<BollingerJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(BollingerJava::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }

    public static List<ElderForceIndexJava> getEFI(String symbol, Timeframe timeframe) {
        List<ElderForceIndexJava> cached = efi.getOrDefault(new EFIKey(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<ElderForceIndexJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<ElderForceIndexJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(ElderForceIndexJava::getTimestamp));
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
