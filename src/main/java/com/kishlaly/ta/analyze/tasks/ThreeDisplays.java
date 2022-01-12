package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.Codes;
import com.kishlaly.ta.analyze.functions.TrendFunctions;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.EMA;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.model.indicators.Stoch;
import com.kishlaly.ta.utils.Context;
import com.kishlaly.ta.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.*;
import static com.kishlaly.ta.model.indicators.Indicator.*;
import static com.kishlaly.ta.utils.Context.minimumBarsCount;

/**
 * Индикаторы:
 * EMA26 (close) на первом экране
 * MACD (12 26 9 close) на втором экране
 * EMA13 (close) на втором экране
 * STOCH (14 1 3 close) на втором экране
 */
public class ThreeDisplays {

    public static class Config {
        public static int NUMBER_OF_EMA26_VALUES_TO_CHECK = 4; // 3 дает меньше сигналов, но они надежнее
        public static int multiplier = 2; // для поиска аномально длинных баров
        public static int STOCH_OVERSOLD = 35;
        public static int STOCH_OVERBOUGHT = 70;
    }

    public static Quote buySignal(SymbolData screen1, SymbolData screen2) {

        if (screen1.quotes.isEmpty() || screen1.quotes.size() < minimumBarsCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen1.timeframe.name());
            Log.recordCode(Codes.NO_DATA_QUOTES, screen1);
            return null;
        }
        if (screen2.quotes.isEmpty() || screen2.quotes.size() < minimumBarsCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen2.timeframe.name());
            Log.recordCode(Codes.NO_DATA_QUOTES, screen2);
            return null;
        }

        List<Indicator> missingData = new ArrayList<>();
        screen1.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < minimumBarsCount) {
                missingData.add(indicator);
            }
        });
        screen2.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < minimumBarsCount) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(Codes.NO_DATA_INDICATORS, screen1);
            Log.recordCode(Codes.NO_DATA_INDICATORS, screen2);
            Log.addDebugLine("Нету данных по индикаторам: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return null;
        }

        List<Quote> screen1Quotes = screen1.quotes.subList(screen1.quotes.size() - minimumBarsCount, screen1.quotes.size());
        List<Quote> screen2Quotes = screen2.quotes.subList(screen2.quotes.size() - minimumBarsCount, screen2.quotes.size());

        List<EMA> screenOneEMA26 = screen1.indicators.get(EMA26);
        screenOneEMA26 = screenOneEMA26.subList(screenOneEMA26.size() - minimumBarsCount, screenOneEMA26.size());

        List<MACD> screenOneMACD = screen1.indicators.get(MACD);
        screenOneMACD = screenOneMACD.subList(screenOneMACD.size() - minimumBarsCount, screenOneMACD.size());

        List<EMA> screenTwoEMA13 = screen2.indicators.get(Indicator.EMA13);
        screenTwoEMA13 = screenTwoEMA13.subList(screenTwoEMA13.size() - minimumBarsCount, screenTwoEMA13.size());

        List<MACD> screenTwoMACD = screen2.indicators.get(Indicator.MACD);
        screenTwoMACD = screenTwoMACD.subList(screenTwoMACD.size() - minimumBarsCount, screenTwoMACD.size());

        List<Stoch> screenTwoStochastic = screen2.indicators.get(Indicator.STOCH);
        screenTwoStochastic = screenTwoStochastic.subList(screenTwoStochastic.size() - minimumBarsCount, screenTwoStochastic.size());

        if (Context.screenTwoDay != null && !screen2.quotes.get(screen2.quotes.size() - 1).getNativeDate().contains(Context.screenTwoDay)) {
            return null;
        }

        // первый экран

        // проверка тренда
        boolean uptrendCheckOnMultipleBars = TrendFunctions.uptrendCheckOnMultipleBars(screen1, NUMBER_OF_EMA26_VALUES_TO_CHECK);
        //boolean uptrendCheckOnLastBar = TrendFunctions.uptrendCheckOnLastBar(screen1); плохая проверка
        if (!uptrendCheckOnMultipleBars) {
            Log.recordCode(Codes.NO_UPTREND, screen1);
            Log.addDebugLine("Не обнаружен восходящий тренд на долгосрочном экране");
            return null;
        }

        // На первом экране последние 4 Quote.low не должны понижаться
        // (эта проверка уже есть в Functions.isUptrend, но пусть тут тоже будет)
        Quote q4 = screen1Quotes.get(minimumBarsCount - 4);
        Quote q3 = screen1Quotes.get(minimumBarsCount - 3);
        Quote q2 = screen1Quotes.get(minimumBarsCount - 2);
        Quote q1 = screen1Quotes.get(minimumBarsCount - 1);
        if (q4.getLow() >= q3.getLow() && q3.getLow() >= q2.getLow() && q2.getLow() >= q1.getLow()) {
            // допустимо только, если последний столбик зеленый
            if (q1.getClose() < q1.getOpen()) {
                Log.recordCode(Codes.UPTREND_FAILING, screen1);
                Log.addDebugLine("Последние 4 столбика на первом экране понижаются");
                return null;
            } else {
                Log.addDebugLine("Последние 4 столбика на первом экране понижаются, но крайний правый закрылся выше открытия");
            }
        }

        // второй экран

        // гистограмма должна быть ниже нуля и начать повышаться: проверить на трех последних значениях

        Double macd3 = screenTwoMACD.get(screenTwoMACD.size() - 3).getHistogram(); // 3 от правого края
        Double macd2 = screenTwoMACD.get(screenTwoMACD.size() - 2).getHistogram(); // 2 от правого края
        Double macd1 = screenTwoMACD.get(screenTwoMACD.size() - 1).getHistogram(); // последняя

        boolean histogramBelowZero = macd3 < 0 && macd2 < 0 && macd1 < 0;
        if (!histogramBelowZero) {
            Log.recordCode(Codes.HISTOGRAM_NOT_BELOW_ZERO, screen2);
            Log.addDebugLine("Гистограмма на втором экране не ниже нуля");
            return null;
        }

        boolean ascendingHistogram = macd3 < macd2 && macd2 < macd1;
        if (!ascendingHistogram) {
            Log.recordCode(Codes.HISTOGRAM_NOT_ASCENDING, screen2);
            Log.addDebugLine("Гистограмма на втором экране не повышается");
            return null;
        }

        // стохастик должен подниматься из зоны перепроданности: проверить на трех последних значениях

        Stoch stoch3 = screenTwoStochastic.get(screenTwoStochastic.size() - 3);
        Stoch stoch2 = screenTwoStochastic.get(screenTwoStochastic.size() - 2);
        Stoch stoch1 = screenTwoStochastic.get(screenTwoStochastic.size() - 1);

        // %D повышается (достаточно, чтобы последний был больше прошлых двух)
        boolean ascendingStochastic = stoch1.getSlowD() > stoch2.getSlowD() && stoch1.getSlowD() > stoch3.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(Codes.STOCH_NOT_ASCENDING, screen1);
            Log.addDebugLine("Стохастик %D не растет на втором экране");
            return null;
        }

        // проверка перепроданности

        // третья или вторая с конца %K ниже STOCH_OVERSOLD, и самая последняя выше первой
        boolean isOversoldK = (stoch3.getSlowK() <= STOCH_OVERSOLD || stoch2.getSlowK() <= STOCH_OVERSOLD)
                && (stoch1.getSlowK() > stoch3.getSlowK());
        // третья или вторая с конца %D ниже STOCH_OVERSOLD, и самая последняя выше обеих
        boolean isOversoldD = (stoch3.getSlowD() <= STOCH_OVERSOLD || stoch2.getSlowD() <= STOCH_OVERSOLD)
                && (stoch1.getSlowD() > stoch2.getSlowD())
                && (stoch1.getSlowD() > stoch3.getSlowD());

        if (!isOversoldK || !isOversoldD) {
            Log.recordCode(Codes.STOCH_NOT_ASCENDING_FROM_OVERSOLD, screen1);
            Log.addDebugLine("Стохастик не поднимается из перепроданности " + STOCH_OVERSOLD + ". %D: " + isOversoldD + "; %K: " + isOversoldK);
            return null;
        }

        // ценовые бары должны пересекать ЕМА13 и должны подниматься

        // обязательное условие 1
        // убедиться сначала, что high у последних трех столбиков повышается
        Quote quote3 = screen2Quotes.get(minimumBarsCount - 3);
        Quote quote2 = screen2Quotes.get(minimumBarsCount - 2);
        Quote quote1 = screen2Quotes.get(minimumBarsCount - 1);
        // наверно ascendingBarHigh=false + ascendingBarClose=false достаточно для отказа
        boolean ascendingBarHigh = quote3.getHigh() < quote2.getHigh() && quote2.getHigh() < quote1.getHigh();
        boolean ascendingBarClose = quote3.getClose() < quote2.getClose() && quote2.getClose() < quote1.getClose();

        int screenTwoEMA13Count = screenTwoEMA13.size();
        if (!ascendingBarHigh) {
            Log.recordCode(Codes.QUOTE_HIGH_NOT_GROWING, screen2);
            Log.addDebugLine("Quote.high не растет последовательно");
            if (!ascendingBarClose) {
                Log.recordCode(Codes.QUOTE_CLOSE_NOT_GROWING, screen2);
                Log.addDebugLine("Quote.close не растет последовательно");
                // третий с конца весь ниже ЕМА13, а второй и последний пересекли
                boolean thirdBarBelowEMA13 = quote3.getLow() < screenTwoEMA13.get(screenTwoEMA13Count - 3).getValue()
                        && quote3.getHigh() < screenTwoEMA13.get(screenTwoEMA13Count - 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= screenTwoEMA13.get(screenTwoEMA13Count - 2).getValue()
                        && quote2.getHigh() >= screenTwoEMA13.get(screenTwoEMA13Count - 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= screenTwoEMA13.get(screenTwoEMA13Count - 1).getValue()
                        && quote1.getHigh() >= screenTwoEMA13.get(screenTwoEMA13Count - 1).getValue();
                boolean crossingRule = thirdBarBelowEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    Log.addDebugLine("Третий с конца" + (thirdBarBelowEMA13 ? " " : " не ") + "ниже ЕМА13");
                    Log.addDebugLine("Предпоследний" + (secondBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.addDebugLine("Последний" + (lastBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.recordCode(Codes.CROSSING_RULE_VIOLATED, screen2);
                    return null;
                } else {
                    Log.recordCode(Codes.CROSSING_RULE_PASSED, screen2);
                    Log.addDebugLine("Правило пересечения выполняется");
                }
            } else {
                Log.recordCode(Codes.QUOTE_CLOSE_GROWING, screen2);
                Log.addDebugLine("Есть рост Quote.close");
            }
        } else {
            Log.recordCode(Codes.QUOTE_HIGH_GROWING, screen2);
            Log.addDebugLine("Есть рост Quote.high");
        }

        // нужно фильтровать ситуацию, когда третий и второй пересекают ЕМА13, а послдений целиком выше (момент входа в сделку упущен)
        // третий может открыться и закрыться выше, и это допустимо: https://drive.google.com/file/d/15XkXFKBQbTjeNjBn03NrF9JawCBFaO5t/view?usp=sharing
        boolean thirdCrossesEMA13 = quote3.getLow() < screenTwoEMA13.get(screenTwoEMA13Count - 3).getValue()
                && quote3.getHigh() > screenTwoEMA13.get(screenTwoEMA13Count - 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < screenTwoEMA13.get(screenTwoEMA13Count - 2).getValue()
                && quote2.getHigh() > screenTwoEMA13.get(screenTwoEMA13Count - 2).getValue();
        boolean lastAboveEMA13 = quote1.getLow() > screenTwoEMA13.get(screenTwoEMA13Count - 1).getValue()
                && quote1.getHigh() > screenTwoEMA13.get(screenTwoEMA13Count - 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastAboveEMA13) {
            Log.recordCode(Codes.LAST_BAR_ABOVE, screen2);
            Log.addDebugLine("Третий и второй пересекли ЕМА13, а последний полностью выше");
            return null;
        }

        // фильтровать ситуации, когда последний столбик имеет тень ниже, чем третий с конца, например: https://drive.google.com/file/d/1-bxHShDKdKEBSk_ADBape7t9ZD4IaMA3/view?usp=sharing
        // это не обязательно
//        if (quote1.getLow() < quote2.getLow() && quote1.getLow() < quote3.getLow()) {
//            Log.addDebugLine("Последний столбик имеет тень ниже предыдуших двух");
//            return false;
//        }

        //попробовать посчитать среднюю длину баров и сравнить с ней последние три
        Double sum = screen2Quotes.stream().map(quote -> quote.getHigh() - quote.getLow()).reduce(Double::sum).get();
        double averageBarLength = sum / minimumBarsCount;
        double quote1Length = quote1.getHigh() - quote1.getLow();
        double quote2Length = quote2.getHigh() - quote2.getLow();
        double quote3Length = quote3.getHigh() - quote3.getLow();
        boolean quote1StrangeLength = quote1Length >= averageBarLength * multiplier;
        boolean quote2StrangeLength = quote2Length >= averageBarLength * multiplier;
        boolean quote3StrangeLength = quote3Length >= averageBarLength * multiplier;
        if (quote1StrangeLength || quote2StrangeLength || quote3StrangeLength) {
            Log.addDebugLine("Внимание: один из последних трех столбиков в " + multiplier + " раза выше среднего");
        }

        return quote1;
    }

    public static Quote buySignalType2(SymbolData screen1, SymbolData screen2) {

        if (screen1.quotes.isEmpty() || screen1.quotes.size() < minimumBarsCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen1.timeframe.name());
            Log.recordCode(Codes.NO_DATA_QUOTES, screen1);
            return null;
        }
        if (screen2.quotes.isEmpty() || screen2.quotes.size() < minimumBarsCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen2.timeframe.name());
            Log.recordCode(Codes.NO_DATA_QUOTES, screen2);
            return null;
        }

        List<Indicator> missingData = new ArrayList<>();
        screen1.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < minimumBarsCount) {
                missingData.add(indicator);
            }
        });
        screen2.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < minimumBarsCount) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(Codes.NO_DATA_INDICATORS, screen1);
            Log.recordCode(Codes.NO_DATA_INDICATORS, screen2);
            Log.addDebugLine("Нету данных по индикаторам: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return null;
        }

        List<Quote> screen1Quotes = screen1.quotes.subList(screen1.quotes.size() - minimumBarsCount, screen1.quotes.size());
        List<Quote> screen2Quotes = screen2.quotes.subList(screen2.quotes.size() - minimumBarsCount, screen2.quotes.size());

        List<EMA> screenOneEMA26 = screen1.indicators.get(EMA26);
        screenOneEMA26 = screenOneEMA26.subList(screenOneEMA26.size() - minimumBarsCount, screenOneEMA26.size());

        List<MACD> screenOneMACD = screen1.indicators.get(MACD);
        screenOneMACD = screenOneMACD.subList(screenOneMACD.size() - minimumBarsCount, screenOneMACD.size());

        List<EMA> screenTwoEMA13 = screen2.indicators.get(Indicator.EMA13);
        screenTwoEMA13 = screenTwoEMA13.subList(screenTwoEMA13.size() - minimumBarsCount, screenTwoEMA13.size());

        List<MACD> screenTwoMACD = screen2.indicators.get(Indicator.MACD);
        screenTwoMACD = screenTwoMACD.subList(screenTwoMACD.size() - minimumBarsCount, screenTwoMACD.size());

        List<Stoch> screenTwoStochastic = screen2.indicators.get(Indicator.STOCH);
        screenTwoStochastic = screenTwoStochastic.subList(screenTwoStochastic.size() - minimumBarsCount, screenTwoStochastic.size());

        if (Context.screenTwoDay != null && !screen2.quotes.get(screen2.quotes.size() - 1).getNativeDate().contains(Context.screenTwoDay)) {
            return null;
        }

        // первый экран

        // проверка тренда
        boolean uptrendCheckOnMultipleBars = TrendFunctions.uptrendCheckOnMultipleBars(screen1, NUMBER_OF_EMA26_VALUES_TO_CHECK);
        //boolean uptrendCheckOnLastBar = TrendFunctions.uptrendCheckOnLastBar(screen1); плохая проверка
        if (!uptrendCheckOnMultipleBars) {
            Log.recordCode(Codes.NO_UPTREND, screen1);
            Log.addDebugLine("Не обнаружен восходящий тренд на долгосрочном экране");
            return null;
        }

        // На первом экране последние 4 Quote.low не должны понижаться
        // (эта проверка уже есть в Functions.isUptrend, но пусть тут тоже будет)
        Quote q4 = screen1Quotes.get(minimumBarsCount - 4);
        Quote q3 = screen1Quotes.get(minimumBarsCount - 3);
        Quote q2 = screen1Quotes.get(minimumBarsCount - 2);
        Quote q1 = screen1Quotes.get(minimumBarsCount - 1);
        if (q4.getLow() >= q3.getLow() && q3.getLow() >= q2.getLow() && q2.getLow() >= q1.getLow()) {
            // допустимо только, если последний столбик зеленый
            if (q1.getClose() < q1.getOpen()) {
                Log.recordCode(Codes.UPTREND_FAILING, screen1);
                Log.addDebugLine("Последние 4 столбика на первом экране понижаются");
                return null;
            } else {
                Log.addDebugLine("Последние 4 столбика на первом экране понижаются, но крайний правый закрылся выше открытия");
            }
        }

        // второй экран

        // гистограмма должна быть ниже нуля и начать повышаться: проверить на ДВУХ последних значениях

        Double macd2 = screenTwoMACD.get(screenTwoMACD.size() - 2).getHistogram(); // 2 от правого края
        Double macd1 = screenTwoMACD.get(screenTwoMACD.size() - 1).getHistogram(); // последняя

        boolean histogramBelowZero = macd2 < 0 && macd1 < 0;
        if (!histogramBelowZero) {
            Log.recordCode(Codes.HISTOGRAM_NOT_BELOW_ZERO, screen2);
            Log.addDebugLine("Гистограмма на втором экране не ниже нуля");
            return null;
        }

        boolean ascendingHistogram = macd2 < macd1;
        if (!ascendingHistogram) {
            Log.recordCode(Codes.HISTOGRAM_NOT_ASCENDING, screen2);
            Log.addDebugLine("Гистограмма на втором экране не повышается");
            return null;
        }

        // стохастик должен подниматься из зоны перепроданности: проверить на ДВУХ последних значениях

        Stoch stoch2 = screenTwoStochastic.get(screenTwoStochastic.size() - 2);
        Stoch stoch1 = screenTwoStochastic.get(screenTwoStochastic.size() - 1);

        // %D повышается (достаточно, чтобы последний был больше прошлого)
        boolean ascendingStochastic = stoch1.getSlowD() > stoch2.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(Codes.STOCH_NOT_ASCENDING, screen1);
            Log.addDebugLine("Стохастик %D не растет на втором экране");
            return null;
        }

        // проверка перепроданности

        // вторая с конца %K ниже STOCH_OVERSOLD, и последняя выше
        boolean isOversoldK = stoch2.getSlowK() <= STOCH_OVERSOLD && stoch1.getSlowK() > stoch2.getSlowK();
        // вторая с конца %D ниже STOCH_OVERSOLD, и последняя выше
        boolean isOversoldD = stoch2.getSlowD() <= STOCH_OVERSOLD
                && stoch1.getSlowD() > stoch2.getSlowD();

        if (!isOversoldK || !isOversoldD) {
            Log.recordCode(Codes.STOCH_NOT_ASCENDING_FROM_OVERSOLD, screen1);
            Log.addDebugLine("Стохастик не поднимается из перепроданности " + STOCH_OVERSOLD + ". %D: " + isOversoldD + "; %K: " + isOversoldK);
            return null;
        }

        // ценовые бары должны пересекать ЕМА13 и должны подниматься

        // обязательное условие 1
        // убедиться сначала, что high у последних ДВУХ столбиков повышается
        Quote preLastQuote = screen2Quotes.get(minimumBarsCount - 2);
        Quote lastQuote = screen2Quotes.get(minimumBarsCount - 1);
        boolean ascendingBarHigh = preLastQuote.getHigh() < lastQuote.getHigh();
        if (!ascendingBarHigh) {
            Log.recordCode(Codes.QUOTE_HIGH_NOT_GROWING, screen2);
            Log.addDebugLine("Quote.high не растет последовательно");
            return null;
        }
        EMA preLastEMA = screenTwoEMA13.get(minimumBarsCount - 2);
        EMA lastEMA = screenTwoEMA13.get(minimumBarsCount - 1);

        // оба столбика ниже ЕМА - отказ
        if (isQuoteBelowEMA(preLastQuote, preLastEMA.getValue()) && isQuoteBelowEMA(lastQuote, lastEMA.getValue())) {
            Log.addDebugLine("Оба последних столбика ниже ЕМА");
            Log.recordCode(Codes.QUOTES_BELOW_EMA, screen2);
            return null;
        }

        // оба столбика выше ЕМА - отказ
        if (isQuoteAboveEMA(preLastQuote, preLastEMA.getValue()) && isQuoteAboveEMA(lastQuote, lastEMA.getValue())) {
            Log.addDebugLine("Оба последних столбика выше ЕМА");
            Log.recordCode(Codes.QUOTES_ABOVE_EMA, screen2);
            return null;
        }

        // предпоследний ниже ЕМА, последний пересекает или выше - ОК
        boolean crossingRule1 = isQuoteBelowEMA(preLastQuote, preLastEMA.getValue())
                && (isQuoteCrossedEMA(lastQuote, lastEMA.getValue()) || isQuoteAboveEMA(lastQuote, lastEMA.getValue()));

        // предпоследний ниже ЕМА, последний пересекает - ОК
        boolean crossingRule2 = isQuoteBelowEMA(preLastQuote, preLastEMA.getValue()) && isQuoteCrossedEMA(lastQuote, lastEMA.getValue());

        // препдоследний и последний пересекают ЕМА - ОК
        boolean crossingRule3 = isQuoteCrossedEMA(preLastQuote, preLastEMA.getValue()) && isQuoteCrossedEMA(lastQuote, lastEMA.getValue());

        // предпоследний пересекает ЕМА, последний выше (может быть поздно входить в сделку, нужно смотреть на график) - ОК
        boolean crossingRule4 = isQuoteCrossedEMA(preLastQuote, preLastEMA.getValue()) && isQuoteAboveEMA(lastQuote, lastEMA.getValue());

        if (!crossingRule1 || !crossingRule2 || !crossingRule3 || !crossingRule4) {
            Log.addDebugLine("Не выполняется правило пересечения ЕМА");
            Log.recordCode(Codes.CROSSING_RULE_VIOLATED, screen2);
            return null;
        }

        //попробовать посчитать среднюю длину баров и сравнить с ней последние три
        Double sum = screen2Quotes.stream().map(quote -> quote.getHigh() - quote.getLow()).reduce(Double::sum).get();
        double averageBarLength = sum / minimumBarsCount;
        double quote1Length = lastQuote.getHigh() - lastQuote.getLow();
        double quote2Length = preLastQuote.getHigh() - preLastQuote.getLow();
        boolean quote1StrangeLength = quote1Length >= averageBarLength * multiplier;
        boolean quote2StrangeLength = quote2Length >= averageBarLength * multiplier;
        if (quote1StrangeLength || quote2StrangeLength) {
            Log.addDebugLine("Внимание: один из последних трех столбиков в " + multiplier + " раза выше среднего");
        }

        return lastQuote;
    }

    public static Quote sellSignal(SymbolData screen1, SymbolData screen2) {

        if (screen1.quotes.isEmpty()) {
            Log.recordCode(Codes.NO_DATA_QUOTES, screen1);
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen1.timeframe.name());
            return null;
        }
        if (screen2.quotes.isEmpty()) {
            Log.recordCode(Codes.NO_DATA_QUOTES, screen2);
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen2.timeframe.name());
            return null;
        }

        List<Indicator> missingData = new ArrayList<>();
        screen1.indicators.forEach((indicator, value) -> {
            if (value.isEmpty()) {
                missingData.add(indicator);
            }
        });
        screen2.indicators.forEach((indicator, value) -> {
            if (value.isEmpty()) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(Codes.NO_DATA_INDICATORS, screen1);
            Log.addDebugLine("Нету данных по индикаторам: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return null;
        }

        List<Quote> screen1Quotes = screen1.quotes.subList(screen1.quotes.size() - minimumBarsCount, screen1.quotes.size());
        List<Quote> screen2Quotes = screen2.quotes.subList(screen2.quotes.size() - minimumBarsCount, screen2.quotes.size());

        List<EMA> screenOneEMA26 = screen1.indicators.get(EMA26);
        screenOneEMA26 = screenOneEMA26.subList(screenOneEMA26.size() - minimumBarsCount, screenOneEMA26.size());

        List<MACD> screenOneMACD = screen1.indicators.get(MACD);
        screenOneMACD = screenOneMACD.subList(screenOneMACD.size() - minimumBarsCount, screenOneMACD.size());

        List<EMA> screenTwoEMA13 = screen2.indicators.get(EMA13);
        screenTwoEMA13 = screenTwoEMA13.subList(screenTwoEMA13.size() - minimumBarsCount, screenTwoEMA13.size());

        List<MACD> screenTwoMACD = screen2.indicators.get(MACD);
        screenTwoMACD = screenTwoMACD.subList(screenTwoMACD.size() - minimumBarsCount, screenTwoMACD.size());

        List<Stoch> screenTwoStochastic = screen2.indicators.get(STOCH);
        screenTwoStochastic = screenTwoStochastic.subList(screenTwoStochastic.size() - minimumBarsCount, screenTwoStochastic.size());

        // первый экран

        // проверка тренда
        boolean downtrendCheckOnMultipleBars = TrendFunctions.downtrendCheckOnMultipleBars(screen1, NUMBER_OF_EMA26_VALUES_TO_CHECK);
        //boolean downtrendCheckOnLastBar = TrendFunctions.downtrendCheckOnLastBar(screen1); опасно
        if (!downtrendCheckOnMultipleBars) {
            Log.recordCode(Codes.NO_DOWNTREND, screen1);
            Log.addDebugLine("Не обнаружен нисходящий тренд на долгосрочном экране");
            return null;
        }

        // На первом экране последние 4 Quote.high не должны повышаться
        // (эта проверка уже есть в Functions.isDownrend, но пусть тут тоже будет)
        Quote q4 = screen1Quotes.get(minimumBarsCount - 4);
        Quote q3 = screen1Quotes.get(minimumBarsCount - 3);
        Quote q2 = screen1Quotes.get(minimumBarsCount - 2);
        Quote q1 = screen1Quotes.get(minimumBarsCount - 1);
        if (q4.getHigh() <= q3.getHigh() && q3.getHigh() <= q2.getHigh() && q2.getHigh() <= q1.getHigh()) {
            // допустимо только, если последний столбик красный
            if (q1.getClose() > q1.getOpen()) {
                Log.recordCode(Codes.DOWNTREND_FAILING, screen1);
                Log.addDebugLine("Последние 4 столбика на первом экране повышаются");
                return null;
            } else {
                Log.addDebugLine("Последние 4 столбика на первом экране повышаются, но крайний правый закрылся ниже открытия");
            }
        }

        // второй экран

        // гистограмма должна быть выше нуля и начать снижаться: проверить на трех последних значениях

        Double macd3 = screenTwoMACD.get(screenTwoMACD.size() - 3).getHistogram(); // 3 от правого края
        Double macd2 = screenTwoMACD.get(screenTwoMACD.size() - 2).getHistogram(); // 2 от правого края
        Double macd1 = screenTwoMACD.get(screenTwoMACD.size() - 1).getHistogram(); // последняя

        boolean histogramAboveZero = macd3 > 0 && macd2 > 0 && macd1 > 0;
        if (!histogramAboveZero) {
            Log.recordCode(Codes.HISTOGRAM_NOT_ABOVE_ZERO, screen2);
            Log.addDebugLine("Гистограмма на втором экране не выше нуля");
            return null;
        }

        boolean ascendingHistogram = macd3 > macd2 && macd2 > macd1;
        if (!ascendingHistogram) {
            Log.recordCode(Codes.HISTOGRAM_NOT_DESCENDING, screen2);
            Log.addDebugLine("Гистограмма на втором экране не снижается");
            return null;
        }

        // стохастик должен снижаться из зоны перекупленности: проверить на трех последних значениях

        Stoch stoch3 = screenTwoStochastic.get(screenTwoStochastic.size() - 3);
        Stoch stoch2 = screenTwoStochastic.get(screenTwoStochastic.size() - 2);
        Stoch stoch1 = screenTwoStochastic.get(screenTwoStochastic.size() - 1);
        // %D снижается (достаточно, чтобы последний был ниже прошлых двух)
        boolean ascendingStochastic = stoch1.getSlowD() < stoch2.getSlowD() && stoch1.getSlowD() < stoch3.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(Codes.STOCH_NOT_DESCENDING, screen2);
            Log.addDebugLine("Стохастик %D не снижается на втором экране");
            return null;
        }

        // проверка перекупленности

        // третья или вторая с конца %K выше STOCH_OVERSOLD, и самая последняя ниже первой
        boolean isOverboughtK = (stoch3.getSlowK() >= STOCH_OVERBOUGHT || stoch2.getSlowK() >= STOCH_OVERBOUGHT)
                && (stoch1.getSlowK() < stoch3.getSlowK());
        // третья или вторая с конца %D выше STOCH_OVERSOLD, и самая последняя ниже обеих
        boolean isOverboughtD = (stoch3.getSlowD() >= STOCH_OVERBOUGHT || stoch2.getSlowD() >= STOCH_OVERBOUGHT)
                && (stoch1.getSlowD() < stoch2.getSlowD())
                && (stoch1.getSlowD() < stoch3.getSlowD());

        if (!isOverboughtK || !isOverboughtD) {
            Log.recordCode(Codes.STOCH_NOT_DESCENDING_FROM_OVERBOUGHT, screen2);
            Log.addDebugLine("Стохастик не снижается из перекупленности " + STOCH_OVERBOUGHT + ". %D: " + isOverboughtD + "; %K: " + isOverboughtK);
            return null;
        }

        // ценовые бары должны пересекать ЕМА13 и должны снижаться

        // убедиться сначала, что low у последних трех столбиков снижается
        Quote quote3 = screen2Quotes.get(minimumBarsCount - 3);
        Quote quote2 = screen2Quotes.get(minimumBarsCount - 2);
        Quote quote1 = screen2Quotes.get(minimumBarsCount - 1);
        // наверно descendingBarLow=false + descendingBarClose=false достаточно для отказа
        boolean descendingBarLow = quote3.getLow() > quote2.getLow() && quote2.getLow() < quote1.getLow();
        boolean descendingBarClose = quote3.getClose() > quote2.getClose() && quote2.getClose() > quote1.getClose();

        if (!descendingBarLow) {
            Log.recordCode(Codes.QUOTE_LOW_NOT_LOWING, screen2);
            Log.addDebugLine("Quote.low не снижается последовательно");
            if (!descendingBarClose) {
                Log.recordCode(Codes.QUOTE_CLOSE_NOT_LOWING, screen2);
                Log.addDebugLine("Quote.close не снижается последовательно");
                // третий с конца весь выше ЕМА13, а второй и последний пересекли ее
                boolean thirdBarAboveEMA13 = quote3.getLow() > screenTwoEMA13.get(minimumBarsCount - 3).getValue()
                        && quote3.getHigh() > screenTwoEMA13.get(minimumBarsCount - 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= screenTwoEMA13.get(minimumBarsCount - 2).getValue()
                        && quote2.getHigh() >= screenTwoEMA13.get(minimumBarsCount - 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= screenTwoEMA13.get(minimumBarsCount - 1).getValue()
                        && quote1.getHigh() >= screenTwoEMA13.get(minimumBarsCount - 1).getValue();
                boolean crossingRule = thirdBarAboveEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    Log.addDebugLine("Третий с конца" + (thirdBarAboveEMA13 ? " " : " не ") + "выше ЕМА13");
                    Log.addDebugLine("Предпоследний" + (secondBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.addDebugLine("Последний" + (lastBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.recordCode(Codes.CROSSING_RULE_VIOLATED, screen2);
                    return null;
                } else {
                    Log.recordCode(Codes.CROSSING_RULE_PASSED, screen2);
                    Log.addDebugLine("Правило пересечения выполняется");
                }
            } else {
                Log.recordCode(Codes.QUOTE_CLOSE_LOWING, screen2);
                Log.addDebugLine("Есть снижение Quote.close");
            }
        } else {
            Log.recordCode(Codes.QUOTE_HIGH_LOWING, screen2);
            Log.addDebugLine("Есть снижение Quote.high");
        }

        // нужно фильтровать ситуацию, когда третий и второй пересекают ЕМА13, а последний целиком ниже (то есть уже момент потерян)
        // третий может открыться и закрыться ниже, и это допустимо
        boolean thirdCrossesEMA13 = quote3.getLow() < screenTwoEMA13.get(minimumBarsCount - 3).getValue()
                && quote3.getHigh() > screenTwoEMA13.get(minimumBarsCount - 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < screenTwoEMA13.get(minimumBarsCount - 2).getValue()
                && quote2.getHigh() > screenTwoEMA13.get(minimumBarsCount - 2).getValue();
        boolean lastBelowEMA13 = quote1.getLow() < screenTwoEMA13.get(minimumBarsCount - 1).getValue()
                && quote1.getHigh() < screenTwoEMA13.get(minimumBarsCount - 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastBelowEMA13) {
            Log.recordCode(Codes.LAST_BAR_BELOW, screen2);
            Log.addDebugLine("Третий и второй пересекли ЕМА13, а последний полностью ниже");
            return null;
        }

        // фильтровать ситуации, когда последний столбик имеет тень ниже, чем третий с конца
        // это не обязательно
//        if (quote1.getLow() < quote2.getLow() && quote1.getLow() < quote3.getLow()) {
//            Log.addDebugLine("Последний столбик имеет тень ниже предыдуших двух");
//            return false;
//        }

        //попробовать посчитать среднюю длину баров и сравнить с ней последние три
        Double sum = screen2Quotes.stream().map(quote -> quote.getHigh() - quote.getLow()).reduce(Double::sum).get();
        double averageBarLength = sum / minimumBarsCount;
        double quote1Length = quote1.getHigh() - quote1.getLow();
        double quote2Length = quote2.getHigh() - quote2.getLow();
        double quote3Length = quote3.getHigh() - quote3.getLow();
        boolean quote1StrangeLength = quote1Length >= averageBarLength * multiplier;
        boolean quote2StrangeLength = quote2Length >= averageBarLength * multiplier;
        boolean quote3StrangeLength = quote3Length >= averageBarLength * multiplier;
        if (quote1StrangeLength || quote2StrangeLength || quote3StrangeLength) {
            Log.addDebugLine("Внимание: один из последних трех столбиков в " + multiplier + " раза больше среднего");
        }

        return quote1;
    }

    private static boolean isQuoteCrossedEMA(Quote quote, double emaValue) {
        return quote.getLow() <= emaValue && quote.getHigh() >= emaValue;
    }

    private static boolean isQuoteBelowEMA(Quote quote, double emaValue) {
        return quote.getLow() < emaValue && quote.getHigh() < emaValue;
    }

    private static boolean isQuoteAboveEMA(Quote quote, double emaValue) {
        return quote.getLow() > emaValue && quote.getHigh() > emaValue;
    }

}
