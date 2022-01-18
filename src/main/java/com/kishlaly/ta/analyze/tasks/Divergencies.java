package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.TaskResultCode;
import com.kishlaly.ta.analyze.functions.TrendFunctions;
import com.kishlaly.ta.model.HistogramQuote;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.TaskResult;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kishlaly.ta.analyze.TaskResultCode.*;
import static com.kishlaly.ta.analyze.tasks.Divergencies.BullishConfig.ALLOW_ON_BEARISH_TREND;
import static com.kishlaly.ta.analyze.tasks.Divergencies.BullishConfig.NUMBER_OF_EMA26_VALUES_TO_CHECK;
import static com.kishlaly.ta.model.indicators.Indicator.EMA26;
import static com.kishlaly.ta.utils.Dates.beautifyQuoteDate;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarCount;

/**
 * Для бычьей дивергенции цена акций должна быть выше $3, для медвежьей - выше $10. Объем торгов по
 * акции должен быть выше 500к в сутки (низкий объем торгов означает слабый интерес и частые
 * неожиданные скачки цен)
 * <p>
 * Индикаторы:
 * MACD (12 26 9 close)
 * EMA26 для бОльшего таймфрейма для фильтрации нисходящих трендов
 *
 * @author Vladimir Kishlaly
 * @since 18.11.2021
 */
public class Divergencies {

    public static class BullishConfig {
        public static boolean ALLOW_ON_BEARISH_TREND;
        public static boolean ALLOW_MULTIPLE_ISLANDS = true;
        public static int MAX_TAIL_SIZE = 7;
        public static int SECOND_BOTTOM_RATIO = 80;
        public static int NUMBER_OF_EMA26_VALUES_TO_CHECK = 4;
    }

    /**
     * <p>
     * Поиск бычьих расхождений происходит в такой последовательности:
     * <p>
     * 1. Гистограмма MACD опускается до самого низкого минимума на отрезке из 100 столбиков
     * (можете задать свое значение). Таким образом определяется дно потенциального бычьего
     * расхождения A-B-C.
     * <p>
     * 2. Гистограмма MACD пересекает нулевую линию снизу вверх, «ломая хребет медведю». Так
     * определяется вершина потенциального бычьего расхождения.
     * <p>
     * 3. Когда акция достигает нового 100-дневного минимума, гистограмма MACD повторно пересекает
     * нулевую линию, но уже сверху вниз. В этой точке сканер и помечает акцию.
     * <p>
     * Пример: https://drive.google.com/file/d/1pd7Y92O3sMRRKHsTbsFoR6uYlhW33CyP/view?usp=sharing
     */
    public static TaskResult isBullish(SymbolData screen1, SymbolData screen2) {
        Quote result = null;
        int screenOneMinBarCount = resolveMinBarCount(screen1.timeframe);
        int screenTwoMinBarCount = resolveMinBarCount(screen2.timeframe);
        if (screen1.indicators.get(Indicator.EMA26) == null || screen1.indicators.get(Indicator.EMA26).isEmpty() || screen1.quotes.size() < screenOneMinBarCount) {
            Log.recordCode(TaskResultCode.NO_DATA_INDICATORS, screen1);
            Log.addDebugLine("Недостаточно данных индикатора EMA26 для " + screen2.timeframe);
            return new TaskResult(null, NO_DATA_QUOTES);
        }
        if (screen2.quotes.isEmpty() || screen2.quotes.size() < screenOneMinBarCount) {
            Log.recordCode(TaskResultCode.NO_DATA_QUOTES, screen1);
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen2.timeframe);
            return new TaskResult(null, NO_DATA_QUOTES);
        }

        List<Quote> screen1Quotes = screen1.quotes.subList(screen1.quotes.size() - screenOneMinBarCount, screen1.quotes.size());
        List<Quote> screen2Quotes = screen2.quotes.subList(screen2.quotes.size() - screenTwoMinBarCount, screen2.quotes.size());

        List<EMA> ema26All = screen1.indicators.get(EMA26);
        List<EMA> ema26 = ema26All.subList(ema26All.size() - screenOneMinBarCount, ema26All.size());

        List<MACD> macdAll = screen1.indicators.get(Indicator.MACD);
        List<MACD> macd = macdAll.subList(macdAll.size() - screenOneMinBarCount, macdAll.size());

        Quote lastChartQuote = screen2Quotes.get(screen2Quotes.size() - 1);

