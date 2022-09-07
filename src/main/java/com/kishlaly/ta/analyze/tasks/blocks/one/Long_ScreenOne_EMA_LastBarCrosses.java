package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTE_NOT_CROSSING_EMA_SCREEN_1;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.utils.Quotes.isQuoteCrossedEMA;

/**
 * the last bar crosses the EMA26
 */
public class Long_ScreenOne_EMA_LastBarCrosses implements ScreenOneBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<EMAJava> screen_1_EMA26 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA26);
        if (!isQuoteCrossedEMA(screen.getLastQuote(), CollectionUtilsJava.getFromEnd(screen_1_EMA26, 1).getValue())) {
            LogJava.recordCode(LAST_QUOTE_NOT_CROSSING_EMA_SCREEN_1, screen);
            LogJava.addDebugLine("The last bar does not cross the EMA on the long term screen");
            return new BlockResultJava(screen.getLastQuote(), LAST_QUOTE_NOT_CROSSING_EMA_SCREEN_1);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
