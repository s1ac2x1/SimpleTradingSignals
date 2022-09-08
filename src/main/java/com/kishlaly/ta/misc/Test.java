package com.kishlaly.ta.misc;

import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.utils.ContextJava;
import com.kishlaly.ta.utils.QuotesJava;

import java.util.List;

import static com.kishlaly.ta.analyze.TaskTypeJava.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.cache.CacheReaderJava.getSymbolData;

public class Test {

    public static void main(String[] args) {
        ContextJava.aggregationTimeframe = TimeframeJava.DAY;
        ContextJava.timeframe = TimeframeJava.DAY;
        String symbol = "KMI";
        SymbolDataJava screen2 = getSymbolData(THREE_DISPLAYS_BUY.getTimeframeIndicators(2), symbol);
        List<QuoteJava> weekly = QuotesJava.dayToWeek(QuotesJava.dayToWeek(screen2.quotes));
        System.out.println(weekly.size());
    }

}
