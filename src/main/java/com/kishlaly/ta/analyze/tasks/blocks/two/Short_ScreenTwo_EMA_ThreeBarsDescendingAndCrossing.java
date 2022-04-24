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
 * ценовые бары должны пересекать ЕМА13 и должны снижаться
 */
public class Short_ScreenTwo_EMA_ThreeBarsDescendingAndCrossing implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        // убедиться сначала, что low у последних трех столбиков снижается
        Quote quote3 = CollectionsTools.getFromEnd(screen.quotes, 3);
        Quote quote2 = CollectionsTools.getFromEnd(screen.quotes, 2);
        Quote quote1 = CollectionsTools.getFromEnd(screen.quotes, 1);
        // наверно descendingBarLow=false + descendingBarClose=false достаточно для отказа
        boolean descendingBarLow = quote3.getLow() > quote2.getLow() && quote2.getLow() < quote1.getLow();
        boolean descendingBarClose = quote3.getClose() > quote2.getClose() && quote2.getClose() > quote1.getClose();

        List<EMA> screen_2_EMA13 = (List<EMA>) screen.indicators.get(Indicator.EMA13);

        if (!descendingBarLow) {
            Log.recordCode(BlockResultCode.QUOTE_LOW_NOT_LOWING_SCREEN_2, screen);
            Log.addDebugLine("Quote.low не снижается последовательно");
            if (!descendingBarClose) {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_NOT_LOWING_SCREEN_2, screen);
                Log.addDebugLine("Quote.close не снижается последовательно");
                // третий с конца весь выше ЕМА13, а второй и последний пересекли ее
                boolean thirdBarAboveEMA13 = quote3.getLow() > CollectionsTools.getFromEnd(screen_2_EMA13, 3).getValue()
                        && quote3.getHigh() > CollectionsTools.getFromEnd(screen_2_EMA13, 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= CollectionsTools.getFromEnd(screen_2_EMA13, 2).getValue()
                        && quote2.getHigh() >= CollectionsTools.getFromEnd(screen_2_EMA13, 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= CollectionsTools.getFromEnd(screen_2_EMA13, 1).getValue()
                        && quote1.getHigh() >= CollectionsTools.getFromEnd(screen_2_EMA13, 1).getValue();
                boolean crossingRule = thirdBarAboveEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    Log.addDebugLine("Третий с конца" + (thirdBarAboveEMA13 ? " " : " не ") + "выше ЕМА13");
                    Log.addDebugLine("Предпоследний" + (secondBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.addDebugLine("Последний" + (lastBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen);
                    return new BlockResult(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
                } else {
                    Log.recordCode(CROSSING_RULE_PASSED_SCREEN_2, screen);
                    Log.addDebugLine("Правило пересечения выполняется");
                }
            } else {
                Log.recordCode(QUOTE_CLOSE_LOWING_SCREEN_2, screen);
                Log.addDebugLine("Есть снижение Quote.close");
            }
        } else {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_LOWING_SCREEN_2, screen);
            Log.addDebugLine("Есть снижение Quote.high");
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
