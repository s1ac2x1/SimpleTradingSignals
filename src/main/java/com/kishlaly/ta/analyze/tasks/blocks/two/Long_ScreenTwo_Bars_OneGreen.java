package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * Две последние котировки - зеленые
 */
public class Long_ScreenTwo_Bars_OneGreen implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Quote> screen_2_Quotes = screen.quotes;
        Quote quote1 = screen_2_Quotes.get(resolveMinBarsCount(screen.timeframe) - 1);
        boolean quote1Green = quote1.getClose() > quote1.getOpen();
        if (!quote1Green) {
            Log.recordCode(LAST_QUOTE_NOT_GREEN_SCREEN_2, screen);
            Log.addDebugLine("Последняя котировка не зеленая на втором экране");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTE_NOT_GREEN_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
