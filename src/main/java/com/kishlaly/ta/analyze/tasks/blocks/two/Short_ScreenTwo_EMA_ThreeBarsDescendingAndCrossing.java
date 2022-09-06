package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultCode;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionsTools;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCode.*;

/**
 * price bars should cross EMA13 and should decrease
 */
public class Short_ScreenTwo_EMA_ThreeBarsDescendingAndCrossing implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        // make sure first that the last three columns are decreasing
        QuoteJava quote3 = CollectionsTools.getFromEnd(screen.quotes, 3);
        QuoteJava quote2 = CollectionsTools.getFromEnd(screen.quotes, 2);
        QuoteJava quote1 = CollectionsTools.getFromEnd(screen.quotes, 1);

        // descendingBarLow=false + descendingBarClose=false is enough to fail
        boolean descendingBarLow = quote3.getLow() > quote2.getLow() && quote2.getLow() < quote1.getLow();
        boolean descendingBarClose = quote3.getClose() > quote2.getClose() && quote2.getClose() > quote1.getClose();

        List<EMAJava> screen_2_EMA13 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA13);

        if (!descendingBarLow) {
            Log.recordCode(BlockResultCode.QUOTE_LOW_NOT_LOWING_SCREEN_2, screen);
            Log.addDebugLine("Quote.low is not reduced consistently");
            if (!descendingBarClose) {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_NOT_LOWING_SCREEN_2, screen);
                Log.addDebugLine("Quote.close is not reduced consistently");
                // the third from the end all above EMA13, and the second and last crossed it
                boolean thirdBarAboveEMA13 = quote3.getLow() > CollectionsTools.getFromEnd(screen_2_EMA13, 3).getValue()
                        && quote3.getHigh() > CollectionsTools.getFromEnd(screen_2_EMA13, 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= CollectionsTools.getFromEnd(screen_2_EMA13, 2).getValue()
                        && quote2.getHigh() >= CollectionsTools.getFromEnd(screen_2_EMA13, 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= CollectionsTools.getFromEnd(screen_2_EMA13, 1).getValue()
                        && quote1.getHigh() >= CollectionsTools.getFromEnd(screen_2_EMA13, 1).getValue();
                boolean crossingRule = thirdBarAboveEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    Log.addDebugLine("Third from the end" + (thirdBarAboveEMA13 ? " " : " not ") + "above ЕМА13");
                    Log.addDebugLine("Penultimate" + (secondBarCrossesEMA13 ? " " : " not ") + "crossed ЕМА13");
                    Log.addDebugLine("Last" + (lastBarCrossesEMA13 ? " " : " not ") + "crossed ЕМА13");
                    Log.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen);
                    return new BlockResult(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
                } else {
                    Log.recordCode(CROSSING_RULE_PASSED_SCREEN_2, screen);
                    Log.addDebugLine("The intersection rule is satisfied");
                }
            } else {
                Log.recordCode(QUOTE_CLOSE_LOWING_SCREEN_2, screen);
                Log.addDebugLine("There is a decrease in Quote.close");
            }
        } else {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_LOWING_SCREEN_2, screen);
            Log.addDebugLine("There is a drop in Quote.high");
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
