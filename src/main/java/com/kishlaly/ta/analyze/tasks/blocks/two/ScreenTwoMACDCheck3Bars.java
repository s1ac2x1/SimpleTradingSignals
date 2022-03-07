package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;

/**
 * гистограмма должна быть ниже нуля и начать повышаться на трех последних значениях
 */
public class ScreenTwoMACDCheck3Bars implements ScreenTwoBlock {

    @Override
    public BlockResult check(SymbolData screen) {
        List<MACD> screen_2_MACD = screen.indicators.get(Indicator.MACD);
        Double macd3 = screen_2_MACD.get(screen_2_MACD.size() - 3).getHistogram(); // 3 от правого края
        Double macd2 = screen_2_MACD.get(screen_2_MACD.size() - 2).getHistogram(); // 2 от правого края
        Double macd1 = screen_2_MACD.get(screen_2_MACD.size() - 1).getHistogram(); // последняя

        boolean histogramBelowZero = macd3 < 0 && macd2 < 0 && macd1 < 0;
        if (!histogramBelowZero) {
            Log.recordCode(HISTOGRAM_NOT_BELOW_ZERO, screen);
            Log.addDebugLine("Гистограмма на втором экране не ниже нуля");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_BELOW_ZERO);
        }

        boolean ascendingHistogram = macd3 < macd2 && macd2 < macd1;
        if (!ascendingHistogram) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING, screen);
            Log.addDebugLine("Гистограмма на втором экране не повышается");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
