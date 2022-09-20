package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_BAR_ABOVE_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * it is necessary to filter the situation when the third and second cross EMA13, and the last one is entirely higher (the moment of entering the trade is missed)
 * the third can open and close higher, and this is acceptable: https://drive.google.com/file/d/15XkXFKBQbTjeNjBn03NrF9JawCBFaO5t/view?usp=sharing
 */
public class Long_ScreenTwo_EMA_LastBarTooHighJava implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        QuoteJava quote3 = CollectionUtilsJava.getFromEnd(screen.quotes, 3);
        QuoteJava quote2 = CollectionUtilsJava.getFromEnd(screen.quotes, 2);
        QuoteJava quote1 = CollectionUtilsJava.getFromEnd(screen.quotes, 1);

        List<EMAJava> screen_2_EMA13 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA13);
        int screen_2_EMA13Count = screen_2_EMA13.size();

        boolean thirdCrossesEMA13 = quote3.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue()
                && quote3.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue()
                && quote2.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue();
        boolean lastAboveEMA13 = quote1.getLow() > screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue()
                && quote1.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastAboveEMA13) {
            LogJava.recordCode(LAST_BAR_ABOVE_SCREEN_2, screen);
            LogJava.addDebugLine("The third and second crossed the EMA13, and the last is completely above");
            return new BlockResultJava(screen.getLastQuote(), LAST_BAR_ABOVE_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
