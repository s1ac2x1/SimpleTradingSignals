package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;

/**
 * EMA13 > EMA26, EMA26 ↑, Close > EMA26
 */
public class Long_ScreenTwo_StrictTrendCheck implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_2_EMA13 = (List<EMA>) screen.indicators.get(Indicator.EMA13);
        List<EMA> screen_2_EMA26 = (List<EMA>) screen.indicators.get(Indicator.EMA26);
        EMA lastEma13 = CollectionsTools.getFromEnd(screen_2_EMA13, 1);
        EMA lastEma26 = CollectionsTools.getFromEnd(screen_2_EMA26, 1);
        EMA prevEma26 = CollectionsTools.getFromEnd(screen_2_EMA26, 2);
        List<Quote> screen_2_Quotes = screen.quotes;
        Quote quote1 = CollectionsTools.getFromEnd(screen_2_Quotes, 1);

        boolean check1 = lastEma13.getValue() > lastEma26.getValue();
        boolean check2 = lastEma26.getValue() > prevEma26.getValue();
        boolean check3 = quote1.getClose() > lastEma26.getValue();

        if (check1 && check2 && check3) {
            Log.recordCode(STRICT_TREND_CHECK_FAILED_SCREEN_2, screen);
            Log.addDebugLine("Failed this check on screen 2: EMA13 > EMA26, EMA26 ↑, Close > EMA26");
            return new BlockResult(screen.getLastQuote(), STRICT_TREND_CHECK_FAILED_SCREEN_2);
        }

        return new BlockResult(screen.getLastQuote(), OK);
    }
}
