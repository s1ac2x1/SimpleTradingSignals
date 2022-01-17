package com.kishlaly.ta.analyze.functions;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TrendFunctions {

    // Главные правила проверки:
    // 1. Как минимум пловина из N последних баров должна быть правильного цвета (зеленые на восходящем тренде или красные на нисходящем)
    //    так же все N не должны открываться и закрываться выше EMA26 (тень может пересекать)
    // 2. ЕМА должна расти последовательно
    // 3. если гистограмма MACD не растет последовательно - нормально
    //    главное, чтобы она не спускалась последовательно при росте ЕМА
    //
    // В будущем, можно подумать над такими случаями:
    // 1) долгосрочный таймфрейм, ЕМА растет, гистограммы плавно спускается: https://drive.google.com/file/d/1l6aAV-qDseGkBqCQ4-cb3lB-hUy_R95G/view?usp=sharing
    // 2) среднесрочный таймфрейм, осциллятор подает сигнал, гистограмма не противоречит, ценовые столбики тоже: https://drive.google.com/file/d/1tw_wUR9MXbT2zooD6dmH97bC9AwQCA3U/view?usp=sharing
    // (вероятно, этот случай уже покрывается первым пунктом: проверка цвета половины из N последних баров)
    public static boolean uptrendCheckOnMultipleBars(
            SymbolData symbolData,
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

    // зеркальный аналог uptrendCheckOnMultipleBars
    public static boolean downtrendCheckOnMultipleBars(SymbolData symbolData, int minBarsCount, int barsToCheck) {
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

    // второй вариант: допускать, когда последний столбик на долгосрочном таймфрейме открылся ниже, но закрылся выше ЕМА
    // пример недельного графика: https://drive.google.com/file/d/14PlpZMZV7lwsIwP2V7bww0LKSVjdn70Q/view?usp=sharing
    // и идеальная точка входа на дневном: https://drive.google.com/file/d/1-a0ZtMuLQyuamez_402v6YkViNWzY6RS/view?usp=sharing
    // мне не нравится этот способ, потому что не берется в рассчет гистограмма
    // на недельном графике из примера ценовой столбик находится на шестой по счету плавно снижающейся гистограмме, это опасно
    public static boolean uptrendCheckOnLastBar(SymbolData symbolData) {
        Quote lastQuote = symbolData.quotes.get(symbolData.quotes.size() - 1);
        List<EMA> ema = symbolData.indicators.get(Indicator.EMA26);
        double lastEMA = ema.get(ema.size() - 1).getValue();
        return lastQuote.getOpen() < lastEMA && lastQuote.getClose() > lastEMA;
    }

    // зеркальная логика uptrendCheckOnLastBar
    public static boolean downtrendCheckOnLastBar(SymbolData symbolData) {
        Quote lastQuote = symbolData.quotes.get(symbolData.quotes.size() - 1);
        List<EMA> ema = symbolData.indicators.get(Indicator.EMA26);
        double lastEMA = ema.get(ema.size() - 1).getValue();
        return lastQuote.getOpen() > lastEMA && lastQuote.getClose() < lastEMA;
    }

    private static boolean abstractTrendCheckOnMultipleBars(
            SymbolData symbolData,
            int minBarsCount,
            int barsToCheck,
            Function<Quote, Boolean> barCorrectColor,
            BiFunction<Quote, EMA, Boolean> quoteEmaIntersectionCheck,
            BiFunction<Double, Double, Boolean> emaMoveCheck,
            BiFunction<Double, Double, Boolean> histogramCheck1,
            BiFunction<Double, Double, Boolean> histogramCheck2
    ) {
        List<Quote> quotes = symbolData.quotes;
        quotes = quotes.subList(quotes.size() - minBarsCount, quotes.size());
        List<EMA> ema = symbolData.indicators.get(Indicator.EMA26);
        ema = ema.subList(ema.size() - minBarsCount, ema.size());
        List<MACD> macd = symbolData.indicators.get(Indicator.MACD);
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

        return true;
    }

}
