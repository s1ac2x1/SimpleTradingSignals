package com.kishlaly.ta.analyze.tasks.blocks.one;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCode.*;

/**
 * the last EMA26 is above
 * the last quote is green
 */
public class Long_ScreenOne_SoftTrendCheck implements ScreenOneBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMAJava> screen_1_EMA26 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA26);
        EMAJava ema2 = CollectionsTools.getFromEnd(screen_1_EMA26, 2);
        EMAJava ema1 = CollectionsTools.getFromEnd(screen_1_EMA26, 1);

        boolean ascending = ema2.getValue() < ema1.getValue();

        if (!ascending) {
            Log.recordCode(LAST_EMA_NOT_ASCENDING_SCREEN_1, screen);
            Log.addDebugLine("The last EMA is not higher on the long-term screen");
            return new BlockResult(screen.getLastQuote(), LAST_EMA_NOT_ASCENDING_SCREEN_1);
        }

        if (!Quotes.isGreen(screen.getLastQuote())) {
            Log.recordCode(QUOTE_NOT_GREEN_SCREEN_1, screen);
            Log.addDebugLine("The last quote is not green on the long-term screen");
            return new BlockResult(screen.getLastQuote(), QUOTE_NOT_GREEN_SCREEN_1);
        }

        return new BlockResult(screen.getLastQuote(), OK);
    }
}
