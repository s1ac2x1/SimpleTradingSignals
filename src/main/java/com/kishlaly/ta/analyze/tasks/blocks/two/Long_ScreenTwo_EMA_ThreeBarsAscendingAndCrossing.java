package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * ценовые бары должны пересекать ЕМА13 и должны подниматься
 */
public class Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        Quote quote3 = screen.quotes.get(resolveMinBarsCount(screen.timeframe) - 3);
        Quote quote2 = screen.quotes.get(resolveMinBarsCount(screen.timeframe) - 2);
        Quote quote1 = screen.quotes.get(resolveMinBarsCount(screen.timeframe) - 1);
        List<EMA> screen_2_EMA13 = screen.indicators.get(Indicator.EMA13);

        // обязательное условие 1
        // убедиться сначала, что high у последних трех столбиков повышается

        // наверно ascendingBarHigh=false + ascendingBarClose=false достаточно для отказа
        boolean ascendingBarHigh = quote3.getHigh() < quote2.getHigh() && quote2.getHigh() < quote1.getHigh();
        boolean ascendingBarClose = quote3.getClose() < quote2.getClose() && quote2.getClose() < quote1.getClose();

        int screen_2_EMA13Count = screen_2_EMA13.size();
        if (!ascendingBarHigh) {
            Log.recordCode(QUOTE_HIGH_NOT_GROWING_SCREEN_2, screen);
            Log.addDebugLine("Quote.high не растет последовательно");
            if (!ascendingBarClose) {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_NOT_GROWING_SCREEN_2, screen);
                Log.addDebugLine("Quote.close не растет последовательно");
                // третий с конца весь ниже ЕМА13, а второй и последний пересекли
                boolean thirdBarBelowEMA13 = quote3.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue()
                        && quote3.getHigh() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue()
                        && quote2.getHigh() >= screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue()
                        && quote1.getHigh() >= screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue();
                boolean crossingRule = thirdBarBelowEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    Log.addDebugLine("Третий с конца" + (thirdBarBelowEMA13 ? " " : " не ") + "ниже ЕМА13");
                    Log.addDebugLine("Предпоследний" + (secondBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.addDebugLine("Последний" + (lastBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen);
                    return new BlockResult(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
                } else {
                    Log.recordCode(BlockResultCode.CROSSING_RULE_PASSED_SCREEN_2, screen);
                    Log.addDebugLine("Правило пересечения выполняется");
                }
            } else {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_GROWING_SCREEN_2, screen);
                Log.addDebugLine("Есть рост Quote.close");
            }
        } else {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_GROWING_SCREEN_2, screen);
            Log.addDebugLine("Есть рост Quote.high");
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
