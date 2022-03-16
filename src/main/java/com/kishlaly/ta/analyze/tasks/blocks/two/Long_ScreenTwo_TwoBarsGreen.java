package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTES_NOT_GREEN_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * Две последние котировки - зеленые
 */
public class Long_ScreenTwo_TwoBarsGreen implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Quote> screen_2_Quotes = screen.quotes;
        Quote quote2 = screen_2_Quotes.get(resolveMinBarsCount(screen.timeframe) - 2);
        Quote quote1 = screen_2_Quotes.get(resolveMinBarsCount(screen.timeframe) - 1);
        boolean quote2Green = quote2.getClose() > quote2.getOpen();
        boolean quote1Green = quote1.getClose() > quote1.getOpen();
        boolean bothAreGreen = quote1Green && quote2Green;
        if (!bothAreGreen) {
            Log.recordCode(LAST_QUOTES_NOT_GREEN_SCREEN_2, screen);
            Log.addDebugLine("Две последних котировки не зеленые");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTES_NOT_GREEN_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
