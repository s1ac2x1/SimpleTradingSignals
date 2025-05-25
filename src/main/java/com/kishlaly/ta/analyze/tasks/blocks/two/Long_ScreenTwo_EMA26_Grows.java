package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.utils.Quotes.isQuoteAboveEMA;

/**
 * EMA26 grows
 */
public class Long_ScreenTwo_EMA26_Grows implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_2_EMA26 = (List<EMA>) screen.indicators.get(Indicator.EMA26);
        EMA lastEma26 = CollectionsTools.getFromEnd(screen_2_EMA26, 1);
        EMA prevEma26 = CollectionsTools.getFromEnd(screen_2_EMA26, 2);
        boolean emaGrows = lastEma26.getValue() > prevEma26.getValue();

        if (!emaGrows) {
            Log.recordCode(EMA26_DOES_NOT_GROW_SCREEN_2, screen);
            Log.addDebugLine("EMA26 doesn't grow on screen2");
            return new BlockResult(screen.getLastQuote(), EMA26_DOES_NOT_GROW_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
