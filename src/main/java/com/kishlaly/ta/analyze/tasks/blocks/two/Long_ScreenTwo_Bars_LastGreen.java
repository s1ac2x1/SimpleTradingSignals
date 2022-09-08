package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTE_NOT_GREEN_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * Last quote is green
 */
public class Long_ScreenTwo_Bars_LastGreen implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<QuoteJava> screen_2_Quotes = screen.quotes;
        QuoteJava quote1 = CollectionUtilsJava.getFromEnd(screen_2_Quotes, 1);

        boolean quote1Green = quote1.getClose() > quote1.getOpen();
        if (!quote1Green) {
            LogJava.recordCode(LAST_QUOTE_NOT_GREEN_SCREEN_2, screen);
            LogJava.addDebugLine("The last quote is not green on the second screen");
            return new BlockResultJava(screen.getLastQuote(), LAST_QUOTE_NOT_GREEN_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
