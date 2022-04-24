package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * из последних TOTAL стобиков как минимум ABOVE полностью выше ЕМА26
 */
public class Long_ScreenOne_EMA_X_OutOf_Y_Above implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        if (ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK < 0) {
            throw new RuntimeException("ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK not set");
        }
        if (ThreeDisplays.Config.EMA26_ABOVE_BARS < 0) {
            throw new RuntimeException("ThreeDisplays.Config.EMA26_ABOVE_BARS not set");
        }

        List<EMA> screen_1_EMA26 = (List<EMA>) screen.indicators.get(Indicator.EMA26);
        int aboveEMA26 = 0;
        for (int i = screen_1_EMA26.size() - ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK; i < screen_1_EMA26.size(); i++) {
            if (Quotes.isQuoteAboveEMA(screen.quotes.get(i), screen_1_EMA26.get(i).getValue())) {
                aboveEMA26++;
            }
        }
        if (aboveEMA26 < ThreeDisplays.Config.EMA26_ABOVE_BARS) {
            Log.recordCode(NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1, screen);
            Log.addDebugLine("Из последних " + ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK + " стобиков " + ThreeDisplays.Config.EMA26_ABOVE_BARS + " не находятся выше ЕМА26 на долгосрочном экране");
            return new BlockResult(screen.getLastQuote(), NOT_ALL_NEEDED_QUOTES_ABOUT_EMA_SCREEN_1);
        }

        return new BlockResult(screen.getLastQuote(), OK);
    }
}
