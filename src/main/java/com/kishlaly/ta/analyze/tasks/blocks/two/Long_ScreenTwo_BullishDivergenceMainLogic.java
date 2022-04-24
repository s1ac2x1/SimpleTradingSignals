package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.analyze.tasks.Divergencies;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.HistogramQuote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.utils.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.utils.Dates.beautifyQuoteDate;

public class Long_ScreenTwo_BullishDivergenceMainLogic implements ScreenTwoBlock {
    @Override
    public BlockResult check(SymbolData screen) {
        int screenTwoMinBarCount = screen.quotes.size();
        List<MACD> screenTwoMacdValues = (List<MACD>) screen.indicators.get(Indicator.MACD);

        // строим массив из котировок с их гистрограммами

        List<HistogramQuote> histogramQuotes = new ArrayList<>();
        for (int i = 0; i < screenTwoMinBarCount; i++) {
            double histogram = screenTwoMacdValues.get(i).getHistogram();
            histogramQuotes.add(new HistogramQuote(histogram, screen.quotes.get(i)));
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
            Log.recordCode(BlockResultCode.BEARISH_BACKBONE_NOT_CRACKED_SCREEN_2, screen);
            Log.addDebugLine("не произошло перелома медвежьего хребта");
            return new BlockResult(screen.getLastQuote(), BEARISH_BACKBONE_NOT_CRACKED_SCREEN_2);
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
                Log.recordCode(BlockResultCode.HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS, screen);
                Log.addDebugLine("В точке " + beautifyQuoteDate(histogramQuotes.get(indexOfSecondPositive).quote) + " обнаружилась второая положительная область");
                return new BlockResult(screen.getLastQuote(), HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS);
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
                if (Divergencies.BullishConfig.ALLOW_MULTIPLE_ISLANDS) {
                    if (histogramQuotes.get(histogramQuotes.size() - 1).getQuote().getClose()
                            < priceInLowestHistogramBar) {
                        // ок, наблюдаем вручную
                        Log.addDebugLine(
                                "Внимание: после пересечениея гистограммы нуля из вершины В (" + beautifyQuoteDate(quoteWhenHistogramCrossedZeroFromTop.getQuote())
                                        + ") встретилась еще одна область положительных гистограмм");
                    } else {
                        Log.recordCode(HISTOGRAM_ISLANDS_HIGHER_PRICE, screen);
                        Log.addDebugLine("После дна гистограммы А встретилось несколько положительных областей гистограмм, но у края цена выше, чем в А");
                        return new BlockResult(screen.getLastQuote(), HISTOGRAM_ISLANDS_HIGHER_PRICE);
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
                Log.recordCode(HISTOGRAM_SECOND_BOTTOM_RATIO, screen);
                Log.addDebugLine("Второе дно гистограммы больше " + Divergencies.BullishConfig.SECOND_BOTTOM_RATIO + "% глубины первого дна");
                return new BlockResult(screen.getLastQuote(), HISTOGRAM_SECOND_BOTTOM_RATIO);
            }

            // последний столбик гистограммы должен быть меньше предыдущего
            HistogramQuote preLast = histogramQuotes.get(histogramQuotes.size() - 2);
            HistogramQuote last = histogramQuotes.get(histogramQuotes.size() - 1);
            if (Math.abs(last.histogramValue) >= Math.abs(preLast.histogramValue)) {
                Log.recordCode(HISTOGRAM_LAST_BAR_NOT_LOWER, screen);
                Log.addDebugLine("Последний столбик гистограммы не ниже предыдущего");
                return new BlockResult(screen.getLastQuote(), HISTOGRAM_LAST_BAR_NOT_LOWER);
            }

            // исплючаем длинные хвосты отрицательных гистограмм у правога края, которые часто бывают на нисходящем тренде на более крупном таймфрейме
            long tailCount = histogramQuotes.subList(indexOfQuoteWhenHistogramCrossedZeroFromTop, histogramQuotes.size()).stream().count();
            if (tailCount >= Divergencies.BullishConfig.MAX_TAIL_SIZE) {
                Log.recordCode(NEGATIVE_HISTOGRAMS_LIMIT, screen);
                Log.addDebugLine("У правого края скопилось " + tailCount + " отрицательных гистограмм (лимит: " + Divergencies.BullishConfig.MAX_TAIL_SIZE + ")");
                return new BlockResult(screen.getLastQuote(), NEGATIVE_HISTOGRAMS_LIMIT);
            }

            // цена должна образовать новую впадину

            if (priceWhenHitogramCrossedZeroFromTop
                    < priceInLowestHistogramBar) {
            } else {
                Log.recordCode(DIVERGENCE_FAIL_AT_ZERO, screen);
                Log.addDebugLine("Нету дивергенции: на спуске к нулю от В цена выше, чем в А");
                return new BlockResult(screen.getLastQuote(), DIVERGENCE_FAIL_AT_ZERO);
            }
        } else {
            Log.recordCode(DIVERGENCE_FAIL_AT_TOP, screen);
            Log.addDebugLine("гистограмма не опустилась от вершины B");
            return new BlockResult(screen.getLastQuote(), DIVERGENCE_FAIL_AT_TOP);
        }

        return new BlockResult(screen.getLastQuote(), OK);
    }
}
