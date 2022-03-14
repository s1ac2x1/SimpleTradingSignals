package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.analyze.functions.TrendFunctions;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.utils.Log;

import static com.kishlaly.ta.analyze.BlockResultCode.NO_DOWNTREND_SCREEN_1;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.NUMBER_OF_EMA26_VALUES_TO_CHECK;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * Строгая проверка тренда с использованием котировок, EMA26 и MACD
 */
public class Short_ScreenOne_StrictTrendCheck implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        boolean downtrendCheckOnMultipleBars = TrendFunctions.downtrendCheckOnMultipleBars(screen, resolveMinBarsCount(screen.timeframe), NUMBER_OF_EMA26_VALUES_TO_CHECK);
        if (!downtrendCheckOnMultipleBars) {
            Log.recordCode(NO_DOWNTREND_SCREEN_1, screen);
            Log.addDebugLine("Не обнаружен нисходящий тренд на долгосрочном экране");
            return new BlockResult(screen.getLastQuote(), NO_DOWNTREND_SCREEN_1);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
