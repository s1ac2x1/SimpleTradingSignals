package com.kishlaly.ta.cache;

import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.MACD;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class IndicatorsInMemoryCache {

    private static ConcurrentHashMap<EMAKey, List<EMA>> ema = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<MACDKey, List<MACD>> macd = new ConcurrentHashMap<>();

    public static void putEMA(String symbol, Timeframe timeframe, int period, List<EMA> data) {
        ema.put(new EMAKey(symbol, timeframe, period), data);
    }

    public static List<EMA> getEMA(String symbol, Timeframe timeframe, int period) {
        return ema.get(new EMAKey(symbol, timeframe, period));
    }

    public static void putMACD(String symbol, Timeframe timeframe, List<MACD> data) {
        macd.put(new MACDKey(symbol, timeframe), data);
    }

    public static List<MACD> getMACD(String symbol, Timeframe timeframe) {
        return macd.get(new MACDKey(symbol, timeframe));
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