        if (!ALLOW_ON_BEARISH_TREND) {
            // фильтрация нисходящих трендов
            boolean uptrendCheckOnMultipleBars = TrendFunctions.uptrendCheckOnMultipleBars(screen1, screenOneMinBarCount, NUMBER_OF_EMA26_VALUES_TO_CHECK);
            //boolean uptrendCheckOnLastBar = TrendFunctions.uptrendCheckOnLastBar(screen1); плохая проверка
            if (!uptrendCheckOnMultipleBars) {
                Log.recordCode(NO_UPTREND, screen1);
                Log.addDebugLine("Не обнаружен восходящий тренд на долгосрочном экране");
                return new TaskResult(lastChartQuote, NO_UPTREND);
            }
        }

        List<MACD> screenTwomacdValuesAll = screen2.indicators.get(Indicator.MACD);
        if (screenTwomacdValuesAll.isEmpty()) {
            Log.addDebugLine("Недостаточно данных по MACD");
            return new TaskResult(lastChartQuote, NO_DATA_MACD);
        }
        List<MACD> screenTwoMacdValues = screenTwomacdValuesAll.subList(screenTwomacdValuesAll.size() - screenTwoMinBarCount, screenTwomacdValuesAll.size());

        // последние значения гистограммы должны быть <= 0

        double latestHistogramValue = screenTwoMacdValues.get(screenTwoMinBarCount - 1).getHistogram();

        if (latestHistogramValue > 0) {
            Log.recordCode(TaskResultCode.LAST_HISTOGRAM_ABOVE_ZERO, screen1);
            Log.addDebugLine("гистограмма у правого края выше нуля");
            return new TaskResult(lastChartQuote, LAST_HISTOGRAM_ABOVE_ZERO);
        }

        // строим массив из котировок с их гистрограммами

        List<HistogramQuote> histogramQuotes = new ArrayList<>();
        for (int i = 0; i < screenTwoMinBarCount; i++) {
            double histogram = screenTwoMacdValues.get(i).getHistogram();
            histogramQuotes.add(new HistogramQuote(histogram, screen2Quotes.get(i)));
        }

        // минимальный столбик гистограммы за весь период и соответствующая котировка

        HistogramQuote quoteWithLowestHistogram = histogramQuotes.stream()
                .min(Comparator.comparingDouble(HistogramQuote::getHistogramValue)).get();

        // индекс этого столбика в массиве histogramQuotes

        int indexOfMinHistogram = -1;
        for (int i = 0; i < histogramQuotes.size(); i++) {
            if (histogramQuotes.get(i).histogramValue
                    == quoteWithLowestHistogram.histogramValue) {
                indexOfMinHistogram = i;
                break;
            }
        }
        // не критично, но лучше, чтобы цена на уровне минимальной гистограммы была тоже минимальной от начала периода до этой точки
        double minimalPriceForRangeUpToLowestHistogram;

        // вдруг первый столбик содержит минимальную гистограмму
        if (indexOfMinHistogram == 0) {
            minimalPriceForRangeUpToLowestHistogram = histogramQuotes.get(0).quote.getClose();
        } else {
            minimalPriceForRangeUpToLowestHistogram = histogramQuotes.subList(
                            0, indexOfMinHistogram).stream()
                    .min(Comparator.comparingDouble(v -> v.getQuote().getClose())).get().getQuote().getClose();
        }

        HistogramQuote histogramQuoteWithMinimanPriceForTheWholeRange = histogramQuotes.stream()
                .filter(q -> q.getQuote().getClose() == minimalPriceForRangeUpToLowestHistogram)
                .findFirst().get();

        if (quoteWithLowestHistogram.getQuote().getClose()
                > minimalPriceForRangeUpToLowestHistogram) {
            Log.addDebugLine("Внимание: цена на дне гистограммы А (" + quoteWithLowestHistogram.histogramValue + " " + beautifyQuoteDate(quoteWithLowestHistogram.getQuote()) + ") не самая низкая в диапазоне");
        }

        // поиск максимума, который следует за прошлым минимумом ("перелом медвежьего хребта")
        // гистограмма должна вынырнуть выше нуля

        int indexOfMaxHistogramBarAfterLowestLow = 0;

        List<HistogramQuote> histogramQuotesAfterLowestLow = histogramQuotes.subList(
                indexOfMinHistogram, histogramQuotes.size());

