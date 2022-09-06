package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTE_NOT_GREEN_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * Last quote is green
 */
public class Long_ScreenTwo_Bars_LastGreen implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<QuoteJava> screen_2_Quotes = screen.quotes;
        QuoteJava quote1 = CollectionsTools.getFromEnd(screen_2_Quotes, 1);

        boolean quote1Green = quote1.getClose() > quote1.getOpen();
        if (!quote1Green) {
            Log.recordCode(LAST_QUOTE_NOT_GREEN_SCREEN_2, screen);
            Log.addDebugLine("The last quote is not green on the second screen");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTE_NOT_GREEN_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
