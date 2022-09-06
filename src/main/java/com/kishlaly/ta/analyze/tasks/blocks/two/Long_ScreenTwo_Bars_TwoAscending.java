package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTES_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * the last two bars go up (quote.low & quote.high)
 */
public class Long_ScreenTwo_Bars_TwoAscending implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolData screen) {
        List<QuoteJava> screen_2_Quotes = screen.quotes;
        QuoteJava quote2 = CollectionsTools.getFromEnd(screen_2_Quotes, 2);
        QuoteJava quote1 = CollectionsTools.getFromEnd(screen_2_Quotes, 1);

        boolean lowAndHightAscending = quote2.getLow() < quote1.getLow() && quote2.getHigh() < quote1.getHigh();
        if (!lowAndHightAscending) {
            Log.recordCode(LAST_QUOTES_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("The last two quotes do not grow consistently on the second screen");
            return new BlockResultJava(screen.getLastQuote(), LAST_QUOTES_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
