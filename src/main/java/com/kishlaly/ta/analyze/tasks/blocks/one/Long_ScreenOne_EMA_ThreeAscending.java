package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.utils.Quotes.isQuoteCrossedEMA;

/**
 * три последних значения растут
 */
public class Long_ScreenOne_EMA_ThreeAscending implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_1_EMA26 = screen.indicators.get(Indicator.EMA26);
        EMA ema3 = screen_1_EMA26.get(screen_1_EMA26.size() - 3);
        EMA ema2 = screen_1_EMA26.get(screen_1_EMA26.size() - 2);
        EMA ema1 = screen_1_EMA26.get(screen_1_EMA26.size() - 1);

        boolean ascending = ema3.getValue() < ema2.getValue() && ema2.getValue() < ema1.getValue();

        if (!ascending) {
            Log.recordCode(THREE_EMA_NOT_ASCENDING_SCREEN_1, screen);
            Log.addDebugLine("Три значения ЕМА не растут на долгосрочном экране");
            return new BlockResult(screen.getLastQuote(), THREE_EMA_NOT_ASCENDING_SCREEN_1);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
