package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * последние X гистограмм растут последовательно
 */
public class Long_ScreenOne_MACD_Last_X_Ascending implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        if (ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK < 0) {
            throw new RuntimeException("ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK not set");
        }
        List<MACD> screen_1_MACD = screen.indicators.get(Indicator.MACD);
        int count = 0;
        for (int i = screen_1_MACD.size() - ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK; i < screen_1_MACD.size() - 1; i++) {
            Double currentValue = screen_1_MACD.get(i).getHistogram();
            Double nextValue = screen_1_MACD.get(i + 1).getHistogram();
            if (currentValue < nextValue) {
                count++;
            }
        }

        if (count < ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Гистограмма не растет на долгосрочном экране");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
