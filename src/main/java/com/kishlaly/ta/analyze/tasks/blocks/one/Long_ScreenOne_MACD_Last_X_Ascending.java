package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.BlockResultCode.X_HISTOGRAMS_NOT_ASCENDING_SCREEN_1;

/**
 * the last X histograms grow consecutively
 */
public class Long_ScreenOne_MACD_Last_X_Ascending implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        if (ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK < 0) {
            throw new RuntimeException("ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK not set");
        }
        List<MACD> screen_1_MACD = (List<MACD>) screen.indicators.get(Indicator.MACD);
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
