package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTE_ABOVE_EMA_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.utils.Quotes.isQuoteAboveEMA;

/**
 * last bar no higher than EMA13
 */
public class Long_ScreenTwo_EMA_LastBarNotAbove implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_2_EMA13 = (List<EMA>) screen.indicators.get(Indicator.EMA13);
        if (isQuoteAboveEMA(screen.getLastQuote(), screen_2_EMA13.get(screen_2_EMA13.size() - 1).getValue())) {
            Log.recordCode(LAST_QUOTE_ABOVE_EMA_SCREEN_2, screen);
            Log.addDebugLine("Last bar above the EMA on the second screen");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTE_ABOVE_EMA_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
