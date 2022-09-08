package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.LogJava;
import com.kishlaly.ta.utils.QuotesJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1;
import static com.kishlaly.ta.model.BlockResultCodeJava.OK;

/**
 * of the latest {TOTAL} quotes at least {ABOVE} is completely above EMA26
 */
public class Long_ScreenOne_EMA_X_OutOf_Y_Above implements ScreenOneBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        if (ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK < 0) {
            throw new RuntimeException("ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK not set");
        }
        if (ThreeDisplays.Config.EMA26_ABOVE_BARS < 0) {
            throw new RuntimeException("ThreeDisplays.Config.EMA26_ABOVE_BARS not set");
        }

        List<EMAJava> screen_1_EMA26 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA26);
        int aboveEMA26 = 0;
        for (int i = screen_1_EMA26.size() - ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK; i < screen_1_EMA26.size(); i++) {
            if (QuotesJava.isQuoteAboveEMA(screen.quotes.get(i), screen_1_EMA26.get(i).getValue())) {
                aboveEMA26++;
            }
        }
        if (aboveEMA26 < ThreeDisplays.Config.EMA26_ABOVE_BARS) {
            LogJava.recordCode(NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1, screen);
            LogJava.addDebugLine("Out of the last " + ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK + " quotes " + ThreeDisplays.Config.EMA26_ABOVE_BARS + " are not above EMA26 on the long term screen");
            return new BlockResultJava(screen.getLastQuote(), NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1);
        }

        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
