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
 * price bars should cross EMA13 and should go up
 */
public class Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolDataJava screen) {
        QuoteJava quote3 = CollectionUtilsJava.getFromEnd(screen.quotes, 3);
        QuoteJava quote2 = CollectionUtilsJava.getFromEnd(screen.quotes, 2);
        QuoteJava quote1 = CollectionUtilsJava.getFromEnd(screen.quotes, 1);
        List<EMAJava> screen_2_EMA13 = (List<EMAJava>) screen.indicators.get(IndicatorJava.EMA13);

        // prerequisite 1:
        // make sure first that the last three columns increase

        boolean ascendingBarHigh = quote3.getHigh() < quote2.getHigh() && quote2.getHigh() < quote1.getHigh();
        boolean ascendingBarClose = quote3.getClose() < quote2.getClose() && quote2.getClose() < quote1.getClose();

        int screen_2_EMA13Count = screen_2_EMA13.size();
        if (!ascendingBarHigh) {
            LogJava.recordCode(QUOTE_HIGH_NOT_GROWING_SCREEN_2, screen);
            LogJava.addDebugLine("Quote.high не растет последовательно");
            if (!ascendingBarClose) {
                LogJava.recordCode(BlockResultCodeJava.QUOTE_CLOSE_NOT_GROWING_SCREEN_2, screen);
                LogJava.addDebugLine("Quote.close does not grow consistently");
                // the third from the end all below EMA13, and the second and last crossed
                boolean thirdBarBelowEMA13 = quote3.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue()
                        && quote3.getHigh() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue()
                        && quote2.getHigh() >= screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue()
                        && quote1.getHigh() >= screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue();
                boolean crossingRule = thirdBarBelowEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    LogJava.addDebugLine("Third from the end" + (thirdBarBelowEMA13 ? " " : " not ") + "below ЕМА13");
                    LogJava.addDebugLine("Penultimate" + (secondBarCrossesEMA13 ? " " : " not ") + "crossed ЕМА13");
                    LogJava.addDebugLine("Last" + (lastBarCrossesEMA13 ? " " : " not ") + "crossed ЕМА13");
                    LogJava.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen);
                    return new BlockResultJava(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
                } else {
                    LogJava.recordCode(BlockResultCodeJava.CROSSING_RULE_PASSED_SCREEN_2, screen);
                    LogJava.addDebugLine("The intersection rule is satisfied");
                }
            } else {
                LogJava.recordCode(BlockResultCodeJava.QUOTE_CLOSE_GROWING_SCREEN_2, screen);
                LogJava.addDebugLine("There is an increase in Quote.close");
            }
        } else {
            LogJava.recordCode(BlockResultCodeJava.QUOTE_HIGH_GROWING_SCREEN_2, screen);
            LogJava.addDebugLine("There is a rise in Quote.high");
        }
        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
