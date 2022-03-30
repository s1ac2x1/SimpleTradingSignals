package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

public class Long_ScreenTwo_MACD_LastShouldBeNegative implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<MACD> screenTwoMacdValues = screen.indicators.get(Indicator.MACD);
        double latestHistogramValue = CollectionsTools.getFromEnd(screenTwoMacdValues, 1).getHistogram();

        if (latestHistogramValue > 0) {
            Log.recordCode(BlockResultCode.LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2, screen);
            Log.addDebugLine("гистограмма у правого края выше нуля");
            return new BlockResult(screen.getLastQuote(), LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
