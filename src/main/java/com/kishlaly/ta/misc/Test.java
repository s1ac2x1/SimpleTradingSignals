package com.kishlaly.ta.misc;

import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.ElderForceIndex;
import com.kishlaly.ta.utils.Context;

import java.util.List;

import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.cache.CacheReader.getSymbolData;
import static com.kishlaly.ta.model.indicators.Indicator.EFI;

public class Test {

    public static void main(String[] args) {
        indicators();
    }

    private static void indicators() {
        Context.aggregationTimeframe = Timeframe.DAY;
        Context.timeframe = Timeframe.DAY;
        String symbol = "SLB";
        SymbolData screen2 = getSymbolData(THREE_DISPLAYS_BUY.getTimeframeIndicators(2), symbol);
        List<ElderForceIndex> list = screen2.indicators.get(EFI);
        System.out.println(list);
    }

}
