package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.OK;
import static com.kishlaly.ta.model.BlockResultCodeJava.THREE_EMA_NOT_ASCENDING_SCREEN_1;

/**
 * the last three EMA are increasing
 */
public class Long_ScreenOne_EMA_ThreeAscending implements ScreenOneBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        List<EMAJava> screen_1_EMA26 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA26);
        EMAJava ema3 = CollectionsTools.getFromEnd(screen_1_EMA26, 3);
        EMAJava ema2 = CollectionsTools.getFromEnd(screen_1_EMA26, 2);
        EMAJava ema1 = CollectionsTools.getFromEnd(screen_1_EMA26, 1);

        boolean ascending = ema3.getValue() < ema2.getValue() && ema2.getValue() < ema1.getValue();

        if (!ascending) {
            Log.recordCode(THREE_EMA_NOT_ASCENDING_SCREEN_1, screen);
            Log.addDebugLine("Three values of the EMA do not grow on the long-term screen");
            return new BlockResultJava(screen.getLastQuote(), THREE_EMA_NOT_ASCENDING_SCREEN_1);
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
