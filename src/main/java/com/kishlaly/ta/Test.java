package com.kishlaly.ta;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Dates;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static com.kishlaly.ta.cache.CacheReader.loadQuotesFromCache;

public class Test {

    public static void main(String[] args) {
        Context.aggregationTimeframe = Timeframe.HOUR;
        // TODO вынести этот метод в Quotes и протестировать
    }

}
