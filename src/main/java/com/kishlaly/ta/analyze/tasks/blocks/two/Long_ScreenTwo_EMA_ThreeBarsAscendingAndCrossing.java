package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.BlockResultCode;
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
 * price bars should cross EMA13 and should go up
 */
public class Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        Quote quote3 = CollectionsTools.getFromEnd(screen.quotes, 3);
        Quote quote2 = CollectionsTools.getFromEnd(screen.quotes, 2);
        Quote quote1 = CollectionsTools.getFromEnd(screen.quotes, 1);
        List<EMA> screen_2_EMA13 = (List<EMA>) screen.indicators.get(Indicator.EMA13);

        // prerequisite 1:
        // make sure first that the last three columns increase

        boolean ascendingBarHigh = quote3.getHigh() < quote2.getHigh() && quote2.getHigh() < quote1.getHigh();
        boolean ascendingBarClose = quote3.getClose() < quote2.getClose() && quote2.getClose() < quote1.getClose();

        int screen_2_EMA13Count = screen_2_EMA13.size();
        if (!ascendingBarHigh) {
            Log.recordCode(QUOTE_HIGH_NOT_GROWING_SCREEN_2, screen);
            Log.addDebugLine("Quote.high не растет последовательно");
            if (!ascendingBarClose) {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_NOT_GROWING_SCREEN_2, screen);
                Log.addDebugLine("Quote.close does not grow consistently");
                // the third from the end all below EMA13, and the second and last crossed
                boolean thirdBarBelowEMA13 = quote3.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue()
                        && quote3.getHigh() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue()
                        && quote2.getHigh() >= screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue()
                        && quote1.getHigh() >= screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue();
                boolean crossingRule = thirdBarBelowEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    Log.addDebugLine("Third from the end" + (thirdBarBelowEMA13 ? " " : " not ") + "below ЕМА13");
                    Log.addDebugLine("Penultimate" + (secondBarCrossesEMA13 ? " " : " not ") + "crossed ЕМА13");
                    Log.addDebugLine("Last" + (lastBarCrossesEMA13 ? " " : " not ") + "crossed ЕМА13");
                    Log.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen);
                    return new BlockResult(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
                } else {
                    Log.recordCode(BlockResultCode.CROSSING_RULE_PASSED_SCREEN_2, screen);
                    Log.addDebugLine("The intersection rule is satisfied");
                }
            } else {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_GROWING_SCREEN_2, screen);
                Log.addDebugLine("There is an increase in Quote.close");
            }
        } else {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_GROWING_SCREEN_2, screen);
            Log.addDebugLine("There is a rise in Quote.high");
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
