package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;

import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromCache;

public class Test {

    public static void main(String[] args) throws Exception {
        List<Quote> quotes = loadQuotesFromCache("MCK");

    }

}
