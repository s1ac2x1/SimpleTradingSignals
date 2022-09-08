package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.QUOTES_NOT_BELOW_EMA_SCREEN_2;
import static com.kishlaly.ta.utils.QuotesJava.isQuoteBelowEMA;

/**
 * the last two bars are completely below EMA13
 */
public class Long_ScreenTwo_EMA_TwoBarsBelow implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<EMAJava> screen_2_EMA13 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA13);
        EMAJava ema13_2 = CollectionUtilsJava.getFromEnd(screen_2_EMA13, 2);
        EMAJava ema13_1 = CollectionUtilsJava.getFromEnd(screen_2_EMA13, 1);
        List<QuoteJava> screen_2_Quotes = screen.quotes;
        QuoteJava quote2 = CollectionUtilsJava.getFromEnd(screen_2_Quotes, 2);
        QuoteJava quote1 = CollectionUtilsJava.getFromEnd(screen_2_Quotes, 1);

        boolean lastQuotesBelowEMA = isQuoteBelowEMA(quote2, ema13_2.getValue()) && isQuoteBelowEMA(quote1, ema13_1.getValue());
        if (!lastQuotesBelowEMA) {
            LogJava.recordCode(QUOTES_NOT_BELOW_EMA_SCREEN_2, screen);
            LogJava.addDebugLine("The last two quotes are not below EMA13");
            return new BlockResultJava(screen.getLastQuote(), QUOTES_NOT_BELOW_EMA_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
