package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.LAST_BAR_BELOW_SCREEN_2;
import static com.kishlaly.ta.analyze.BlockResultCode.OK;

/**
 * to filter the situation when the third and second cross EMA13, and the last one is entirely lower (that is, the moment is already lost)
 */
public class Short_ScreenTwo_EMA_LastBarTooLow implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<EMA> screen_2_EMA13 = (List<EMA>) screen.indicators.get(Indicator.EMA13);
        QuoteJava quote3 = CollectionsTools.getFromEnd(screen.quotes, 3);
        QuoteJava quote2 = CollectionsTools.getFromEnd(screen.quotes, 2);
        QuoteJava quote1 = CollectionsTools.getFromEnd(screen.quotes, 1);

        boolean thirdCrossesEMA13 = quote3.getLow() < CollectionsTools.getFromEnd(screen_2_EMA13, 3).getValue()
                && quote3.getHigh() > CollectionsTools.getFromEnd(screen_2_EMA13, 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < CollectionsTools.getFromEnd(screen_2_EMA13, 2).getValue()
                && quote2.getHigh() > CollectionsTools.getFromEnd(screen_2_EMA13, 2).getValue();
        boolean lastBelowEMA13 = quote1.getLow() < CollectionsTools.getFromEnd(screen_2_EMA13, 1).getValue()
                && quote1.getHigh() < CollectionsTools.getFromEnd(screen_2_EMA13, 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastBelowEMA13) {
            Log.recordCode(LAST_BAR_BELOW_SCREEN_2, screen);
            Log.addDebugLine("The third and second crossed the EMA13, and the last is completely below");
            return new BlockResult(screen.getLastQuote(), LAST_BAR_BELOW_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
