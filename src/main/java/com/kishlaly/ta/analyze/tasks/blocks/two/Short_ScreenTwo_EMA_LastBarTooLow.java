package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_BAR_BELOW_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * to filter the situation when the third and second cross EMA13, and the last one is entirely lower (that is, the moment is already lost)
 */
public class Short_ScreenTwo_EMA_LastBarTooLow implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<EMAJava> screen_2_EMA13 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA13);
        QuoteJava quote3 = CollectionUtilsJava.getFromEnd(screen.quotes, 3);
        QuoteJava quote2 = CollectionUtilsJava.getFromEnd(screen.quotes, 2);
        QuoteJava quote1 = CollectionUtilsJava.getFromEnd(screen.quotes, 1);

        boolean thirdCrossesEMA13 = quote3.getLow() < CollectionUtilsJava.getFromEnd(screen_2_EMA13, 3).getValue()
                && quote3.getHigh() > CollectionUtilsJava.getFromEnd(screen_2_EMA13, 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < CollectionUtilsJava.getFromEnd(screen_2_EMA13, 2).getValue()
                && quote2.getHigh() > CollectionUtilsJava.getFromEnd(screen_2_EMA13, 2).getValue();
        boolean lastBelowEMA13 = quote1.getLow() < CollectionUtilsJava.getFromEnd(screen_2_EMA13, 1).getValue()
                && quote1.getHigh() < CollectionUtilsJava.getFromEnd(screen_2_EMA13, 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastBelowEMA13) {
            LogJava.recordCode(LAST_BAR_BELOW_SCREEN_2, screen);
            LogJava.addDebugLine("The third and second crossed the EMA13, and the last is completely below");
            return new BlockResultJava(screen.getLastQuote(), LAST_BAR_BELOW_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
