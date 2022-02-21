package com.kishlaly.ta.cache;

import com.google.gson.Gson;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class IndicatorsInMemoryCache {

    private static Gson gson = new Gson();

    private static ConcurrentHashMap<EMAKey, List<EMA>> ema = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<MACDKey, List<MACD>> macd = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<KeltnerKEY, List<Keltner>> keltner = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<ATRKey, List<ATR>> atr = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<StochKey, List<Stoch>> stoch = new ConcurrentHashMap<>();

    public static void putEMA(String symbol, Timeframe timeframe, int period, List<EMA> data) {
        ema.put(new EMAKey(symbol, timeframe, period), data);
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

    public static void putMACD(String symbol, Timeframe timeframe, List<MACD> data) {
        macd.put(new MACDKey(symbol, timeframe), data);
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

    public static void putKeltner(String symbol, Timeframe timeframe, List<Keltner> data) {
        keltner.put(new KeltnerKEY(symbol, timeframe), data);
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

    public static void putATR(String symbol, Timeframe timeframe, int period, List<ATR> data) {
        atr.put(new ATRKey(symbol, timeframe, period), data);
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

    public static void putStoch(String symbol, Timeframe timeframe, List<Stoch> data) {
        stoch.put(new StochKey(symbol, timeframe), data);
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

    public static void clear() {
        ema.clear();
        macd.clear();
        keltner.clear();
        atr.clear();
        stoch.clear();
    }

    private static class StochKey {
        String symbol;
        Timeframe timeframe;

        public StochKey(final String symbol, final Timeframe timeframe) {
            this.symbol = symbol;
            this.timeframe = timeframe;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final StochKey stochKey = (StochKey) o;
            return this.symbol.equals(stochKey.symbol) && this.timeframe == stochKey.timeframe;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.symbol, this.timeframe);
        }
    }

    private static class ATRKey {
        String symbol;
        Timeframe timeframe;
        int period;

        public ATRKey(final String symbol, final Timeframe timeframe, final int period) {
            this.symbol = symbol;
            this.timeframe = timeframe;
            this.period = period;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final ATRKey atrKey = (ATRKey) o;
            return this.period == atrKey.period && this.symbol.equals(atrKey.symbol) && this.timeframe == atrKey.timeframe;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.symbol, this.timeframe, this.period);
        }
    }

    private static class KeltnerKEY {
        String symbol;
        Timeframe timeframe;

        public KeltnerKEY(final String symbol, final Timeframe timeframe) {
            this.symbol = symbol;
            this.timeframe = timeframe;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final KeltnerKEY that = (KeltnerKEY) o;
            return this.symbol.equals(that.symbol) && this.timeframe == that.timeframe;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.symbol, this.timeframe);
        }
    }

    private static class MACDKey {
        String symbol;
        Timeframe timeframe;

        public MACDKey(final String symbol, final Timeframe timeframe) {
            this.symbol = symbol;
            this.timeframe = timeframe;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final MACDKey macdKey = (MACDKey) o;
            return this.symbol.equals(macdKey.symbol) && this.timeframe == macdKey.timeframe;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.symbol, this.timeframe);
        }
    }

    private static class EMAKey {
        String symbol;
        Timeframe timeframe;
        int period;

        public EMAKey(final String symbol, final Timeframe timeframe, final int period) {
            this.symbol = symbol;
            this.timeframe = timeframe;
            this.period = period;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final EMAKey emaKey = (EMAKey) o;
            return this.period == emaKey.period && this.symbol.equals(emaKey.symbol) && this.timeframe == emaKey.timeframe;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.symbol, this.timeframe, this.period);
        }
    }

}
