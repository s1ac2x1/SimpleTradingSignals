package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.utils.Log;

import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.analyze.BlockResultCode.CROSSING_RULE_VIOLATED_SCREEN_2;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * ценовые бары должны пересекать ЕМА13 и должны подниматься
 */
public class ScreenTwo_EMA_TwoBarsAscendingAndCrossing implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        List<Quote> screen_2_Quotes = screen.quotes;
        // обязательное условие 1
        // убедиться сначала, что high у последних ДВУХ столбиков повышается
        Quote preLastQuote = screen_2_Quotes.get(resolveMinBarsCount(screen.timeframe) - 2);
        Quote lastQuote = screen_2_Quotes.get(resolveMinBarsCount(screen.timeframe) - 1);
        boolean ascendingBarHigh = preLastQuote.getHigh() < lastQuote.getHigh();
        if (!ascendingBarHigh) {
            Log.recordCode(QUOTE_HIGH_NOT_GROWING_SCREEN_2, screen);
            Log.addDebugLine("Quote.high не растет последовательно");
            return new BlockResult(screen.getLastQuote(), QUOTE_HIGH_NOT_GROWING_SCREEN_2);
        }
        EMA preLastEMA = screen_2_EMA13.get(resolveMinBarsCount(screen.timeframe) - 2);
        EMA lastEMA = screen_2_EMA13.get(resolveMinBarsCount(screen.timeframe) - 1);

        // оба столбика ниже ЕМА - отказ
        if (isQuoteBelowEMA(preLastQuote, preLastEMA.getValue()) && isQuoteBelowEMA(lastQuote, lastEMA.getValue())) {
            Log.addDebugLine("Оба последних столбика ниже ЕМА");
            Log.recordCode(QUOTES_BELOW_EMA, screen_2);
            return new BlockResult(lastChartQuote, QUOTES_BELOW_EMA);
        }

        // оба столбика выше ЕМА - отказ
        if (isQuoteAboveEMA(preLastQuote, preLastEMA.getValue()) && isQuoteAboveEMA(lastQuote, lastEMA.getValue())) {
            Log.addDebugLine("Оба последних столбика выше ЕМА");
            Log.recordCode(QUOTES_ABOVE_EMA, screen_2);
            return new BlockResult(lastChartQuote, QUOTES_ABOVE_EMA);
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
            Log.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen_2);
            return new BlockResult(lastChartQuote, CROSSING_RULE_VIOLATED_SCREEN_2);
        }
        return new BlockResult(screen.getLastQuote(), CROSSING_RULE_VIOLATED_SCREEN_2);
    }
}
