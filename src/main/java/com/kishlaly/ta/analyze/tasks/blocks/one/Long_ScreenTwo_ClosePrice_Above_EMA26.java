package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTE_CLOSE_PRICE_NOT_ABOVE_EMA26_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.utils.Quotes.isQuoteClosePriceAboveEMA;

/**
 * the last bar crosses the EMA26
 */
public class Long_ScreenTwo_ClosePrice_Above_EMA26 implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_1_EMA26 = (List<EMA>) screen.indicators.get(Indicator.EMA26);
        if (!isQuoteClosePriceAboveEMA(screen.getLastQuote(), CollectionsTools.getFromEnd(screen_1_EMA26, 1).getValue())) {
            Log.recordCode(LAST_QUOTE_CLOSE_PRICE_NOT_ABOVE_EMA26_SCREEN_2, screen);
            Log.addDebugLine("Last quote close price not above EMA26 on screen 2");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTE_CLOSE_PRICE_NOT_ABOVE_EMA26_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
