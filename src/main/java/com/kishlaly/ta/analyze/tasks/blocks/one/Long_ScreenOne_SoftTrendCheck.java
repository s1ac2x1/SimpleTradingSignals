package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;
import com.kishlaly.ta.utils.QuotesJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.*;

/**
 * the last EMA26 is above
 * the last quote is green
 */
public class Long_ScreenOne_SoftTrendCheck implements ScreenOneBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<EMAJava> screen_1_EMA26 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA26);
        EMAJava ema2 = CollectionUtilsJava.getFromEnd(screen_1_EMA26, 2);
        EMAJava ema1 = CollectionUtilsJava.getFromEnd(screen_1_EMA26, 1);

        boolean ascending = ema2.getValue() < ema1.getValue();

        if (!ascending) {
            LogJava.recordCode(LAST_EMA_NOT_ASCENDING_SCREEN_1, screen);
            LogJava.addDebugLine("The last EMA is not higher on the long-term screen");
            return new BlockResultJava(screen.getLastQuote(), LAST_EMA_NOT_ASCENDING_SCREEN_1);
        }

        if (!QuotesJava.isGreen(screen.getLastQuote())) {
            LogJava.recordCode(QUOTE_NOT_GREEN_SCREEN_1, screen);
            LogJava.addDebugLine("The last quote is not green on the long-term screen");
            return new BlockResultJava(screen.getLastQuote(), QUOTE_NOT_GREEN_SCREEN_1);
        }

        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
