package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultCodeJava;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

public class Long_ScreenTwo_MACD_LastShouldBeNegative implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<MACDJava> screenTwoMacdValues = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        double latestHistogramValue = CollectionUtilsJava.getFromEnd(screenTwoMacdValues, 1).getHistogram();

        if (latestHistogramValue > 0) {
            LogJava.recordCode(BlockResultCodeJava.LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2, screen);
            LogJava.addDebugLine("histogram at the right edge above zero");
            return new BlockResultJava(screen.getLastQuote(), LAST_HISTOGRAM_ABOVE_ZERO_SCREEN_2);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
