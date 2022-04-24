package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * последний столбик пересекает EMA26 или выше
 */
public class Long_ScreenOne_EMA_LastCrossesOrAbove implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        Quote lastQuote = screen.getLastQuote();
        List<EMA> screen_2_EMA26 = (List<EMA>) screen.indicators.get(Indicator.EMA26);
        EMA lastEMA26 = CollectionsTools.getFromEnd(screen_2_EMA26, 1);
        boolean lastBarCrossing = Quotes.isQuoteCrossedEMA(lastQuote, lastEMA26.getValue());
        boolean lastBarAbove = Quotes.isQuoteAboveEMA(lastQuote, lastEMA26.getValue());

        if (!lastBarCrossing || !lastBarAbove) {
            Log.recordCode(LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1, screen);
            Log.addDebugLine("Последний столбик не пересекает или не выше EMA на долгосрочном экране");
            return new BlockResult(lastQuote, LAST_QUOTE_NOT_CROSSING_OR_NOT_ABOVE_EMA_SCREEN_1);
        }
        return new BlockResult(lastQuote, OK);
    }
}
