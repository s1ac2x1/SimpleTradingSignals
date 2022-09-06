package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * the last bar crosses the EMA26 or higher
 */
public class Long_ScreenOne_EMA_LastCrossesOrAbove implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        QuoteJava lastQuote = screen.getLastQuote();
        List<EMAJava> screen_2_EMA26 = (List<EMAJava>) screen.indicators.get(Indicator.EMA26);
        EMAJava lastEMA26 = CollectionsTools.getFromEnd(screen_2_EMA26, 1);
        boolean lastBarCrossing = Quotes.isQuoteCrossedEMA(lastQuote, lastEMA26.getValue());
        boolean lastBarAbove = Quotes.isQuoteAboveEMA(lastQuote, lastEMA26.getValue());

        if (!lastBarCrossing || !lastBarAbove) {
            Log.recordCode(LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1, screen);
            Log.addDebugLine("The last bar does not cross or is not above the EMA on the long term screen");
            return new BlockResult(lastQuote, LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1);
        }
        return new BlockResult(lastQuote, OK);
    }
}
