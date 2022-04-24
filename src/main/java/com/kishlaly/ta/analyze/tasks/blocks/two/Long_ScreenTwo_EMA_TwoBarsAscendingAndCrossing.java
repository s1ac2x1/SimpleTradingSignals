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
import static com.kishlaly.ta.utils.Quotes.*;

/**
 * ценовые бары должны пересекать ЕМА13 и должны подниматься
 */
public class Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Quote> screen_2_Quotes = screen.quotes;
        List<EMA> screen_2_EMA13 = (List<EMA>) screen.indicators.get(Indicator.EMA13);
        // обязательное условие 1
        // убедиться сначала, что high у последних ДВУХ столбиков повышается
        Quote preLastQuote = CollectionsTools.getFromEnd(screen_2_Quotes, 2);
        Quote lastQuote = CollectionsTools.getFromEnd(screen_2_Quotes, 1);
        boolean ascendingBarHigh = preLastQuote.getHigh() < lastQuote.getHigh();
        if (!ascendingBarHigh) {
            Log.recordCode(QUOTE_HIGH_NOT_GROWING_SCREEN_2, screen);
            Log.addDebugLine("Quote.high не растет последовательно");
            return new BlockResult(screen.getLastQuote(), QUOTE_HIGH_NOT_GROWING_SCREEN_2);
        }
        EMA preLastEMA = CollectionsTools.getFromEnd(screen_2_EMA13, 2);
        EMA lastEMA = CollectionsTools.getFromEnd(screen_2_EMA13, 1);

        // оба столбика ниже ЕМА - отказ
        if (isQuoteBelowEMA(preLastQuote, preLastEMA.getValue()) && isQuoteBelowEMA(lastQuote, lastEMA.getValue())) {
            Log.addDebugLine("Оба последних столбика ниже ЕМА");
            Log.recordCode(QUOTES_BELOW_EMA_SCREEN_2, screen);
            return new BlockResult(screen.getLastQuote(), QUOTES_BELOW_EMA_SCREEN_2);
        }

        // оба столбика выше ЕМА - отказ
        if (isQuoteAboveEMA(preLastQuote, preLastEMA.getValue()) && isQuoteAboveEMA(lastQuote, lastEMA.getValue())) {
            Log.addDebugLine("Оба последних столбика выше ЕМА");
            Log.recordCode(QUOTES_ABOVE_EMA_SCREEN_2, screen);
            return new BlockResult(screen.getLastQuote(), QUOTES_ABOVE_EMA_SCREEN_2);
        }

        // предпоследний ниже ЕМА, последний пересекает или выше - ОК
        boolean crossingRule1 = isQuoteBelowEMA(preLastQuote, preLastEMA.getValue())
                && (isQuoteCrossedEMA(lastQuote, lastEMA.getValue()) || isQuoteAboveEMA(lastQuote, lastEMA.getValue()));

        // предпоследний ниже ЕМА, последний пересекает - ОК
        boolean crossingRule2 = isQuoteBelowEMA(preLastQuote, preLastEMA.getValue()) && isQuoteCrossedEMA(lastQuote, lastEMA.getValue());

        // предпоследний и последний пересекают ЕМА - ОК
        boolean crossingRule3 = isQuoteCrossedEMA(preLastQuote, preLastEMA.getValue()) && isQuoteCrossedEMA(lastQuote, lastEMA.getValue());

        // предпоследний пересекает ЕМА, последний выше (может быть поздно входить в сделку, нужно смотреть на график) - ОК
        boolean crossingRule4 = isQuoteCrossedEMA(preLastQuote, preLastEMA.getValue()) && isQuoteAboveEMA(lastQuote, lastEMA.getValue());

        boolean crossingOk = crossingRule1 || crossingRule2 || crossingRule3 || crossingRule4;
        if (!crossingOk) {
            Log.addDebugLine("Не выполняется правило пересечения ЕМА");
            Log.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen);
            return new BlockResult(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), OK);
    }
}
