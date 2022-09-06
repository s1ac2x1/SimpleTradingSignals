package com.kishlaly.ta.analyze.functions;

import com.kishlaly.ta.model.QuoteJava;
import com.kishlaly.ta.model.SymbolDataJava;
import com.kishlaly.ta.model.indicators.EMAJava;
import com.kishlaly.ta.model.indicators.IndicatorJava;
import com.kishlaly.ta.model.indicators.MACDJava;
import com.kishlaly.ta.utils.Context;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TrendFunctions {

    // Main rules of verification:
    // 1. At least half of the last N bars must be the right color (green on an uptrend or red on a downtrend)
    //    Also, all N should not open and close above the EMA26 (the shadow may cross) TODO why so strict?
    // 2. The EMA should rise sequentially
    // 3. if the MACD histogram does not increase consistently - that's ok
    //    the main thing is that it does not descend sequentially when the EMA grows
    //
    // In the future, one might think about such cases:
    // 1) Long-term timeframe, EMA rises, histogram descends smoothly: https://drive.google.com/file/d/1l6aAV-qDseGkBqCQ4-cb3lB-hUy_R95G/view?usp=sharing
    // 2) medium-term timeframe, the oscillator gives a signal, the histogram does not contradict, the price bars too: https://drive.google.com/file/d/1tw_wUR9MXbT2zooD6dmH97bC9AwQCA3U/view?usp=sharing
    // (this case is probably already covered by the first point: check the color of half of the last N bars)
    public static boolean uptrendCheckOnMultipleBars(
            SymbolDataJava symbolData,
            int minBarsCount,
            int barsToCheck) {
        return abstractTrendCheckOnMultipleBars(
                symbolData,
                minBarsCount,
                barsToCheck,
                quote -> quote.getOpen() < quote.getClose(),
                (quote, ema) -> quote.getOpen() > ema.getValue() && quote.getClose() > ema.getValue(),
                (next, curr) -> next <= curr,
                (curr, next) -> curr < next,
                (curr, next) -> curr > next
        );
    }

    // mirror analog of uptrendCheckOnMultipleBars
    public static boolean downtrendCheckOnMultipleBars(SymbolDataJava symbolData, int minBarsCount, int barsToCheck) {
        return abstractTrendCheckOnMultipleBars(
                symbolData,
                minBarsCount,
                barsToCheck,
                quote -> quote.getOpen() > quote.getClose(),
                (quote, ema) -> quote.getOpen() < ema.getValue() && quote.getClose() < ema.getValue(),
                (next, curr) -> next >= curr,
                (curr, next) -> curr > next,
                (curr, next) -> curr < next
        );
    }

    // the second option: allow when the last bar on the long term timeframe opened below, but closed above the EMA
    // example of a weekly schedule: https://drive.google.com/file/d/14PlpZMZV7lwsIwP2V7bww0LKSVjdn70Q/view?usp=sharing
    // and the ideal entry point on the daily: https://drive.google.com/file/d/1-a0ZtMuLQyuamez_402v6YkViNWzY6RS/view?usp=sharing
    // I don't like this method because it doesn't take the histogram into account
    // On the weekly chart from the example, the price bar is on the sixth histogram smoothly decreasing, this is dangerous
    public static boolean uptrendCheckOnLastBar(SymbolDataJava symbolData) {
        QuoteJava lastQuote = symbolData.quotes.get(symbolData.quotes.size() - 1);
        List<EMAJava> ema = (List<EMAJava>) symbolData.indicators.get(IndicatorJava.EMA26);
        double lastEMA = ema.get(ema.size() - 1).getValue();
        return lastQuote.getOpen() < lastEMA && lastQuote.getClose() > lastEMA;
    }

    // mirror logic uptrendCheckOnLastBar
    public static boolean downtrendCheckOnLastBar(SymbolDataJava symbolData) {
        QuoteJava lastQuote = symbolData.quotes.get(symbolData.quotes.size() - 1);
        List<EMAJava> ema = (List<EMAJava>) symbolData.indicators.get(IndicatorJava.EMA26);
        double lastEMA = ema.get(ema.size() - 1).getValue();
        return lastQuote.getOpen() > lastEMA && lastQuote.getClose() < lastEMA;
    }

    private static boolean abstractTrendCheckOnMultipleBars(
            SymbolDataJava symbolData,
            int minBarsCount,
            int barsToCheck,
            Function<QuoteJava, Boolean> barCorrectColor,
            BiFunction<QuoteJava, EMAJava, Boolean> quoteEmaIntersectionCheck,
            BiFunction<Double, Double, Boolean> emaMoveCheck,
            BiFunction<Double, Double, Boolean> histogramCheck1,
            BiFunction<Double, Double, Boolean> histogramCheck2
    ) {
        List<QuoteJava> quotes = symbolData.quotes;
        quotes = quotes.subList(quotes.size() - minBarsCount, quotes.size());
        List<EMAJava> ema = (List<EMAJava>) symbolData.indicators.get(IndicatorJava.EMA26);
        ema = ema.subList(ema.size() - minBarsCount, ema.size());
        List<MACDJava> macd = (List<MACDJava>) symbolData.indicators.get(IndicatorJava.MACD);
        macd = macd.subList(macd.size() - minBarsCount, macd.size());

        for (int i = quotes.size() - barsToCheck; i < quotes.size(); i++) {
            if (!quoteEmaIntersectionCheck.apply(quotes.get(i), ema.get(i))) {
                return false;
            }
        }

        int barsWithCorrectColors = 0;
        for (int i = quotes.size() - barsToCheck; i < quotes.size(); i++) {
            if (barCorrectColor.apply(quotes.get(i))) {
                barsWithCorrectColors++;
            }
        }
        if (barsWithCorrectColors < barsToCheck / 2) {
            return false;
        }

        for (int i = ema.size() - barsToCheck; i < ema.size() - 1; i++) {
            Double curr = ema.get(i).getValue();
            Double next = ema.get(i + 1).getValue();
            if (emaMoveCheck.apply(next, curr)) {
                return false;
            }
        }

        if (Context.trendCheckIncludeHistogram) {
            int histogramMovement1 = 0;
            int histogramMovement2 = 0;
            boolean macdMovingConstantly = false;
            for (int i = macd.size() - barsToCheck; i < macd.size() - 1; i++) {
                Double curr = macd.get(i).getHistogram();
                Double next = macd.get(i + 1).getHistogram();
                if (histogramCheck1.apply(curr, next)) {
                    histogramMovement1++;
                }
                if (histogramCheck2.apply(curr, next)) {
                    histogramMovement2++;
                }
            }

            macdMovingConstantly = histogramMovement1 == (barsToCheck - 1);
            if (!macdMovingConstantly) {
                if (histogramMovement2 == (barsToCheck - 1)) {
                    return false;
                }
            }
        }

        return true;
    }

}