        HistogramQuote quoteWithHighestHistogramAfterLowestLow = histogramQuotesAfterLowestLow.stream()
                .max(Comparator.comparingDouble(HistogramQuote::getHistogramValue)).get();

        if (quoteWithHighestHistogramAfterLowestLow.histogramValue <= 0) {
            Log.recordCode(TaskResultCode.BEARISH_BACKBONE_NOT_CRACKED, screen1);
            Log.addDebugLine("не произошло перелома медвежьего хребта");
            return new TaskResult(lastChartQuote, BEARISH_BACKBONE_NOT_CRACKED);
        }

        // какой индекс у максимума

        for (int i = indexOfMinHistogram; i < histogramQuotes.size(); i++) {
            if (histogramQuotes.get(i).histogramValue
                    == quoteWithHighestHistogramAfterLowestLow.histogramValue) {
                indexOfMaxHistogramBarAfterLowestLow = i;
                break;
            }
        }

        // теперь нужно найти пересечения гистограммой нулевой линии сверху вниз

        List<HistogramQuote> histogramQuotesFromMaxBar = histogramQuotes.subList(
                indexOfMaxHistogramBarAfterLowestLow, histogramQuotes.size());
        boolean crossedZero = false;
        HistogramQuote quoteWhenHistogramCrossedZeroFromTop = null;
        for (int i = indexOfMaxHistogramBarAfterLowestLow; i < histogramQuotes.size(); i++) {
            if (histogramQuotes.get(i).histogramValue < 0) {
                crossedZero = true;
                quoteWhenHistogramCrossedZeroFromTop = histogramQuotes.get(i);
                break;
            }
        }

        // сверяем значения цены в минимуме гистограммы и максимуме

