package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultCodeJava;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.utils.CollectionUtilsJava;
import com.kishlaly.ta.utils.LogJava;

import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.*;

/**
 * price bars should cross EMA13 and should decrease
 */
public class Short_ScreenTwo_EMA_ThreeBarsDescendingAndCrossing implements ScreenTwoBlockJava {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        // make sure first that the last three columns are decreasing
        QuoteJava quote3 = CollectionUtilsJava.getFromEnd(screen.quotes, 3);
        QuoteJava quote2 = CollectionUtilsJava.getFromEnd(screen.quotes, 2);
        QuoteJava quote1 = CollectionUtilsJava.getFromEnd(screen.quotes, 1);

        // descendingBarLow=false + descendingBarClose=false is enough to fail
        boolean descendingBarLow = quote3.getLow() > quote2.getLow() && quote2.getLow() < quote1.getLow();
        boolean descendingBarClose = quote3.getClose() > quote2.getClose() && quote2.getClose() > quote1.getClose();

        List<EMAJava> screen_2_EMA13 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA13);

        if (!descendingBarLow) {
            LogJava.recordCode(BlockResultCodeJava.QUOTE_LOW_NOT_LOWING_SCREEN_2, screen);
            LogJava.addDebugLine("Quote.low is not reduced consistently");
            if (!descendingBarClose) {
                LogJava.recordCode(BlockResultCodeJava.QUOTE_CLOSE_NOT_LOWING_SCREEN_2, screen);
                LogJava.addDebugLine("Quote.close is not reduced consistently");
                // the third from the end all above EMA13, and the second and last crossed it
                boolean thirdBarAboveEMA13 = quote3.getLow() > CollectionUtilsJava.getFromEnd(screen_2_EMA13, 3).getValue()
                        && quote3.getHigh() > CollectionUtilsJava.getFromEnd(screen_2_EMA13, 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= CollectionUtilsJava.getFromEnd(screen_2_EMA13, 2).getValue()
                        && quote2.getHigh() >= CollectionUtilsJava.getFromEnd(screen_2_EMA13, 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= CollectionUtilsJava.getFromEnd(screen_2_EMA13, 1).getValue()
                        && quote1.getHigh() >= CollectionUtilsJava.getFromEnd(screen_2_EMA13, 1).getValue();
                boolean crossingRule = thirdBarAboveEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    LogJava.addDebugLine("Third from the end" + (thirdBarAboveEMA13 ? " " : " not ") + "above ЕМА13");
                    LogJava.addDebugLine("Penultimate" + (secondBarCrossesEMA13 ? " " : " not ") + "crossed ЕМА13");
                    LogJava.addDebugLine("Last" + (lastBarCrossesEMA13 ? " " : " not ") + "crossed ЕМА13");
                    LogJava.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen);
                    return new BlockResultJava(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
                } else {
                    LogJava.recordCode(CROSSING_RULE_PASSED_SCREEN_2, screen);
                    LogJava.addDebugLine("The intersection rule is satisfied");
                }
            } else {
                LogJava.recordCode(QUOTE_CLOSE_LOWING_SCREEN_2, screen);
                LogJava.addDebugLine("There is a decrease in Quote.close");
            }
        } else {
            LogJava.recordCode(BlockResultCodeJava.QUOTE_HIGH_LOWING_SCREEN_2, screen);
            LogJava.addDebugLine("There is a drop in Quote.high");
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
