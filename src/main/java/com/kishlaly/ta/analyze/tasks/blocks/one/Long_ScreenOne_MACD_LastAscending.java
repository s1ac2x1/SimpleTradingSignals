package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_1;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * the last histogram grows
 */
public class Long_ScreenOne_MACD_LastAscending implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<MACD> screen_1_MACD = (List<MACD>) screen.indicators.get(Indicator.MACD);
        MACD screen_1_lastMACD = CollectionsTools.getFromEnd(screen_1_MACD, 1);
        MACD screen_1_preLastMACD = CollectionsTools.getFromEnd(screen_1_MACD, 2);

        boolean check2 = screen_1_lastMACD.getHistogram() > screen_1_preLastMACD.getHistogram();
        if (!check2) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_1, screen);
            Log.addDebugLine("The histogram does not grow on the long-term screen");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_1);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
