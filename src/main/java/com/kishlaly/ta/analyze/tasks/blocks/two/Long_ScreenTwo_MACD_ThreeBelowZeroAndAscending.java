package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.*;

/**
 * histogram should be below zero and start to rise at the last three values
 */
public class Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending implements ScreenTwoBlock {

    @Override
    public BlockResult check(SymbolData screen) {
        List<MACDJava> screen_2_MACD = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        Double macd3 = CollectionsTools.getFromEnd(screen_2_MACD, 3).getHistogram();
        Double macd2 = CollectionsTools.getFromEnd(screen_2_MACD, 2).getHistogram();
        Double macd1 = CollectionsTools.getFromEnd(screen_2_MACD, 1).getHistogram();

        boolean histogramBelowZero = macd3 < 0 && macd2 < 0 && macd1 < 0;
        if (!histogramBelowZero) {
            Log.recordCode(HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2, screen);
            Log.addDebugLine("The bar graph on the second screen is at least zero");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2);
        }

        boolean ascendingHistogram = macd3 < macd2 && macd2 < macd1;
        if (!ascendingHistogram) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("The histogram on the second screen does not increase");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
