package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_QUOTE_ABOVE_EMA_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.utils.QuotesJava.isQuoteAboveEMA;

/**
 * last bar no higher than EMA13
 */
public class Long_ScreenTwo_EMA_LastBarNotAboveJava implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<EMAJava> screen_2_EMA13 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA13);
        if (isQuoteAboveEMA(screen.getLastQuote(), screen_2_EMA13.get(screen_2_EMA13.size() - 1).getValue())) {
            LogJava.recordCode(LAST_QUOTE_ABOVE_EMA_SCREEN_2, screen);
            LogJava.addDebugLine("Last bar above the EMA on the second screen");
            return new BlockResultJava(screen.getLastQuote(), LAST_QUOTE_ABOVE_EMA_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
