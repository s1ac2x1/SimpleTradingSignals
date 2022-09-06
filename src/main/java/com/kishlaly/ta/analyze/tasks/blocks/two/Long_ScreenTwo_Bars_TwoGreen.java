package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTES_NOT_GREEN_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * The last two quotes are green
 */
public class Long_ScreenTwo_Bars_TwoGreen implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<QuoteJava> screen_2_Quotes = screen.quotes;
        QuoteJava quote2 = CollectionUtilsJava.getFromEnd(screen_2_Quotes, 2);
        QuoteJava quote1 = CollectionUtilsJava.getFromEnd(screen_2_Quotes, 1);
        boolean quote2Green = quote2.getClose() > quote2.getOpen();
        boolean quote1Green = quote1.getClose() > quote1.getOpen();
        boolean bothAreGreen = quote1Green && quote2Green;
        if (!bothAreGreen) {
            Log.recordCode(LAST_QUOTES_NOT_GREEN_SCREEN_2, screen);
            Log.addDebugLine("The last two quotes are not green on the second screen");
            return new BlockResultJava(screen.getLastQuote(), LAST_QUOTES_NOT_GREEN_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
