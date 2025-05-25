package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Context;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * EMA50 over EMA200
 */
public class Long_ScreenOne_EMA50_Over_EMA200 implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_1_EMA50 = (List<EMA>) screen.indicators.get(Indicator.EMA50);
        List<EMA> screen_1_EMA200 = (List<EMA>) screen.indicators.get(Indicator.EMA200);

        Double lastEMA50 = CollectionsTools.getFromEnd(screen_1_EMA50, 1).getValue();
        Double lastEMA200 = CollectionsTools.getFromEnd(screen_1_EMA200, 1).getValue();

        Context.EMA50_OVER_EMA200 = lastEMA50 > lastEMA200;

        return new BlockResult(screen.getLastQuote(), OK);
    }
}
