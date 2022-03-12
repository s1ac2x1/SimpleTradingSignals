package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;

/**
 * гистограмма должна быть выше нуля и начать снижаться: проверить на трех последних значениях
 */
public class Short_ScreenTwo_MACD_ThreeAboveZeroAndDescending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<MACD> screen_2_MACD = screen.indicators.get(Indicator.MACD);
        Double macd3 = screen_2_MACD.get(screen_2_MACD.size() - 3).getHistogram(); // 3 от правого края
        Double macd2 = screen_2_MACD.get(screen_2_MACD.size() - 2).getHistogram(); // 2 от правого края
        Double macd1 = screen_2_MACD.get(screen_2_MACD.size() - 1).getHistogram(); // последняя

        boolean histogramAboveZero = macd3 > 0 && macd2 > 0 && macd1 > 0;
        if (!histogramAboveZero) {
            Log.recordCode(HISTOGRAM_NOT_ABOVE_ZERO_SCREEN_2, screen);
            Log.addDebugLine("Гистограмма на втором экране не выше нуля");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ABOVE_ZERO_SCREEN_2);
        }

        boolean ascendingHistogram = macd3 > macd2 && macd2 > macd1;
        if (!ascendingHistogram) {
            Log.recordCode(HISTOGRAM_NOT_DESCENDING_SCREEN_2, screen);
            Log.addDebugLine("Гистограмма на втором экране не снижается");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_DESCENDING_SCREEN_2);
        }

        return new BlockResult(screen.getLastQuote(), OK);
    }
}
