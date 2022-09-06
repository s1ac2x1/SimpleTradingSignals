package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultCode;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCode.LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCode.OK;

public class Long_ScreenTwo_MACD_LastShouldBeNegative implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<MACDJava> screenTwoMacdValues = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        double latestHistogramValue = CollectionsTools.getFromEnd(screenTwoMacdValues, 1).getHistogram();

        if (latestHistogramValue > 0) {
            Log.recordCode(BlockResultCode.LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2, screen);
            Log.addDebugLine("histogram at the right edge above zero");
            return new BlockResult(screen.getLastQuote(), LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
