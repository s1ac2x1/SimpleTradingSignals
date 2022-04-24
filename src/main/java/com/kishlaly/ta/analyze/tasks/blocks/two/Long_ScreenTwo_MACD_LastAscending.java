package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * последняя гистограмма растет
 */
public class Long_ScreenTwo_MACD_LastAscending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<MACD> screen_2_MACD = (List<MACD>) screen.indicators.get(Indicator.MACD);
        MACD screen_2_lastMACD = screen_2_MACD.get(screen_2_MACD.size() - 1);
        MACD screen_2_preLastMACD = screen_2_MACD.get(screen_2_MACD.size() - 2);

        boolean ascending = screen_2_lastMACD.getHistogram() > screen_2_preLastMACD.getHistogram();
        if (!ascending) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Гистограмма не растет на втором экране");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
