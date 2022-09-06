package com.kishlaly.ta.cache;

import com.google.gson.Gson;
import com.kishlaly.ta.cache.key.*;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.model.indicators.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class IndicatorsInMemoryCacheJava {

    private static Gson gson = new Gson();

    private static ConcurrentHashMap<EMAKeyJava, List<EMAJava>> ema = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<MACDKey, List<MACDJava>> macd = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<KeltnerKEY, List<KeltnerJava>> keltner = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<ATRKeyJava, List<ATRJava>> atr = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<StochKey, List<StochJava>> stoch = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<BollingerKeyJava, List<BollingerJava>> bollinger = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<EFIKeyJava, List<ElderForceIndexJava>> efi = new ConcurrentHashMap<>();

    public static void putEMA(String symbol, TimeframeJava timeframe, int period, List<EMAJava> data) {
        ema.put(new EMAKeyJava(symbol, timeframe, period), data);
    }

    public static void putMACD(String symbol, TimeframeJava timeframe, List<MACDJava> data) {
        macd.put(new MACDKey(symbol, timeframe), data);
    }

    public static void putKeltner(String symbol, TimeframeJava timeframe, List<KeltnerJava> data) {
        keltner.put(new KeltnerKEY(symbol, timeframe), data);
    }

    public static void putATR(String symbol, TimeframeJava timeframe, int period, List<ATRJava> data) {
        atr.put(new ATRKeyJava(symbol, timeframe, period), data);
    }

    public static void putStoch(String symbol, TimeframeJava timeframe, List<StochJava> data) {
        stoch.put(new StochKey(symbol, timeframe), data);
    }

    public static void putEFI(String symbol, TimeframeJava timeframe, List<ElderForceIndexJava> data) {
        efi.put(new EFIKeyJava(symbol, timeframe), data);
    }

    public static void putBollinger(String symbol, TimeframeJava timeframe, List<BollingerJava> data) {
        bollinger.put(new BollingerKeyJava(symbol, timeframe), data);
    }

    public static List<EMAJava> getEMA(String symbol, TimeframeJava timeframe, int period) {
        List<EMAJava> cached = ema.getOrDefault(new EMAKeyJava(symbol, timeframe, period), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<EMAJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<EMAJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(EMAJava::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static List<MACDJava> getMACD(String symbol, TimeframeJava timeframe) {
        List<MACDJava> cached = macd.getOrDefault(new MACDKey(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<MACDJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<MACDJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(MACDJava::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static List<KeltnerJava> getKeltner(String symbol, TimeframeJava timeframe) {
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


    public static List<ATRJava> getATR(String symbol, TimeframeJava timeframe, int period) {
        List<ATRJava> cached = atr.getOrDefault(new ATRKeyJava(symbol, timeframe, period), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<ATRJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<ATRJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(ATRJava::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }


    public static List<StochJava> getStoch(String symbol, TimeframeJava timeframe) {
        List<StochJava> cached = stoch.getOrDefault(new StochKey(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<StochJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<StochJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(StochJava::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }

    public static List<BollingerJava> getBollinger(String symbol, TimeframeJava timeframe) {
        List<BollingerJava> cached = bollinger.getOrDefault(new BollingerKeyJava(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<BollingerJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<BollingerJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(BollingerJava::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }

    public static List<ElderForceIndexJava> getEFI(String symbol, TimeframeJava timeframe) {
        List<ElderForceIndexJava> cached = efi.getOrDefault(new EFIKeyJava(symbol, timeframe), Collections.emptyList());
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
