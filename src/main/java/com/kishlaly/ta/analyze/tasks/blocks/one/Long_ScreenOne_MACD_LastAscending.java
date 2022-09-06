package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.HISTOGRAM_NOT_ASCENDING_SCREEN_1;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * the last histogram grows
 */
public class Long_ScreenOne_MACD_LastAscending implements ScreenOneBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<MACDJava> screen_1_MACD = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        MACDJava screen_1_lastMACD = CollectionsTools.getFromEnd(screen_1_MACD, 1);
        MACDJava screen_1_preLastMACD = CollectionsTools.getFromEnd(screen_1_MACD, 2);

        boolean check2 = screen_1_lastMACD.getHistogram() > screen_1_preLastMACD.getHistogram();
        if (!check2) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_1, screen);
            Log.addDebugLine("The histogram does not grow on the long-term screen");
            return new BlockResultJava(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_1);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
