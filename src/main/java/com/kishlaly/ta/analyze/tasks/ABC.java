package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.TaskResult;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kishlaly.ta.analyze.TaskResultCode.*;
import static com.kishlaly.ta.model.indicators.Indicator.EMA26;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarCount;

/**
 * Работает только для Week-Day
 */
public class ABC {

    public static TaskResult buySignal(SymbolData screen_1, SymbolData screen_2) {
        int screen_1_MinBarCount = resolveMinBarCount(screen_1.timeframe);
        int screen_2_MinBarCount = resolveMinBarCount(screen_2.timeframe);

        if (screen_1.quotes.isEmpty() || screen_1.quotes.size() < screen_1_MinBarCount) {
            Log.recordCode(NO_DATA_QUOTES, screen_1);
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_1.timeframe.name());
            return new TaskResult(null, NO_DATA_QUOTES);
        }
        if (screen_2.quotes.isEmpty() || screen_2.quotes.size() < screen_2_MinBarCount) {
            Log.recordCode(NO_DATA_QUOTES, screen_2);
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_2.timeframe.name());
            return new TaskResult(null, NO_DATA_QUOTES);
        }

        List<Indicator> missingData = new ArrayList<>();
        screen_1.indicators.forEach((indicator, value) -> {
            if (value.isEmpty()) {
                missingData.add(indicator);
            }
        });
        screen_2.indicators.forEach((indicator, value) -> {
            if (value.isEmpty()) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(NO_DATA_INDICATORS, screen_1);
            Log.addDebugLine("Нету данных по индикаторам: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return new TaskResult(null, NO_DATA_INDICATORS);
        }

        // нужны 4 года
        List<Quote> screen_1_Quotes = screen_1.quotes.subList(screen_1.quotes.size() - 200, screen_1.quotes.size());
        List<Quote> screen_2_Quotes = screen_2.quotes.subList(screen_2.quotes.size() - screen_2_MinBarCount, screen_2.quotes.size());

        List<EMA> screen_1_EMA26 = screen_1.indicators.get(EMA26);
        screen_1_EMA26 = screen_1_EMA26.subList(screen_1_EMA26.size() - 200, screen_1_EMA26.size());

        Quote lastChartQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);

        if (screen_1_Quotes.get(199).getLow() >= screen_1_EMA26.get(199).getValue()) {
            Log.recordCode(QUOTE_LOW_NOT_BELOW_EMA, screen_1);
            Log.addDebugLine("Последняя недельная котировка не опустилась ниже ЕМА");
            return new TaskResult(lastChartQuote, QUOTE_LOW_NOT_BELOW_EMA);
        }

        // находим величину среднего отклонения от ЕМА26
        List<Double> priceToEMADivergencies = new ArrayList<>();
        for (int i = 0; i < screen_1_Quotes.size(); i++) {
            Quote quote = screen_1_Quotes.get(i);
            Double ema26 = screen_1_EMA26.get(i).getValue();
            if (quote.getHigh() > ema26) {
                priceToEMADivergencies.add(quote.getHigh() - ema26);
            }
            if (quote.getLow() < ema26) {
                priceToEMADivergencies.add(ema26 - quote.getLow());
            }
        }
        double averageDivergence = priceToEMADivergencies.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double averageDivergence75 = averageDivergence / 100 * 75;
        double currentDivergence = screen_1_EMA26.get(199).getValue() - screen_1_Quotes.get(199).getLow();
        boolean isMoreThanNeedeDivergence = currentDivergence > averageDivergence75;
        if (!isMoreThanNeedeDivergence) {
            Log.recordCode(DIVERGENCE_NOT_REACHED_PERCENTAGE, screen_1);
            Log.addDebugLine("Текущее отклонение от ЕМА не достигло уровня 75% от среднего за 4 года");
            return new TaskResult(lastChartQuote, DIVERGENCE_NOT_REACHED_PERCENTAGE);
        }

        return new TaskResult(lastChartQuote, SIGNAL);
    }

}
