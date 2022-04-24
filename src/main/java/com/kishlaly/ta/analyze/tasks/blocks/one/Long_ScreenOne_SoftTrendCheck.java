package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;

/**
 * + последнее ЕМА26 выше
 * + последний столбик зеленый
 */
public class Long_ScreenOne_SoftTrendCheck implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_1_EMA26 = (List<EMA>) screen.indicators.get(Indicator.EMA26);
        EMA ema2 = CollectionsTools.getFromEnd(screen_1_EMA26, 2);
        EMA ema1 = CollectionsTools.getFromEnd(screen_1_EMA26, 1);

        boolean ascending = ema2.getValue() < ema1.getValue();

        if (!ascending) {
            Log.recordCode(LAST_EMA_NOT_ASCENDING_SCREEN_1, screen);
            Log.addDebugLine("Последнее ЕМА не выше на долгосрочном экране");
            return new BlockResult(screen.getLastQuote(), LAST_EMA_NOT_ASCENDING_SCREEN_1);
        }

        if (!Quotes.isGreen(screen.getLastQuote())) {
            Log.recordCode(QUOTE_NOT_GREEN_SCREEN_1, screen);
            Log.addDebugLine("Последняя котировка не зеленая на долгосрочном экране");
            return new BlockResult(screen.getLastQuote(), QUOTE_NOT_GREEN_SCREEN_1);
        }

        return new BlockResult(screen.getLastQuote(), OK);
    }
}
