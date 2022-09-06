package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.HISTOGRAM_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * the histogram rises on the last three values
 */
public class Long_ScreenTwo_MACD_ThreeAscending implements ScreenTwoBlock {

    @Override
    public BlockResultJava check(SymbolData screen) {
        List<MACDJava> screen_2_MACD = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        Double macd3 = CollectionsTools.getFromEnd(screen_2_MACD, 3).getHistogram();
        Double macd2 = CollectionsTools.getFromEnd(screen_2_MACD, 2).getHistogram();
        Double macd1 = CollectionsTools.getFromEnd(screen_2_MACD, 1).getHistogram();

        boolean ascending = macd3 < macd2 && macd2 < macd1;
        if (!ascending) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("The histogram on the second screen does not increase");
            return new BlockResultJava(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
