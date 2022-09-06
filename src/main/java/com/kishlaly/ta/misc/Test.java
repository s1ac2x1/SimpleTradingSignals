package com.kishlaly.ta.misc;

import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.TimeframeJava;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.cache.CacheReader.getSymbolData;

public class Test {

    public static void main(String[] args) {
        Context.aggregationTimeframe = TimeframeJava.DAY;
        Context.timeframe = TimeframeJava.DAY;
        String symbol = "KMI";
        SymbolData screen2 = getSymbolData(THREE_DISPLAYS_BUY.getTimeframeIndicators(2), symbol);
        List<QuoteJava> weekly = Quotes.dayToWeek(Quotes.dayToWeek(screen2.quotes));
        System.out.println(weekly.size());
    }

}
