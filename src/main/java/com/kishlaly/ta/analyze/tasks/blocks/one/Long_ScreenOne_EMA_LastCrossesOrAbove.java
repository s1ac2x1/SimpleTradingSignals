package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;
import com.kishlaly.ta.utils.QuotesJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * the last bar crosses the EMA26 or higher
 */
public class Long_ScreenOne_EMA_LastCrossesOrAbove implements ScreenOneBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        QuoteJava lastQuote = screen.getLastQuote();
        List<EMAJava> screen_2_EMA26 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA26);
        EMAJava lastEMA26 = CollectionUtilsJava.getFromEnd(screen_2_EMA26, 1);
        boolean lastBarCrossing = QuotesJava.isQuoteCrossedEMA(lastQuote, lastEMA26.getValue());
        boolean lastBarAbove = QuotesJava.isQuoteAboveEMA(lastQuote, lastEMA26.getValue());

        if (!lastBarCrossing || !lastBarAbove) {
            LogJava.recordCode(LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1, screen);
            LogJava.addDebugLine("The last bar does not cross or is not above the EMA on the long term screen");
            return new BlockResultJava(lastQuote, LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1);
        }
        return new BlockResultJava(lastQuote, OK);
    }
}
