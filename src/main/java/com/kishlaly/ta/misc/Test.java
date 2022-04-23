package com.kishlaly.ta.misc;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Numbers;

import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.cache.CacheReader.getSymbolData;

public class Test {

    public static void main(String[] args) {
        indicators();
    }

    private static void indicators() {
        Context.aggregationTimeframe = Timeframe.DAY;
        Context.timeframe = Timeframe.DAY;
        String symbol = "KMI";
        SymbolData screen2 = getSymbolData(THREE_DISPLAYS_BUY.getTimeframeIndicators(2), symbol);

        int accountBalance = 100;
        double price = 15.05;
        int lots = Numbers.roundDown(accountBalance / price);
        System.out.println(lots);
    }

}
