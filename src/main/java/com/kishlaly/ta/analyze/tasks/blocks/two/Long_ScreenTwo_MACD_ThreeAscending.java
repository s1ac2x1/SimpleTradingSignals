package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.HISTOGRAM_NOT_ASCENDING_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * гистограмма повышается на трех последних значениях
 */
public class Long_ScreenTwo_MACD_ThreeAscending implements ScreenTwoBlock {

    @Override
    public BlockResult check(SymbolData screen) {
        List<MACD> screen_2_MACD = (List<MACD>) screen.indicators.get(Indicator.MACD);
        Double macd3 = CollectionsTools.getFromEnd(screen_2_MACD, 3).getHistogram(); // 3 от правого края
        Double macd2 = CollectionsTools.getFromEnd(screen_2_MACD, 2).getHistogram(); // 2 от правого края
        Double macd1 = CollectionsTools.getFromEnd(screen_2_MACD, 1).getHistogram(); // последняя

        boolean ascending = macd3 < macd2 && macd2 < macd1;
        if (!ascending) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen);
            Log.addDebugLine("Гистограмма на втором экране не повышается");
            return new BlockResult(screen.getLastQuote(), HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
