package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.X_HISTOGRAMS_NOT_ASCENDING_SCREEN_1;

/**
 * the last X histograms grow consecutively
 */
public class Long_ScreenOne_MACD_Last_X_Ascending implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        if (ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK < 0) {
            throw new RuntimeException("ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK not set");
        }
        List<MACDJava> screen_1_MACD = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        int count = 0;
        for (int i = screen_1_MACD.size() - ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK; i < screen_1_MACD.size() - 1; i++) {
            Double currentValue = screen_1_MACD.get(i).getHistogram();
            Double nextValue = screen_1_MACD.get(i + 1).getHistogram();
            if (currentValue < nextValue) {
                count++;
            }
        }

        if (count < ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK) {
            Log.recordCode(X_HISTOGRAMS_NOT_ASCENDING_SCREEN_1, screen);
            Log.addDebugLine(ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK + " histograms do not grow on the long-term screen");
            return new BlockResult(screen.getLastQuote(), X_HISTOGRAMS_NOT_ASCENDING_SCREEN_1);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }

}
