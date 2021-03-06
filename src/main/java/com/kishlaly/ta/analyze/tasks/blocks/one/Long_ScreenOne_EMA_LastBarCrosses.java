package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTE_NOT_CROSSING_EMA_SCREEN_1;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.utils.Quotes.isQuoteCrossedEMA;

/**
 * the last bar crosses the EMA26
 */
public class Long_ScreenOne_EMA_LastBarCrosses implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_1_EMA26 = (List<EMA>) screen.indicators.get(Indicator.EMA26);
        if (!isQuoteCrossedEMA(screen.getLastQuote(), CollectionsTools.getFromEnd(screen_1_EMA26, 1).getValue())) {
            Log.recordCode(LAST_QUOTE_NOT_CROSSING_EMA_SCREEN_1, screen);
            Log.addDebugLine("The last bar does not cross the EMA on the long term screen");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTE_NOT_CROSSING_EMA_SCREEN_1);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
