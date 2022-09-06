package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCode.*;

/**
 * histogram should be above zero and start to decrease: check on the last three values
 */
public class Short_ScreenTwo_MACD_ThreeAboveZeroAndDescending implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<MACDJava> screen_2_MACD = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);
        Double macd3 = screen_2_MACD.get(screen_2_MACD.size() - 3).getHistogram();
        Double macd2 = screen_2_MACD.get(screen_2_MACD.size() - 2).getHistogram();
        Double macd1 = screen_2_MACD.get(screen_2_MACD.size() - 1).getHistogram();

        boolean histogramAboveZero = macd3 > 0 && macd2 > 0 && macd1 > 0;
        if (!histogramAboveZero) {
            Log.recordCode(HISTOGRAM_NOT_ABOVE_ZERO_SCREEN_2, screen);
            Log.addDebugLine("Histogram on the second screen is not higher than zero");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ABOVE_ZERO_SCREEN_2);
        }

        boolean descending = macd3 > macd2 && macd2 > macd1;
        if (!descending) {
            Log.recordCode(HISTOGRAM_NOT_DESCENDING_SCREEN_2, screen);
            Log.addDebugLine("The histogram on the second screen is not reduced");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_DESCENDING_SCREEN_2);
        }

        return new BlockResult(screen.getLastQuote(), OK);
    }
}
