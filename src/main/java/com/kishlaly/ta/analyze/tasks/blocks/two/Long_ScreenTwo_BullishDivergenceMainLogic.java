package com.kishlaly.ta.analyze.tasks.blocks.two;

import com.kishlaly.ta.model.BlockResultCodeJava;
import com.kishlaly.ta.analyze.tasks.Divergencies;
import com.kishlaly.ta.model.BlockResultJava;
import com.kishlaly.ta.model.HistogramQuoteJava;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kishlaly.ta.model.BlockResultCodeJava.*;
import static com.kishlaly.ta.utils.DatesJava.beautifyQuoteDate;

public class Long_ScreenTwo_BullishDivergenceMainLogic implements ScreenTwoBlock {
    @Override
    public BlockResultJava check(SymbolData screen) {
        int screenTwoMinBarCount = screen.quotes.size();
        List<MACDJava> screenTwoMacdValues = (List<MACDJava>) screen.indicators.get(IndicatorJava.MACD);

        // build an array of quotes with their histograms

        List<HistogramQuoteJava> histogramQuotes = new ArrayList<>();
        for (int i = 0; i < screenTwoMinBarCount; i++) {
            double histogram = screenTwoMacdValues.get(i).getHistogram();
            histogramQuotes.add(new HistogramQuoteJava(histogram, screen.quotes.get(i)));
        }

        // the minimum bar of the histogram for the whole period and the corresponding quotation

        HistogramQuoteJava quoteWithLowestHistogram = histogramQuotes.stream()
                .min(Comparator.comparingDouble(HistogramQuoteJava::getHistogramValue)).get();

        // the index of this column in the histogramQuotes array

        int indexOfMinHistogram = -1;
        for (int i = 0; i < histogramQuotes.size(); i++) {
            if (histogramQuotes.get(i).histogramValue
                    == quoteWithLowestHistogram.histogramValue) {
                indexOfMinHistogram = i;
                break;
            }
        }
        // not critical, but it is better that the price at the level of the minimum histogram was also the minimum from the beginning of the period to this point
        double minimalPriceForRangeUpToLowestHistogram;

        // if the first bar contains a minimum histogram
        if (indexOfMinHistogram == 0) {
            minimalPriceForRangeUpToLowestHistogram = histogramQuotes.get(0).quote.getClose();
        } else {
            minimalPriceForRangeUpToLowestHistogram = histogramQuotes.subList(
                            0, indexOfMinHistogram).stream()
                    .min(Comparator.comparingDouble(v -> v.getQuote().getClose())).get().getQuote().getClose();
        }

        HistogramQuoteJava histogramQuoteWithMinimanPriceForTheWholeRange = histogramQuotes.stream()
                .filter(q -> q.getQuote().getClose() == minimalPriceForRangeUpToLowestHistogram)
                .findFirst().get();

        if (quoteWithLowestHistogram.getQuote().getClose()
                > minimalPriceForRangeUpToLowestHistogram) {
            Log.addDebugLine("Внимание: цена на дне гистограммы А (" + quoteWithLowestHistogram.histogramValue + " " + beautifyQuoteDate(quoteWithLowestHistogram.getQuote()) + ") не самая низкая в диапазоне");
        }

        // Finding a high that follows a past low ("breaking a bearish backbone")
        // The histogram should pop out above zero

        int indexOfMaxHistogramBarAfterLowestLow = 0;

        List<HistogramQuoteJava> histogramQuotesAfterLowestLow = histogramQuotes.subList(
                indexOfMinHistogram, histogramQuotes.size());

        HistogramQuoteJava quoteWithHighestHistogramAfterLowestLow = histogramQuotesAfterLowestLow.stream()
                .max(Comparator.comparingDouble(HistogramQuoteJava::getHistogramValue)).get();

        if (quoteWithHighestHistogramAfterLowestLow.histogramValue <= 0) {
            Log.recordCode(BlockResultCodeJava.BEARISH_BACKBONE_NOT_CRACKED_SCREEN_2, screen);
            Log.addDebugLine("there was no fracture of the bear's backbone");
            return new BlockResultJava(screen.getLastQuote(), BEARISH_BACKBONE_NOT_CRACKED_SCREEN_2);
        }

        // what is the maximum index

        for (int i = indexOfMinHistogram; i < histogramQuotes.size(); i++) {
            if (histogramQuotes.get(i).histogramValue
                    == quoteWithHighestHistogramAfterLowestLow.histogramValue) {
                indexOfMaxHistogramBarAfterLowestLow = i;
                break;
            }
        }

        // now we need to find the histogram zero line crossings from top to bottom

        List<HistogramQuoteJava> histogramQuotesFromMaxBar = histogramQuotes.subList(
                indexOfMaxHistogramBarAfterLowestLow, histogramQuotes.size());
        boolean crossedZero = false;
        HistogramQuoteJava quoteWhenHistogramCrossedZeroFromTop = null;
        for (int i = indexOfMaxHistogramBarAfterLowestLow; i < histogramQuotes.size(); i++) {
            if (histogramQuotes.get(i).histogramValue < 0) {
                crossedZero = true;
                quoteWhenHistogramCrossedZeroFromTop = histogramQuotes.get(i);
                break;
            }
        }

        // check the price values at the histogram minimum and maximum

        if (crossedZero) {

            double priceInLowestHistogramBar = quoteWithLowestHistogram.quote.getClose();
            double priceWhenHitogramCrossedZeroFromTop = quoteWhenHistogramCrossedZeroFromTop.quote.getClose();

            // between the point of intersection of the histogram zero from vertex B and the end of the chart there should be no positive bars of the histogram
            // because it may already be an old divergence
            // howerver, it doesn't filter cases like https://drive.google.com/file/d/1FYm6rib--9VmlNucXCmzdJnUIzrYjzPc/view?usp=sharing

            int indexOfQuoteWhenHistogramCrossedZeroFromTop = 0;
            for (int i = 0; i < histogramQuotes.size(); i++) {
                if (histogramQuotes.get(i).getQuote()
                        .equals(quoteWhenHistogramCrossedZeroFromTop.getQuote())) {
                    indexOfQuoteWhenHistogramCrossedZeroFromTop = i;
                    break;
                }
            }

            // Trying to get rid of the situation https://drive.google.com/file/d/1OT1LBAdH1cZiGYJw0PDrwQ3NTpslqygY/view?usp=sharing

            boolean foundFirstPositive = false;
            boolean foundSecondPositive = false;
            boolean foundSecondNegativeAfterLowestLow = false;
            int indexOfSecondPositive = -1;
            for (int i = indexOfMinHistogram; i < histogramQuotes.size(); i++) {
                HistogramQuoteJava histogramQuote = histogramQuotes.get(i);
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
                Log.recordCode(BlockResultCodeJava.HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS, screen);
                Log.addDebugLine("In the point " + beautifyQuoteDate(histogramQuotes.get(indexOfSecondPositive).quote) + " a second positive area was found");
                return new BlockResultJava(screen.getLastQuote(), HISTOGRAM_MULTIPLE_POSITIVE_ISLANDS);
            }

            // this could be the beginning of a triple divergence if the price keeps falling
            // example: https://drive.google.com/file/d/1hm-LUXXtpghwNMnfbz93diLQOIyjy9dV/view?usp=sharing

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
                        // OK, we observe manually
                        Log.addDebugLine(
                                "Note: after crossing the histogram zero from vertex B (" + beautifyQuoteDate(quoteWhenHistogramCrossedZeroFromTop.getQuote())
                                        + ") another area of positive histograms was encountered");
                    } else {
                        Log.recordCode(HISTOGRAM_ISLANDS_HIGHER_PRICE, screen);
                        Log.addDebugLine("After the bottom of the histogram A there are several positive areas of the histograms, but at the edge the price is higher than in A");
                        return new BlockResultJava(screen.getLastQuote(), HISTOGRAM_ISLANDS_HIGHER_PRICE);
                    }
                } else {
                    Log.addDebugLine(
                            "Forbidden: after the histogram crosses zero from vertex B (" + beautifyQuoteDate(quoteWhenHistogramCrossedZeroFromTop.getQuote())
                                    + ") another area of positive histograms was encountered");
                }
            }

            // the second bottom of the histogram should not be lower than half of the first bottom

            double lowestHistogramAfterCrossedZeroFromTop = histogramQuotes.subList(indexOfQuoteWhenHistogramCrossedZeroFromTop, histogramQuotes.size()).stream()
                    .min(Comparator.comparingDouble(HistogramQuoteJava::getHistogramValue)).get().histogramValue;
            if (Math.abs(lowestHistogramAfterCrossedZeroFromTop) >= Math.abs(quoteWithLowestHistogram.histogramValue) / 100 * 60) {
                Log.recordCode(HISTOGRAM_SECOND_BOTTOM_RATIO, screen);
                Log.addDebugLine("The second bottom of the histogram is larger than " + Divergencies.BullishConfig.SECOND_BOTTOM_RATIO + "% of the first bottom depth");
                return new BlockResultJava(screen.getLastQuote(), HISTOGRAM_SECOND_BOTTOM_RATIO);
            }

            // the last bar of the histogram should be smaller than the previous one
            HistogramQuoteJava preLast = histogramQuotes.get(histogramQuotes.size() - 2);
            HistogramQuoteJava last = histogramQuotes.get(histogramQuotes.size() - 1);
            if (Math.abs(last.histogramValue) >= Math.abs(preLast.histogramValue)) {
                Log.recordCode(HISTOGRAM_LAST_BAR_NOT_LOWER, screen);
                Log.addDebugLine("The last bar of the histogram is not lower than the previous one");
                return new BlockResultJava(screen.getLastQuote(), HISTOGRAM_LAST_BAR_NOT_LOWER);
            }

            // exclude long tails of negative histograms at the right edge, which often occur in a downtrend on a larger timeframe
            long tailCount = histogramQuotes.subList(indexOfQuoteWhenHistogramCrossedZeroFromTop, histogramQuotes.size()).stream().count();
            if (tailCount >= Divergencies.BullishConfig.MAX_TAIL_SIZE) {
                Log.recordCode(NEGATIVE_HISTOGRAMS_LIMIT, screen);
                Log.addDebugLine("At the right edge has piled " + tailCount + " negative histograms (limit: " + Divergencies.BullishConfig.MAX_TAIL_SIZE + ")");
                return new BlockResultJava(screen.getLastQuote(), NEGATIVE_HISTOGRAMS_LIMIT);
            }

            // the price should form a new valley

            if (priceWhenHitogramCrossedZeroFromTop
                    < priceInLowestHistogramBar) {
            } else {
                Log.recordCode(DIVERGENCE_FAIL_AT_ZERO, screen);
                Log.addDebugLine("No divergence: on the slope to zero from B the price is higher than in A");
                return new BlockResultJava(screen.getLastQuote(), DIVERGENCE_FAIL_AT_ZERO);
            }
        } else {
            Log.recordCode(DIVERGENCE_FAIL_AT_TOP, screen);
            Log.addDebugLine("the histogram did not descend from the top of B");
            return new BlockResultJava(screen.getLastQuote(), DIVERGENCE_FAIL_AT_TOP);
        }

        return new BlockResultJava(screen.getLastQuote(), OK);
    }
}
