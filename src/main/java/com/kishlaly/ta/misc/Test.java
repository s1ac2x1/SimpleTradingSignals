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

        double openingPrice = 100;
        int lots = Numbers.roundDown(Context.accountBalance / openingPrice);
        double openPositionSize = lots * openingPrice;
        double commissions = openPositionSize / 100 * Context.tradeCommission;
        System.out.println(openPositionSize);
        System.out.println(commissions);
    }

}
