package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.functions.TrendFunctionsJava;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.utils.LogJava;

import static com.kishlaly.ta.model.BlockResultCodeJava.NO_UPTREND_SCREEN_1;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.NUMBER_OF_EMA26_VALUES_TO_CHECK;
import static com.kishlaly.ta.utils.QuotesJava.resolveMinBarsCount;

/**
 * Strict trend check using quotes, EMA26 and MACD
 */
public class Long_ScreenOne_StrictTrendCheck implements ScreenOneBlockJava {

    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        boolean uptrendCheckOnMultipleBars = TrendFunctionsJava.uptrendCheckOnMultipleBars(screen, resolveMinBarsCount(screen.timeframe), NUMBER_OF_EMA26_VALUES_TO_CHECK);
        if (!uptrendCheckOnMultipleBars) {
            LogJava.recordCode(NO_UPTREND_SCREEN_1, screen);
            LogJava.addDebugLine("No uptrend detected on the long-term screen");
            return new BlockResultJava(null, NO_UPTREND_SCREEN_1);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }

}