        if (crossedZero) {

            double priceInLowestHistogramBar = quoteWithLowestHistogram.quote.getClose();
            double priceWhenHitogramCrossedZeroFromTop = quoteWhenHistogramCrossedZeroFromTop.quote.getClose();

            // между точкой пересечения гистограммой нуля из вершины В и концом графика не должно быть положительных столбиков гистограммы
            // потому что это уже может быть старая дивергенция
            // это не фильтрует https://drive.google.com/file/d/1FYm6rib--9VmlNucXCmzdJnUIzrYjzPc/view?usp=sharing но это можно проверить вручную

            int indexOfQuoteWhenHistogramCrossedZeroFromTop = 0;
            for (int i = 0; i < histogramQuotes.size(); i++) {
                if (histogramQuotes.get(i).getQuote()
                        .equals(quoteWhenHistogramCrossedZeroFromTop.getQuote())) {
                    indexOfQuoteWhenHistogramCrossedZeroFromTop = i;
                    break;
                }
            }

            // попытка избавитсья от ситуации https://drive.google.com/file/d/1OT1LBAdH1cZiGYJw0PDrwQ3NTpslqygY/view?usp=sharing

            boolean foundFirstPositive = false;
            boolean foundSecondPositive = false;
            boolean foundSecondNegativeAfterLowestLow = false;
            int indexOfSecondPositive = -1;
            for (int i = indexOfMinHistogram; i < histogramQuotes.size(); i++) {
                HistogramQuote histogramQuote = histogramQuotes.get(i);
                double histogramValue = histogramQuote.histogramValue;
                if (histogramValue > 0) {
                    if (!foundSecondPositive) {
                        foundFirstPositive = true;
                    }
                    if (foundSecondNegativeAfterLowestLow) {
                        foundSecondPositive = true;
                        indexOfSecondPositive = i;
                        break;
                    }
                }
                if (histogramValue < 0) {
                    if (foundFirstPositive) {
                        foundSecondNegativeAfterLowestLow = true;
                    }
                }
            }
            if (foundSecondPositive) {
                Log.recordCode(TaskResultCode.HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS, screen1);
                Log.addDebugLine("В точке " + beautifyQuoteDate(histogramQuotes.get(indexOfSecondPositive).quote) + " обнаружилась второая положительная область");
                return new TaskResult(lastChartQuote, HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS);
            }

            // это может быть началом тройной дивергенции, если цена продолжает падать
            // пример: https://drive.google.com/file/d/1hm-LUXXtpghwNMnfbz93diLQOIyjy9dV/view?usp=sharing

            boolean histogramWentAboveZeroAgainAfterHistogramCrossedZeroFromTop = false;
            for (int i = indexOfQuoteWhenHistogramCrossedZeroFromTop; i < histogramQuotes.size();
                 i++) {
                if (histogramQuotes.get(i).histogramValue > 0) {
                    histogramWentAboveZeroAgainAfterHistogramCrossedZeroFromTop = true;
                }
            }

            if (histogramWentAboveZeroAgainAfterHistogramCrossedZeroFromTop) {
                if (BullishConfig.ALLOW_MULTIPLE_ISLANDS) {
                    if (histogramQuotes.get(histogramQuotes.size() - 1).getQuote().getClose()
                            < priceInLowestHistogramBar) {
                        // ок, наблюдаем вручную
                        Log.addDebugLine(
                                "Внимание: после пересечениея гистограммы нуля из вершины В (" + beautifyQuoteDate(quoteWhenHistogramCrossedZeroFromTop.getQuote())
                                        + ") встретилась еще одна область положительных гистограмм");
                    } else {
                        Log.recordCode(HISTOGRAM_ISLANDS_HIGHER_PRICE, screen1);
                        Log.addDebugLine("После дна гистограммы А встретилось несколько положительных областей гистограмм, но у края цена выше, чем в А");
                        return new TaskResult(lastChartQuote, HISTOGRAM_ISLANDS_HIGHER_PRICE);
                    }
                } else {
                    Log.addDebugLine(
                            "Запрет: после пересечениея гистограммы нуля из вершины В (" + beautifyQuoteDate(quoteWhenHistogramCrossedZeroFromTop.getQuote())
                                    + ") встретилась еще одна область положительных гистограмм");
                }
            }

            // второе дно гистограммы не должно быть ниже половины первого дна

            double lowestHistogramAfterCrossedZeroFromTop = histogramQuotes.subList(indexOfQuoteWhenHistogramCrossedZeroFromTop, histogramQuotes.size()).stream()
                    .min(Comparator.comparingDouble(HistogramQuote::getHistogramValue)).get().histogramValue;
            if (Math.abs(lowestHistogramAfterCrossedZeroFromTop) >= Math.abs(quoteWithLowestHistogram.histogramValue) / 100 * 60) {
                Log.recordCode(HISTOGRAM_SECOND_BOTTOM_RATIO, screen1);
                Log.addDebugLine("Второе дно гистограммы больше " + BullishConfig.SECOND_BOTTOM_RATIO + "% глубины первого дна");
                return new TaskResult(lastChartQuote, HISTOGRAM_SECOND_BOTTOM_RATIO);
            }

            // последний столбик гистограммы должен быть меньше предыдущего
            HistogramQuote preLast = histogramQuotes.get(histogramQuotes.size() - 2);
            HistogramQuote last = histogramQuotes.get(histogramQuotes.size() - 1);
            if (Math.abs(last.histogramValue) >= Math.abs(preLast.histogramValue)) {
                Log.recordCode(HISTOGRAM_LAST_BAR_NOT_LOWER, screen1);
                Log.addDebugLine("Последний столбик гистограммы не ниже предыдущего");
                return new TaskResult(lastChartQuote, HISTOGRAM_LAST_BAR_NOT_LOWER);
            }

            // исплючаем длинные хвосты отрицательных гистограмм у правога края, которые часто бывают на нисходящем тренде на более крупном таймфрейме
            long tailCount = histogramQuotes.subList(indexOfQuoteWhenHistogramCrossedZeroFromTop, histogramQuotes.size()).stream().count();
            if (tailCount >= BullishConfig.MAX_TAIL_SIZE) {
                Log.recordCode(NEGATIVE_HISTOGRAMS_LIMIT, screen1);
                Log.addDebugLine("У правого края скопилось " + tailCount + " отрицательных гистограмм (лимит: " + BullishConfig.MAX_TAIL_SIZE + ")");
                return new TaskResult(lastChartQuote, NEGATIVE_HISTOGRAMS_LIMIT);
            }

            // цена должна образовать новую впадину

            if (priceWhenHitogramCrossedZeroFromTop
                    < priceInLowestHistogramBar) {
                result = last.getQuote();
            } else {
                Log.recordCode(DIVERGENCE_FAIL_AT_ZERO, screen1);
                Log.addDebugLine("Нету дивергенции: на спуске к нулю от В цена выше, чем в А");
            }
        } else {
            Log.recordCode(DIVERGENCE_FAIL_AT_TOP, screen1);
            Log.addDebugLine("гистограмма не опустилась от вершины B");
        }

        return new TaskResult(result, SIGNAL);
    }

}
