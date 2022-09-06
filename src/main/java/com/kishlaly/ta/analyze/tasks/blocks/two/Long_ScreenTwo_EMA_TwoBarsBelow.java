package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.QUOTES_NOT_BELOW_EMA_SCREEN_2;
import static com.kishlaly.ta.utils.Quotes.isQuoteBelowEMA;

/**
 * the last two bars are completely below EMA13
 */
public class Long_ScreenTwo_EMA_TwoBarsBelow implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_2_EMA13 = (List<EMA>) screen.indicators.get(Indicator.EMA13);
        EMA ema13_2 = CollectionsTools.getFromEnd(screen_2_EMA13, 2);
        EMA ema13_1 = CollectionsTools.getFromEnd(screen_2_EMA13, 1);
        List<QuoteJava> screen_2_Quotes = screen.quotes;
        QuoteJava quote2 = CollectionsTools.getFromEnd(screen_2_Quotes, 2);
        QuoteJava quote1 = CollectionsTools.getFromEnd(screen_2_Quotes, 1);

        boolean lastQuotesBelowEMA = isQuoteBelowEMA(quote2, ema13_2.getValue()) && isQuoteBelowEMA(quote1, ema13_1.getValue());
        if (!lastQuotesBelowEMA) {
            Log.recordCode(QUOTES_NOT_BELOW_EMA_SCREEN_2, screen);
            Log.addDebugLine("The last two quotes are not below EMA13");
            return new BlockResult(screen.getLastQuote(), QUOTES_NOT_BELOW_EMA_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
