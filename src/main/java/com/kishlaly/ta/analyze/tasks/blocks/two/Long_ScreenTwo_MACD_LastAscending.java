package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCode.OK;

/**
 * the last histogram grows
 */
public class Long_ScreenTwo_MACD_LastAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<MACDJava> screen_2_MACD = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        MACDJava screen_2_lastMACD = screen_2_MACD.get(screen_2_MACD.size() - 1);
        MACDJava screen_2_preLastMACD = screen_2_MACD.get(screen_2_MACD.size() - 2);

        boolean ascending = screen_2_lastMACD.getHistogram() > screen_2_preLastMACD.getHistogram();
        if (!ascending) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("The histogram does not grow on the second screen");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
