package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_QUOTE_NOT_CROSSING_EMA;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;
import static com.kishlaly.ta.utils.Quotes.isQuoteCrossedEMA;

/**
 * последний столбик пересекает ЕМА26
 */
public class Long_ScreenOne_EMA_LastBarCrosses implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_1_EMA26 = screen.indicators.get(Indicator.EMA26);
        if (!isQuoteCrossedEMA(screen.getLastQuote(), screen_1_EMA26.get(screen_1_EMA26.size() - 1).getValue())) {
            Log.recordCode(LAST_QUOTE_NOT_CROSSING_EMA, screen);
            Log.addDebugLine("Последний столбик не пересекает ЕМА на долгосрочном экране");
            return new BlockResult(screen.getLastQuote(), LAST_QUOTE_NOT_CROSSING_EMA);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}