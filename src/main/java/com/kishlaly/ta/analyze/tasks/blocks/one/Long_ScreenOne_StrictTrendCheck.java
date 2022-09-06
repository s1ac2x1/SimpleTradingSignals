package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.functions.TrendFunctions;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.model.BlockResultCodeJava.NO_UPTREND_SCREEN_1;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.NUMBER_OF_EMA26_VALUES_TO_CHECK;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * Strict trend check using quotes, EMA26 and MACD
 */
public class Long_ScreenOne_StrictTrendCheck implements ScreenOneBlock {

    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        boolean uptrendCheckOnMultipleBars = TrendFunctions.uptrendCheckOnMultipleBars(screen, resolveMinBarsCount(screen.timeframe), NUMBER_OF_EMA26_VALUES_TO_CHECK);
        if (!uptrendCheckOnMultipleBars) {
            Log.recordCode(NO_UPTREND_SCREEN_1, screen);
            Log.addDebugLine("No uptrend detected on the long-term screen");
            return new BlockResultJava(null, NO_UPTREND_SCREEN_1);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }

}
