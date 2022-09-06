package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.*;

/**
 * histogram should be below zero and start to rise: check on the last TWO values
 */
public class Long_ScreenTwo_MACD_TwoBelowZeroAndAscending implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolData screen) {
        List<MACDJava> screen_2_MACD = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        Double macd2 = screen_2_MACD.get(screen_2_MACD.size() - 2).getHistogram();
        Double macd1 = screen_2_MACD.get(screen_2_MACD.size() - 1).getHistogram();

        boolean histogramBelowZero = macd2 < 0 && macd1 < 0;
        if (!histogramBelowZero) {
            Log.recordCode(HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2, screen);
            Log.addDebugLine("The bar graph on the second screen is at least zero");
            return new BlockResultJava(screen.getLastQuote(), HISTOGRAM_NOT_BELOW_ZERO_SCREEN_2);
        }

        boolean ascendingHistogram = macd2 < macd1;
        if (!ascendingHistogram) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("The histogram on the second screen does not increase");
            return new BlockResultJava(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
