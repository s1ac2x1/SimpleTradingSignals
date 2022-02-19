package com.kishlaly.ta.cache;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class QuotesInMemoryCache {

    private static ConcurrentHashMap<Key, List<Quote>> cache = new ConcurrentHashMap<>();

    public static void put(String symbol, Timeframe timeframe, List<Quote> quotes) {
        cache.put(new Key(symbol, timeframe), quotes);
    }

    public static List<Quote> get(String symbol, Timeframe timeframe) {
        return cache.get(new Key(symbol, timeframe));
    }

    private static class Key {
        public String symbol;
        public Timeframe timeframe;

        public Key(final String symbol, final Timeframe timeframe) {
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
