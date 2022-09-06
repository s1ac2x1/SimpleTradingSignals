package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.BollingerJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2;

/**
 * the second quote from the end below the bottom Bollinger band
 */
public class Long_ScreenTwo_Bollinger_Bottom_PreLastBelow implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<QuoteJava> quotes = screen.quotes;
        List<BollingerJava> screen_2_Bollinger = (List<BollingerJava>) screen.indicators.get(IndicatorJava.BOLLINGER);
        QuoteJava quote_2 = CollectionUtilsJava.getFromEnd(quotes, 2);
        BollingerJava bollinger_2 = CollectionUtilsJava.getFromEnd(screen_2_Bollinger, 2);

        boolean below = Quotes.isQuoteBelowBollingerBottom(quote_2, bollinger_2);

        if (!below) {
            Log.recordCode(QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2, screen);
            Log.addDebugLine("The second from the end of the quote is not lower than the bottom Bollinger band on the second screen");
            return new BlockResultJava(screen.getLastQuote(), QUOTE_2_NOT_BELOW_BOLLINGER_BOTTOM_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
