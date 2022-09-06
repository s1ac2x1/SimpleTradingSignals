package com.kishlaly.ta.cache;

import com.google.gson.Gson;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.TimeframeJava;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class QuotesInMemoryCache {

    private static ConcurrentHashMap<Key, List<QuoteJava>> cache = new ConcurrentHashMap<>();
    private static Gson gson = new Gson();

    public static void put(String symbol, TimeframeJava timeframe, List<QuoteJava> quotes) {
        cache.put(new Key(symbol, timeframe), quotes);
    }

    public static List<QuoteJava> get(String symbol, TimeframeJava timeframe) {
        List<QuoteJava> cached = cache.getOrDefault(new Key(symbol, timeframe), Collections.emptyList());
        if (!cached.isEmpty()) {
            String json = gson.toJson(cached);
            List<QuoteJava> copy = gson.fromJson(json, new com.google.common.reflect.TypeToken<List<QuoteJava>>() {
            }.getType());
            Collections.sort(copy, Comparator.comparing(QuoteJava::getTimestamp));
            return copy;
        }
        return Collections.emptyList();
    }

    public static void clear() {
        cache.clear();
    }

    private static class Key {
        public String symbol;
        public TimeframeJava timeframe;

        public Key(final String symbol, final TimeframeJava timeframe) {
            this.symbol = symbol;
            this.timeframe = timeframe;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final Key key = (Key) o;
            return this.symbol.equals(key.symbol) && this.timeframe == key.timeframe;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.symbol, this.timeframe);
        }
    }

}
