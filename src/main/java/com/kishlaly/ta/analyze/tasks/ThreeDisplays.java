package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.analyze.functions.TrendFunctions;
import com.kishlaly.ta.analyze.tasks.blocks.ScreenOneBlock;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Screens;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.MACD;
import com.kishlaly.ta.model.indicators.*;
import com.kishlaly.ta.utils.IndicatorUtils;
import com.kishlaly.ta.utils.Log;
import com.kishlaly.ta.utils.Quotes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kishlaly.ta.analyze.BlockResultCode.*;
import static com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config.*;
import static com.kishlaly.ta.model.indicators.Indicator.*;
import static com.kishlaly.ta.utils.Quotes.resolveMinBarsCount;

/**
 * Индикаторы:
 * EMA26 (close) на первом экране
 * MACD (12 26 9 close) на втором экране
 * EMA13 (close) на втором экране
 * STOCH (14 1 3 close) на втором экране
 */
public class ThreeDisplays {

    public static class Config {

        // 3 дает меньше сигналов, но они надежнее
        public static int NUMBER_OF_EMA26_VALUES_TO_CHECK = 4;

        // для поиска аномально длинных баров
        public static int multiplier = 2;

        public static int STOCH_OVERSOLD = 40;
        public static int STOCH_OVERBOUGHT = 70;
        public static int STOCH_VALUES_TO_CHECK = 5;

        // в процентах от середины до вершины канала
        public static int FILTER_BY_KELTNER = 40;

        // фильтрация сигналов, если котировка закрылась выше FILTER_BY_KELTNER
        // тесты показывают результат лучше, когда эта проверка выключена
        public static boolean FILTER_BY_KELTNER_ENABLED;
    }

    public static BlockResult buySignal(Screens screens, List<TaskBlock> blocks) {

        SymbolData screen1 = screens.screen1;
        SymbolData screen2 = screens.screen2;

        Quotes.trim(screen1);
        Quotes.trim(screen2);
        IndicatorUtils.trim(screen1);
        IndicatorUtils.trim(screen2);

        List<TaskBlock> screenOneBlocks = blocks
                .stream()
                .filter(block -> block instanceof ScreenOneBlock)
                .collect(Collectors.toList());

        boolean screenOneAllBlockaValid = true;
        BlockResult screenOneResult = null;

        for (int i = 0; i < screenOneBlocks.size(); i++) {
            TaskBlock block = screenOneBlocks.get(i);
            screenOneResult = block.check(screen1);
            if (!screenOneResult.isOk()) {
                screenOneAllBlockaValid = false;
                break;
            }
        }
        if (!screenOneAllBlockaValid) {
            return screenOneResult;
        }

        List<TaskBlock> screenTwoBlocks = blocks
                .stream()
                .filter(block -> block instanceof ScreenOneBlock)
                .collect(Collectors.toList());
        BlockResult screenTwoResult = null;
        for (int i = 0; i < screenTwoBlocks.size(); i++) {
            TaskBlock block = screenTwoBlocks.get(i);
            screenTwoResult = block.check(screens.screen2);
            if (!screenTwoResult.isOk()) {
                break;
            }
        }
        return screenTwoResult;

        List<EMA> screen_1_EMA26 = screen_1.indicators.get(EMA26);
        screen_1_EMA26 = screen_1_EMA26.subList(screen_1_EMA26.size() - screen_1_MinBarCount, screen_1_EMA26.size());

        List<MACD> screen_1_MACD = screen_1.indicators.get(MACD);
        screen_1_MACD = screen_1_MACD.subList(screen_1_MACD.size() - screen_1_MinBarCount, screen_1_MACD.size());

        List<EMA> screen_2_EMA13 = screen_2.indicators.get(Indicator.EMA13);
        screen_2_EMA13 = screen_2_EMA13.subList(screen_2_EMA13.size() - screen_2_MinBarCount, screen_2_EMA13.size());

        List<MACD> screen_2_MACD = screen_2.indicators.get(Indicator.MACD);
        screen_2_MACD = screen_2_MACD.subList(screen_2_MACD.size() - screen_2_MinBarCount, screen_2_MACD.size());

        List<Stoch> screen_2_Stochastic = screen_2.indicators.get(Indicator.STOCH);
        screen_2_Stochastic = screen_2_Stochastic.subList(screen_2_Stochastic.size() - screen_2_MinBarCount, screen_2_Stochastic.size());

        // первый экран

        // проверка тренда
        boolean uptrendCheckOnMultipleBars = TrendFunctions.uptrendCheckOnMultipleBars(screen_1, screen_1_MinBarCount, NUMBER_OF_EMA26_VALUES_TO_CHECK);
        //boolean uptrendCheckOnLastBar = TrendFunctions.uptrendCheckOnLastBar(screen_1); плохая проверка
        Quote lastChartQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);
        if (!uptrendCheckOnMultipleBars) {
            Log.recordCode(NO_UPTREND, screen_1);
            Log.addDebugLine("Не обнаружен восходящий тренд на долгосрочном экране");
            return new BlockResult(lastChartQuote, NO_UPTREND);
        }

        // На первом экране последние 4 Quote.low не должны понижаться
        // (эта проверка уже есть в Functions.isUptrend, но пусть тут тоже будет)
        Quote q4 = screen_1_Quotes.get(screen_1_MinBarCount - 4);
        Quote q3 = screen_1_Quotes.get(screen_1_MinBarCount - 3);
        Quote q2 = screen_1_Quotes.get(screen_1_MinBarCount - 2);
        Quote q1 = screen_1_Quotes.get(screen_1_MinBarCount - 1);
        if (q4.getLow() >= q3.getLow() && q3.getLow() >= q2.getLow() && q2.getLow() >= q1.getLow()) {
            // допустимо только, если последний столбик зеленый
            if (q1.getClose() < q1.getOpen()) {
                Log.recordCode(UPTREND_FAILING, screen_1);
                Log.addDebugLine("Последние 4 столбика на первом экране понижаются");
                return new BlockResult(lastChartQuote, UPTREND_FAILING);
            } else {
                Log.addDebugLine("Последние 4 столбика на первом экране понижаются, но крайний правый закрылся выше открытия");
            }
        }

        // второй экран

        // гистограмма должна быть ниже нуля и начать повышаться: проверить на трех последних значениях

        Double macd3 = screen_2_MACD.get(screen_2_MACD.size() - 3).getHistogram(); // 3 от правого края
        Double macd2 = screen_2_MACD.get(screen_2_MACD.size() - 2).getHistogram(); // 2 от правого края
        Double macd1 = screen_2_MACD.get(screen_2_MACD.size() - 1).getHistogram(); // последняя

        boolean histogramBelowZero = macd3 < 0 && macd2 < 0 && macd1 < 0;
        if (!histogramBelowZero) {
            Log.recordCode(HISTOGRAM_NOT_BELOW_ZERO, screen_2);
            Log.addDebugLine("Гистограмма на втором экране не ниже нуля");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_BELOW_ZERO);
        }

        boolean ascendingHistogram = macd3 < macd2 && macd2 < macd1;
        if (!ascendingHistogram) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING, screen_2);
            Log.addDebugLine("Гистограмма на втором экране не повышается");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_ASCENDING);
        }

        // стохастик должен подниматься из зоны перепроданности: проверить на трех последних значениях

        Stoch stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        // %D повышается (достаточно, чтобы последний был больше прошлых двух)
        boolean ascendingStochastic = stoch1.getSlowD() > stoch2.getSlowD() && stoch1.getSlowD() > stoch3.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(STOCH_NOT_ASCENDING, screen_1);
            Log.addDebugLine("Стохастик %D не растет на втором экране");
            return new BlockResult(lastChartQuote, STOCH_NOT_ASCENDING);
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
            Log.recordCode(STOCH_NOT_ASCENDING_FROM_OVERSOLD, screen_1);
            Log.addDebugLine("Стохастик не поднимается из перепроданности " + STOCH_OVERSOLD + ". %D: " + isOversoldD + "; %K: " + isOversoldK);
            return new BlockResult(lastChartQuote, STOCH_NOT_ASCENDING_FROM_OVERSOLD);
        }

        // ценовые бары должны пересекать ЕМА13 и должны подниматься

        // обязательное условие 1
        // убедиться сначала, что high у последних трех столбиков повышается
        Quote quote3 = screen_2_Quotes.get(screen_2_MinBarCount - 3);
        Quote quote2 = screen_2_Quotes.get(screen_2_MinBarCount - 2);
        Quote quote1 = screen_2_Quotes.get(screen_2_MinBarCount - 1);
        // наверно ascendingBarHigh=false + ascendingBarClose=false достаточно для отказа
        boolean ascendingBarHigh = quote3.getHigh() < quote2.getHigh() && quote2.getHigh() < quote1.getHigh();
        boolean ascendingBarClose = quote3.getClose() < quote2.getClose() && quote2.getClose() < quote1.getClose();

        int screen_2_EMA13Count = screen_2_EMA13.size();
        if (!ascendingBarHigh) {
            Log.recordCode(QUOTE_HIGH_NOT_GROWING, screen_2);
            Log.addDebugLine("Quote.high не растет последовательно");
            if (!ascendingBarClose) {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_NOT_GROWING, screen_2);
                Log.addDebugLine("Quote.close не растет последовательно");
                // третий с конца весь ниже ЕМА13, а второй и последний пересекли
                boolean thirdBarBelowEMA13 = quote3.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue()
                        && quote3.getHigh() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue()
                        && quote2.getHigh() >= screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue()
                        && quote1.getHigh() >= screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue();
                boolean crossingRule = thirdBarBelowEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    Log.addDebugLine("Третий с конца" + (thirdBarBelowEMA13 ? " " : " не ") + "ниже ЕМА13");
                    Log.addDebugLine("Предпоследний" + (secondBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.addDebugLine("Последний" + (lastBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.recordCode(CROSSING_RULE_VIOLATED, screen_2);
                    return new BlockResult(lastChartQuote, CROSSING_RULE_VIOLATED);
                } else {
                    Log.recordCode(BlockResultCode.CROSSING_RULE_PASSED, screen_2);
                    Log.addDebugLine("Правило пересечения выполняется");
                }
            } else {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_GROWING, screen_2);
                Log.addDebugLine("Есть рост Quote.close");
            }
        } else {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_GROWING, screen_2);
            Log.addDebugLine("Есть рост Quote.high");
        }

        // нужно фильтровать ситуацию, когда третий и второй пересекают ЕМА13, а послдений целиком выше (момент входа в сделку упущен)
        // третий может открыться и закрыться выше, и это допустимо: https://drive.google.com/file/d/15XkXFKBQbTjeNjBn03NrF9JawCBFaO5t/view?usp=sharing
        boolean thirdCrossesEMA13 = quote3.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue()
                && quote3.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue()
                && quote2.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 2).getValue();
        boolean lastAboveEMA13 = quote1.getLow() > screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue()
                && quote1.getHigh() > screen_2_EMA13.get(screen_2_EMA13Count - 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastAboveEMA13) {
            Log.recordCode(LAST_BAR_ABOVE, screen_2);
            Log.addDebugLine("Третий и второй пересекли ЕМА13, а последний полностью выше");
            return new BlockResult(lastChartQuote, LAST_BAR_ABOVE);
        }

        // фильтровать ситуации, когда последний столбик имеет тень ниже, чем третий с конца, например: https://drive.google.com/file/d/1-bxHShDKdKEBSk_ADBape7t9ZD4IaMA3/view?usp=sharing
        // это не обязательно
//        if (quote1.getLow() < quote2.getLow() && quote1.getLow() < quote3.getLow()) {
//            Log.addDebugLine("Последний столбик имеет тень ниже предыдуших двух");
//            return false;
//        }

        //попробовать посчитать среднюю длину баров и сравнить с ней последние три
        Double sum = screen_2_Quotes.stream().map(quote -> quote.getHigh() - quote.getLow()).reduce(Double::sum).get();
        double averageBarLength = sum / screen_2_MinBarCount;
        double quote1Length = quote1.getHigh() - quote1.getLow();
        double quote2Length = quote2.getHigh() - quote2.getLow();
        double quote3Length = quote3.getHigh() - quote3.getLow();
        boolean quote1StrangeLength = quote1Length >= averageBarLength * multiplier;
        boolean quote2StrangeLength = quote2Length >= averageBarLength * multiplier;
        boolean quote3StrangeLength = quote3Length >= averageBarLength * multiplier;
        if (quote1StrangeLength || quote2StrangeLength || quote3StrangeLength) {
            Log.addDebugLine("Внимание: один из последних трех столбиков в " + multiplier + " раза выше среднего");
        }

        return new BlockResult(quote1, OK);
    }

    public static BlockResult buySignalType2(SymbolData screen_1, SymbolData screen_2) {

        int screen_1_MinBarCount = resolveMinBarsCount(screen_1.timeframe);
        int screen_2_MinBarCount = resolveMinBarsCount(screen_2.timeframe);

        if (screen_1.quotes.isEmpty() || screen_1.quotes.size() < screen_1_MinBarCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_1.timeframe.name());
            Log.recordCode(NO_DATA_QUOTES, screen_1);
            return new BlockResult(null, NO_DATA_QUOTES);
        }
        if (screen_2.quotes.isEmpty() || screen_2.quotes.size() < screen_2_MinBarCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_2.timeframe.name());
            Log.recordCode(NO_DATA_QUOTES, screen_2);
            return new BlockResult(null, NO_DATA_QUOTES);
        }

        List<Indicator> missingData = new ArrayList<>();
        screen_1.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < screen_1_MinBarCount) {
                missingData.add(indicator);
            }
        });
        screen_2.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < screen_2_MinBarCount) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(NO_DATA_INDICATORS, screen_1);
            Log.recordCode(NO_DATA_INDICATORS, screen_2);
            Log.addDebugLine("Нету данных по индикаторам: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return new BlockResult(null, NO_DATA_INDICATORS);
        }

        List<Quote> screen_1_Quotes = screen_1.quotes.subList(screen_1.quotes.size() - screen_1_MinBarCount, screen_1.quotes.size());
        List<Quote> screen_2_Quotes = screen_2.quotes.subList(screen_2.quotes.size() - screen_2_MinBarCount, screen_2.quotes.size());

        List<EMA> screen_1_EMA26 = screen_1.indicators.get(EMA26);
        screen_1_EMA26 = screen_1_EMA26.subList(screen_1_EMA26.size() - screen_1_MinBarCount, screen_1_EMA26.size());

        List<MACD> screen_1_MACD = screen_1.indicators.get(MACD);
        screen_1_MACD = screen_1_MACD.subList(screen_1_MACD.size() - screen_1_MinBarCount, screen_1_MACD.size());

        List<EMA> screen_2_EMA13 = screen_2.indicators.get(Indicator.EMA13);
        screen_2_EMA13 = screen_2_EMA13.subList(screen_2_EMA13.size() - screen_2_MinBarCount, screen_2_EMA13.size());

        List<MACD> screen_2_MACD = screen_2.indicators.get(Indicator.MACD);
        screen_2_MACD = screen_2_MACD.subList(screen_2_MACD.size() - screen_2_MinBarCount, screen_2_MACD.size());

        List<Stoch> screen_2_Stochastic = screen_2.indicators.get(Indicator.STOCH);
        screen_2_Stochastic = screen_2_Stochastic.subList(screen_2_Stochastic.size() - screen_2_MinBarCount, screen_2_Stochastic.size());

        List<Keltner> screen_2_Keltner = screen_2.indicators.get(KELTNER);
        screen_2_Keltner = screen_2_Keltner.subList(screen_2_Keltner.size() - screen_2_MinBarCount, screen_2_Keltner.size());

        // первый экран

        // проверка тренда
        boolean uptrendCheckOnMultipleBars = TrendFunctions.uptrendCheckOnMultipleBars(screen_1, screen_1_MinBarCount, NUMBER_OF_EMA26_VALUES_TO_CHECK);
        //boolean uptrendCheckOnLastBar = TrendFunctions.uptrendCheckOnLastBar(screen_1); плохая проверка
        Quote lastChartQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);
        if (!uptrendCheckOnMultipleBars) {
            Log.recordCode(NO_UPTREND, screen_1);
            Log.addDebugLine("Не обнаружен восходящий тренд на долгосрочном экране");
            return new BlockResult(lastChartQuote, NO_UPTREND);
        }

        // На первом экране последние 4 Quote.low не должны понижаться
        // (эта проверка уже есть в Functions.isUptrend, но пусть тут тоже будет)
        Quote q4 = screen_1_Quotes.get(screen_1_MinBarCount - 4);
        Quote q3 = screen_1_Quotes.get(screen_1_MinBarCount - 3);
        Quote q2 = screen_1_Quotes.get(screen_1_MinBarCount - 2);
        Quote q1 = screen_1_Quotes.get(screen_1_MinBarCount - 1);
        if (q4.getLow() >= q3.getLow() && q3.getLow() >= q2.getLow() && q2.getLow() >= q1.getLow()) {
            // допустимо только, если последний столбик зеленый
            if (q1.getClose() < q1.getOpen()) {
                Log.recordCode(UPTREND_FAILING, screen_1);
                Log.addDebugLine("Последние 4 столбика на первом экране понижаются");
                return new BlockResult(lastChartQuote, UPTREND_FAILING);
            } else {
                Log.addDebugLine("Последние 4 столбика на первом экране понижаются, но крайний правый закрылся выше открытия");
            }
        }

        // второй экран

        // гистограмма должна быть ниже нуля и начать повышаться: проверить на ДВУХ последних значениях

        Double macd2 = screen_2_MACD.get(screen_2_MACD.size() - 2).getHistogram(); // 2 от правого края
        Double macd1 = screen_2_MACD.get(screen_2_MACD.size() - 1).getHistogram(); // последняя

        boolean histogramBelowZero = macd2 < 0 && macd1 < 0;
        if (!histogramBelowZero) {
            Log.recordCode(HISTOGRAM_NOT_BELOW_ZERO, screen_2);
            Log.addDebugLine("Гистограмма на втором экране не ниже нуля");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_BELOW_ZERO);
        }

        boolean ascendingHistogram = macd2 < macd1;
        if (!ascendingHistogram) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING, screen_2);
            Log.addDebugLine("Гистограмма на втором экране не повышается");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_ASCENDING);
        }

        // стохастик должен подниматься из зоны перепроданности: проверить на ДВУХ последних значениях

        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);

        // %D повышается (достаточно, чтобы последний был больше прошлого)
        boolean ascendingStochastic = stoch1.getSlowD() > stoch2.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(STOCH_NOT_ASCENDING, screen_1);
            Log.addDebugLine("Стохастик %D не растет на втором экране");
            return new BlockResult(lastChartQuote, STOCH_NOT_ASCENDING);
        }

        // проверка перепроданности

        // нужно проверять несколько стохастиков влево от последнего значения
        // например, 5 последних: если ли среди них значения ниже STOCH_OVERSOLD
        // но при условии, что медленная линия у правого края была выше
        // тогда STOCH_OVERSOLD можно держать поменьше, эдак 30
        boolean wasOversoldRecently = false;
        for (int i = screen_2_MinBarCount - STOCH_VALUES_TO_CHECK; i < screen_2_MinBarCount; i++) {
            Stoch stoch = screen_2_Stochastic.get(i);
            if (stoch.getSlowD() <= STOCH_OVERSOLD || stoch.getSlowK() <= STOCH_OVERSOLD) {
                wasOversoldRecently = true;
            }
        }
        if (!wasOversoldRecently) {
            Log.recordCode(STOCH_WAS_NOT_OVERSOLD_RECENTLY, screen_2);
            Log.addDebugLine("Стохастик не был в перепроданности на последних " + STOCH_VALUES_TO_CHECK + " значениях");
            return new BlockResult(lastChartQuote, STOCH_WAS_NOT_OVERSOLD_RECENTLY);
        }

        boolean lastStochIsBigger = stoch1.getSlowD() > stoch2.getSlowD();
        if (!lastStochIsBigger) {
            Log.recordCode(STOCH_NOT_ASCENDING, screen_2);
            Log.addDebugLine("Последние два значения стохастика не повышаются");
            return new BlockResult(lastChartQuote, STOCH_NOT_ASCENDING);
        }

// старый вариант
//        // вторая с конца %K ниже STOCH_OVERSOLD, и последняя выше
//        boolean isOversoldK = stoch2.getSlowK() <= STOCH_OVERSOLD && stoch1.getSlowK() > stoch2.getSlowK();
//        // вторая с конца %D ниже STOCH_OVERSOLD, и последняя выше
//        boolean isOversoldD = stoch2.getSlowD() <= STOCH_OVERSOLD
//                && stoch1.getSlowD() > stoch2.getSlowD();
//
//        if (!isOversoldK || !isOversoldD) {
//            Log.recordCode(STOCH_NOT_ASCENDING_FROM_OVERSOLD, screen_1);
//            Log.addDebugLine("Стохастик не поднимается из перепроданности " + STOCH_OVERSOLD + ". %D: " + isOversoldD + "; %K: " + isOversoldK);
//            return new TaskResult(screen_2_Quotes.get(screen_2_Quotes.size() - 1), STOCH_NOT_ASCENDING_FROM_OVERSOLD);
//        }

        // ценовые бары должны пересекать ЕМА13 и должны подниматься

        // обязательное условие 1
        // убедиться сначала, что high у последних ДВУХ столбиков повышается
        Quote preLastQuote = screen_2_Quotes.get(screen_2_MinBarCount - 2);
        Quote lastQuote = screen_2_Quotes.get(screen_2_MinBarCount - 1);
        boolean ascendingBarHigh = preLastQuote.getHigh() < lastQuote.getHigh();
        if (!ascendingBarHigh) {
            Log.recordCode(QUOTE_HIGH_NOT_GROWING, screen_2);
            Log.addDebugLine("Quote.high не растет последовательно");
            return new BlockResult(lastChartQuote, QUOTE_HIGH_NOT_GROWING);
        }
        EMA preLastEMA = screen_2_EMA13.get(screen_2_MinBarCount - 2);
        EMA lastEMA = screen_2_EMA13.get(screen_2_MinBarCount - 1);

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
            Log.recordCode(CROSSING_RULE_VIOLATED, screen_2);
            return new BlockResult(lastChartQuote, CROSSING_RULE_VIOLATED);
        }

        // фильтрация поздних входов, когда столбик закрылся выше FILTER_BY_KELTNER
        if (FILTER_BY_KELTNER_ENABLED) {
            Keltner lastKeltnerData = screen_2_Keltner.get(screen_2_MinBarCount - 1);
            double lastQuoteClose = lastQuote.getClose();
            double middle = lastKeltnerData.getMiddle();
            double top = lastKeltnerData.getTop();
            double diff = top - middle;
            double ratio = diff / 100 * FILTER_BY_KELTNER;
            double maxAllowedCloseValue = middle + ratio;
            if (lastQuoteClose >= maxAllowedCloseValue) {
                Log.addDebugLine("Последняя котировка закрылась выше " + FILTER_BY_KELTNER + "% расстояния от середины до вершины канала");
                Log.recordCode(QUOTE_CLOSED_ABOVE_KELTNER_RULE, screen_2);
                return new BlockResult(lastChartQuote, QUOTE_CLOSED_ABOVE_KELTNER_RULE);
            }
        }

        //попробовать посчитать среднюю длину баров и сравнить с ней последние три
        Double sum = screen_2_Quotes.stream().map(quote -> quote.getHigh() - quote.getLow()).reduce(Double::sum).get();
        double averageBarLength = sum / screen_2_MinBarCount;
        double quote1Length = lastQuote.getHigh() - lastQuote.getLow();
        double quote2Length = preLastQuote.getHigh() - preLastQuote.getLow();
        boolean quote1StrangeLength = quote1Length >= averageBarLength * multiplier;
        boolean quote2StrangeLength = quote2Length >= averageBarLength * multiplier;
        if (quote1StrangeLength || quote2StrangeLength) {
            Log.addDebugLine("Внимание: один из последних трех столбиков в " + multiplier + " раза выше среднего");
        }

        return new BlockResult(lastQuote, OK);
    }

    // проверить buy стратегию (вдохновитель [D] CFLT 20 Dec 2021)
    // первый экран - подумать TODO
    // второй экран -
    //    перепроданность ниже 20 у трех значений медленной линии стохастика и она повышается
    //    последние три столбика гистограммы повышаются
    //    два из трех последних баров зеленые
    //    последние два бара повышаются (quote.low & quote.high)
    //    последние два бара полностью ниже ЕМА13
    // вход на 7 центов выше закрытия последнего бара
    // TP на середине верхней половины канала Кельтнера
    public static BlockResult buySignalType3(SymbolData screen_1, SymbolData screen_2) {

        int screen_1_MinBarCount = resolveMinBarsCount(screen_1.timeframe);
        int screen_2_MinBarCount = resolveMinBarsCount(screen_2.timeframe);

        if (screen_1.quotes.isEmpty() || screen_1.quotes.size() < screen_1_MinBarCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_1.timeframe.name());
            Log.recordCode(NO_DATA_QUOTES, screen_1);
            return new BlockResult(null, NO_DATA_QUOTES);
        }
        if (screen_2.quotes.isEmpty() || screen_2.quotes.size() < screen_2_MinBarCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_2.timeframe.name());
            Log.recordCode(NO_DATA_QUOTES, screen_2);
            return new BlockResult(null, NO_DATA_QUOTES);
        }

        List<Indicator> missingData = new ArrayList<>();
        screen_1.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < screen_1_MinBarCount) {
                missingData.add(indicator);
            }
        });
        screen_2.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < screen_2_MinBarCount) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(NO_DATA_INDICATORS, screen_1);
            Log.recordCode(NO_DATA_INDICATORS, screen_2);
            Log.addDebugLine("Нету данных по индикаторам: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return new BlockResult(null, NO_DATA_INDICATORS);
        }

        List<Quote> screen_1_Quotes = screen_1.quotes.subList(screen_1.quotes.size() - screen_1_MinBarCount, screen_1.quotes.size());
        List<Quote> screen_2_Quotes = screen_2.quotes.subList(screen_2.quotes.size() - screen_2_MinBarCount, screen_2.quotes.size());

        List<EMA> screen_1_EMA26 = screen_1.indicators.get(EMA26);
        screen_1_EMA26 = screen_1_EMA26.subList(screen_1_EMA26.size() - screen_1_MinBarCount, screen_1_EMA26.size());

        List<MACD> screen_1_MACD = screen_1.indicators.get(MACD);
        screen_1_MACD = screen_1_MACD.subList(screen_1_MACD.size() - screen_1_MinBarCount, screen_1_MACD.size());

        List<EMA> screen_2_EMA13 = screen_2.indicators.get(Indicator.EMA13);
        screen_2_EMA13 = screen_2_EMA13.subList(screen_2_EMA13.size() - screen_2_MinBarCount, screen_2_EMA13.size());

        List<MACD> screen_2_MACD = screen_2.indicators.get(Indicator.MACD);
        screen_2_MACD = screen_2_MACD.subList(screen_2_MACD.size() - screen_2_MinBarCount, screen_2_MACD.size());

        List<Stoch> screen_2_Stochastic = screen_2.indicators.get(Indicator.STOCH);
        screen_2_Stochastic = screen_2_Stochastic.subList(screen_2_Stochastic.size() - screen_2_MinBarCount, screen_2_Stochastic.size());

        List<Keltner> screen_2_Keltner = screen_2.indicators.get(KELTNER);
        screen_2_Keltner = screen_2_Keltner.subList(screen_2_Keltner.size() - screen_2_MinBarCount, screen_2_Keltner.size());

        // первый экран

//        // проверка тренда
//        boolean uptrendCheckOnMultipleBars = TrendFunctions.uptrendCheckOnMultipleBars(screen_1, screen_1_MinBarCount, NUMBER_OF_EMA26_VALUES_TO_CHECK);
//        //boolean uptrendCheckOnLastBar = TrendFunctions.uptrendCheckOnLastBar(screen_1); плохая проверка
//        Quote lastChartQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);
//        if (!uptrendCheckOnMultipleBars) {
//            Log.recordCode(NO_UPTREND, screen_1);
//            Log.addDebugLine("Не обнаружен восходящий тренд на долгосрочном экране");
//            return new TaskResult(lastChartQuote, NO_UPTREND);
//        }
//
//        // На первом экране последние 4 Quote.low не должны понижаться
//        // (эта проверка уже есть в Functions.isUptrend, но пусть тут тоже будет)
//        Quote q4 = screen_1_Quotes.get(screen_1_MinBarCount - 4);
//        Quote q3 = screen_1_Quotes.get(screen_1_MinBarCount - 3);
//        Quote q2 = screen_1_Quotes.get(screen_1_MinBarCount - 2);
//        Quote q1 = screen_1_Quotes.get(screen_1_MinBarCount - 1);
//        if (q4.getLow() >= q3.getLow() && q3.getLow() >= q2.getLow() && q2.getLow() >= q1.getLow()) {
//            // допустимо только, если последний столбик зеленый
//            if (q1.getClose() < q1.getOpen()) {
//                Log.recordCode(UPTREND_FAILING, screen_1);
//                Log.addDebugLine("Последние 4 столбика на первом экране понижаются");
//                return new TaskResult(lastChartQuote, UPTREND_FAILING);
//            } else {
//                Log.addDebugLine("Последние 4 столбика на первом экране понижаются, но крайний правый закрылся выше открытия");
//            }
//        }

        // второй экран

        Quote lastChartQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);

        // перепроданность ниже 20 у трех значений медленной линии стохастика и она повышается
        Stoch stoch3 = screen_2_Stochastic.get(screen_2_MinBarCount - 3);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_MinBarCount - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_MinBarCount - 1);
        boolean oversold = stoch3.getSlowD() < 20 && stoch2.getSlowD() < 20 && stoch1.getSlowD() < 20;
        if (!oversold) {
            Log.recordCode(STOCH_WAS_NOT_OVERSOLD_RECENTLY, screen_2);
            Log.addDebugLine("Три последних значения %D стохастика не ниже 20");
            return new BlockResult(lastChartQuote, STOCH_WAS_NOT_OVERSOLD_RECENTLY);
        }
        boolean stochAscending = stoch3.getSlowD() < stoch2.getSlowD() && stoch2.getSlowD() < stoch1.getSlowD();
        if (!stochAscending) {
            Log.recordCode(STOCH_NOT_ASCENDING, screen_2);
            Log.addDebugLine("Три последних значения %D стохастика не повышаются");
            return new BlockResult(lastChartQuote, STOCH_NOT_ASCENDING);
        }

        // последние три столбика гистограммы повышаются
        MACD macd3 = screen_2_MACD.get(screen_2_MinBarCount - 3);
        MACD macd2 = screen_2_MACD.get(screen_2_MinBarCount - 2);
        MACD macd1 = screen_2_MACD.get(screen_2_MinBarCount - 1);
        boolean histogramAscending = macd3.getHistogram() < macd2.getHistogram() && macd2.getHistogram() < macd1.getHistogram();
        if (!histogramAscending) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING, screen_2);
            Log.addDebugLine("Три последних столбика гистограммы MACD не повышаются");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_ASCENDING);
        }

        // два из трех последних баров зеленые
        Quote quote2 = screen_2_Quotes.get(screen_2_MinBarCount - 2);
        Quote quote1 = screen_2_Quotes.get(screen_2_MinBarCount - 1);
        boolean quote2Green = quote2.getClose() > quote2.getOpen();
        boolean quote1Green = quote1.getClose() > quote1.getOpen();
        if (!quote2Green || !quote1Green) {
            Log.recordCode(QUOTE_NOT_GREEN, screen_2);
            Log.addDebugLine("Одна или две последних котировки не зеленые");
            return new BlockResult(lastChartQuote, QUOTE_NOT_GREEN);
        }

        // последние два бара повышаются (quote.low & quote.high)
        boolean lowAndHightAscending = quote2.getLow() < quote1.getLow() && quote2.getHigh() < quote1.getHigh();
        if (!lowAndHightAscending) {
            Log.recordCode(LAST_QUOTES_NOT_ASCENDING, screen_2);
            Log.addDebugLine("Последние две котировки не растут последовательно");
            return new BlockResult(lastChartQuote, LAST_QUOTES_NOT_ASCENDING);
        }

        // последние два бара полностью ниже ЕМА13
        EMA ema13_2 = screen_2_EMA13.get(screen_2_MinBarCount - 2);
        EMA ema13_1 = screen_2_EMA13.get(screen_2_MinBarCount - 1);
        boolean lastQuotesBelowEMA = quote2.getHigh() < ema13_2.getValue() && quote1.getHigh() < ema13_1.getValue();
        if (!lastQuotesBelowEMA) {
            Log.recordCode(QUOTES_NOT_BELOW_EMA, screen_2);
            Log.addDebugLine("Последние две котировки не ниже EMA13");
            return new BlockResult(lastChartQuote, QUOTES_NOT_BELOW_EMA);
        }

        return new BlockResult(lastChartQuote, OK);
    }

    // модификация buySignalType2 с попыткой остлеживания начала долгосрочного тренда
    // 1 экран: отслеживание начала движения выше ЕМА по двум столбикам
    //   последний столбик зеленый
    //   последний столбик выше предпоследнего
    //   последний столбик пересекает ЕМА26
    //   последняя гистограмма растет
    // 2 экран: смотреть на два последних столбика
    //   high последнего столбика выше предпоследнего
    //   последний столбик не выше ЕМА13
    //   последняя гистограмма растет
    //   %D и %K последнего стохастика должны быть выше, чем у предпоследнего
    // ВНИМАНИЕ:
    // после сигнала проверить вручную, чтобы на втором экране послединй столбик не поднимался слишком высоко от ЕМА13
    public static BlockResult buySignalType4(SymbolData screen_1, SymbolData screen_2) {

        int screen_1_MinBarCount = resolveMinBarsCount(screen_1.timeframe);
        int screen_2_MinBarCount = resolveMinBarsCount(screen_2.timeframe);

        if (screen_1.quotes.isEmpty() || screen_1.quotes.size() < screen_1_MinBarCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_1.timeframe.name());
            Log.recordCode(NO_DATA_QUOTES, screen_1);
            return new BlockResult(null, NO_DATA_QUOTES);
        }
        if (screen_2.quotes.isEmpty() || screen_2.quotes.size() < screen_2_MinBarCount) {
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_2.timeframe.name());
            Log.recordCode(NO_DATA_QUOTES, screen_2);
            return new BlockResult(null, NO_DATA_QUOTES);
        }

        List<Indicator> missingData = new ArrayList<>();
        screen_1.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < screen_1_MinBarCount) {
                missingData.add(indicator);
            }
        });
        screen_2.indicators.forEach((indicator, value) -> {
            if (value.isEmpty() || value.size() < screen_2_MinBarCount) {
                missingData.add(indicator);
            }
        });
        if (!missingData.isEmpty()) {
            Log.recordCode(NO_DATA_INDICATORS, screen_1);
            Log.recordCode(NO_DATA_INDICATORS, screen_2);
            Log.addDebugLine("Нету данных по индикаторам: " + missingData.stream().map(indicator -> indicator.name()).collect(Collectors.joining(", ")));
            return new BlockResult(null, NO_DATA_INDICATORS);
        }

        List<Quote> screen_1_Quotes = screen_1.quotes.subList(screen_1.quotes.size() - screen_1_MinBarCount, screen_1.quotes.size());
        List<Quote> screen_2_Quotes = screen_2.quotes.subList(screen_2.quotes.size() - screen_2_MinBarCount, screen_2.quotes.size());

        List<EMA> screen_1_EMA26 = screen_1.indicators.get(EMA26);
        screen_1_EMA26 = screen_1_EMA26.subList(screen_1_EMA26.size() - screen_1_MinBarCount, screen_1_EMA26.size());

        List<MACD> screen_1_MACD = screen_1.indicators.get(MACD);
        screen_1_MACD = screen_1_MACD.subList(screen_1_MACD.size() - screen_1_MinBarCount, screen_1_MACD.size());

        List<EMA> screen_2_EMA13 = screen_2.indicators.get(Indicator.EMA13);
        screen_2_EMA13 = screen_2_EMA13.subList(screen_2_EMA13.size() - screen_2_MinBarCount, screen_2_EMA13.size());

        List<MACD> screen_2_MACD = screen_2.indicators.get(Indicator.MACD);
        screen_2_MACD = screen_2_MACD.subList(screen_2_MACD.size() - screen_2_MinBarCount, screen_2_MACD.size());

        List<Stoch> screen_2_Stochastic = screen_2.indicators.get(Indicator.STOCH);
        screen_2_Stochastic = screen_2_Stochastic.subList(screen_2_Stochastic.size() - screen_2_MinBarCount, screen_2_Stochastic.size());

        List<Keltner> screen_2_Keltner = screen_2.indicators.get(KELTNER);
        screen_2_Keltner = screen_2_Keltner.subList(screen_2_Keltner.size() - screen_2_MinBarCount, screen_2_Keltner.size());

        // первый экран
        Quote screen_1_lastQuote = screen_1_Quotes.get(screen_1_Quotes.size() - 1);
        Quote screen_1_preLastQuote = screen_1_Quotes.get(screen_1_Quotes.size() - 2);
        Quote lastChartQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);

        // последний столбик зеленый
        boolean lastBarIsGreen = screen_1_lastQuote.getOpen() < screen_1_lastQuote.getClose();
        if (!lastBarIsGreen) {
            Log.recordCode(LAST_QUOTE_NOT_GREEN_SCREEN_1, screen_1);
            Log.addDebugLine("Последний столбик не зеленый на долгосрочном экране");
            return new BlockResult(lastChartQuote, LAST_QUOTE_NOT_GREEN_SCREEN_1);
        }

        // последний столбик выше предпоследнего
        boolean lastBarHigher = screen_1_lastQuote.getLow() > screen_1_preLastQuote.getLow()
                && screen_1_lastQuote.getHigh() > screen_1_preLastQuote.getHigh();

        if (!lastBarHigher) {
            Log.recordCode(LAST_QUOTES_NOT_ASCENDING, screen_1);
            Log.addDebugLine("Последний столбик не выше предпоследнего на долгосрочном экране");
            return new BlockResult(lastChartQuote, LAST_QUOTES_NOT_ASCENDING);
        }

        // последний столбик пересекает ЕМА
        if (!isQuoteCrossedEMA(screen_1_lastQuote, screen_1_EMA26.get(screen_1_EMA26.size() - 1).getValue())) {
            Log.recordCode(LAST_QUOTE_NOT_CROSSING_EMA, screen_1);
            Log.addDebugLine("Последний столбик не пересекает ЕМА на долгосрочном экране");
            return new BlockResult(lastChartQuote, LAST_QUOTE_NOT_CROSSING_EMA);
        }

        // последняя гистограмма растет
        com.kishlaly.ta.model.indicators.MACD screen_1_lastMACD = screen_1_MACD.get(screen_1_MACD.size() - 1);
        com.kishlaly.ta.model.indicators.MACD screen_1_preLastMACD = screen_1_MACD.get(screen_1_MACD.size() - 2);
        boolean check2 = screen_1_lastMACD.getHistogram() > screen_1_preLastMACD.getHistogram();
        if (!check2) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING, screen_1);
            Log.addDebugLine("Гистограмма не растет на долгосрочном экране");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_ASCENDING);
        }

        // второй экран

        Quote screen_2_lastQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);
        Quote screen_2_preLastQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 2);

        // high последнего столбика выше предпоследнего
        boolean screen_2_check1 = screen_2_lastQuote.getHigh() > screen_2_preLastQuote.getHigh();
        if (!screen_2_check1) {
            Log.recordCode(LAST_QUOTES_NOT_ASCENDING, screen_1);
            Log.addDebugLine("High последнего столбика не выше предпоследнего на втором экране");
            return new BlockResult(lastChartQuote, LAST_QUOTES_NOT_ASCENDING);
        }

        // последний столбик не выше ЕМА13
        if (isQuoteAboveEMA(screen_2_lastQuote, screen_2_EMA13.get(screen_2_EMA13.size() - 1).getValue())) {
            Log.recordCode(LAST_QUOTE_ABOVE_EMA_SCREEN_2, screen_1);
            Log.addDebugLine("Последний столбик выше EMA на втором экране");
            return new BlockResult(lastChartQuote, LAST_QUOTE_ABOVE_EMA_SCREEN_2);
        }

        // последняя гистограмма растет
        com.kishlaly.ta.model.indicators.MACD screen_2_lastMACD = screen_2_MACD.get(screen_2_MACD.size() - 1);
        com.kishlaly.ta.model.indicators.MACD screen_2_preLastMACD = screen_2_MACD.get(screen_2_MACD.size() - 2);
        boolean screen_2_check2 = screen_2_lastMACD.getHistogram() > screen_2_preLastMACD.getHistogram();
        if (!screen_2_check2) {
            Log.recordCode(HISTOGRAM_NOT_ASCENDING, screen_1);
            Log.addDebugLine("Гистограмма не растет на втором экране");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_ASCENDING);
        }

        // %D и %K последнего стохастика должны быть выше, чем у предпоследнего
        Stoch screen_2_lastStoch = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);
        Stoch screen_2_preLastStoch = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        boolean screen_2_check3 = screen_2_lastStoch.getSlowK() > screen_2_preLastStoch.getSlowK()
                && screen_2_lastStoch.getSlowD() > screen_2_preLastStoch.getSlowD();
        if (!screen_2_check3) {
            Log.recordCode(STOCH_NOT_ASCENDING, screen_1);
            Log.addDebugLine("Стохастик не растет на втором экране");
            return new BlockResult(lastChartQuote, STOCH_NOT_ASCENDING);
        }

        if (FILTER_BY_KELTNER_ENABLED) {
            Keltner lastKeltnerData = screen_2_Keltner.get(screen_2_MinBarCount - 1);
            double lastQuoteClose = lastChartQuote.getClose();
            double middle = lastKeltnerData.getMiddle();
            double top = lastKeltnerData.getTop();
            double diff = top - middle;
            double ratio = diff / 100 * FILTER_BY_KELTNER;
            double maxAllowedCloseValue = middle + ratio;
            if (lastQuoteClose >= maxAllowedCloseValue) {
                Log.addDebugLine("Последняя котировка закрылась выше " + FILTER_BY_KELTNER + "% расстояния от середины до вершины канала");
                Log.recordCode(QUOTE_CLOSED_ABOVE_KELTNER_RULE, screen_2);
                return new BlockResult(lastChartQuote, QUOTE_CLOSED_ABOVE_KELTNER_RULE);
            }
        }

        return new BlockResult(lastChartQuote, OK);
    }

    public static BlockResult sellSignal(SymbolData screen_1, SymbolData screen_2) {

        int screen_1_MinBarCount = resolveMinBarsCount(screen_1.timeframe);
        int screen_2_MinBarCount = resolveMinBarsCount(screen_2.timeframe);

        if (screen_1.quotes.isEmpty() || screen_1.quotes.size() < screen_1_MinBarCount) {
            Log.recordCode(NO_DATA_QUOTES, screen_1);
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_1.timeframe.name());
            return new BlockResult(null, NO_DATA_QUOTES);
        }
        if (screen_2.quotes.isEmpty() || screen_2.quotes.size() < screen_2_MinBarCount) {
            Log.recordCode(NO_DATA_QUOTES, screen_2);
            Log.addDebugLine("Недостаточно ценовых столбиков для " + screen_2.timeframe.name());
            return new BlockResult(null, NO_DATA_QUOTES);
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
            return new BlockResult(null, NO_DATA_INDICATORS);
        }

        List<Quote> screen_1_Quotes = screen_1.quotes.subList(screen_1.quotes.size() - screen_1_MinBarCount, screen_1.quotes.size());
        List<Quote> screen_2_Quotes = screen_2.quotes.subList(screen_2.quotes.size() - screen_2_MinBarCount, screen_2.quotes.size());

        List<EMA> screen_1_EMA26 = screen_1.indicators.get(EMA26);
        screen_1_EMA26 = screen_1_EMA26.subList(screen_1_EMA26.size() - screen_1_MinBarCount, screen_1_EMA26.size());

        List<MACD> screen_1_MACD = screen_1.indicators.get(MACD);
        screen_1_MACD = screen_1_MACD.subList(screen_1_MACD.size() - screen_1_MinBarCount, screen_1_MACD.size());

        List<EMA> screen_2_EMA13 = screen_2.indicators.get(EMA13);
        screen_2_EMA13 = screen_2_EMA13.subList(screen_2_EMA13.size() - screen_2_MinBarCount, screen_2_EMA13.size());

        List<MACD> screen_2_MACD = screen_2.indicators.get(MACD);
        screen_2_MACD = screen_2_MACD.subList(screen_2_MACD.size() - screen_2_MinBarCount, screen_2_MACD.size());

        List<Stoch> screen_2_Stochastic = screen_2.indicators.get(STOCH);
        screen_2_Stochastic = screen_2_Stochastic.subList(screen_2_Stochastic.size() - screen_2_MinBarCount, screen_2_Stochastic.size());

        // первый экран

        // проверка тренда
        boolean downtrendCheckOnMultipleBars = TrendFunctions.downtrendCheckOnMultipleBars(screen_1, screen_1_MinBarCount, NUMBER_OF_EMA26_VALUES_TO_CHECK);
        //boolean downtrendCheckOnLastBar = TrendFunctions.downtrendCheckOnLastBar(screen_1); опасно
        Quote lastChartQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);
        if (!downtrendCheckOnMultipleBars) {
            Log.recordCode(NO_DOWNTREND, screen_1);
            Log.addDebugLine("Не обнаружен нисходящий тренд на долгосрочном экране");
            return new BlockResult(lastChartQuote, NO_DOWNTREND);
        }

        // На первом экране последние 4 Quote.high не должны повышаться
        // (эта проверка уже есть в Functions.isDownrend, но пусть тут тоже будет)
        Quote q4 = screen_1_Quotes.get(screen_1_MinBarCount - 4);
        Quote q3 = screen_1_Quotes.get(screen_1_MinBarCount - 3);
        Quote q2 = screen_1_Quotes.get(screen_1_MinBarCount - 2);
        Quote q1 = screen_1_Quotes.get(screen_1_MinBarCount - 1);
        if (q4.getHigh() <= q3.getHigh() && q3.getHigh() <= q2.getHigh() && q2.getHigh() <= q1.getHigh()) {
            // допустимо только, если последний столбик красный
            if (q1.getClose() > q1.getOpen()) {
                Log.recordCode(DOWNTREND_FAILING, screen_1);
                Log.addDebugLine("Последние 4 столбика на первом экране повышаются");
                return new BlockResult(lastChartQuote, DOWNTREND_FAILING);
            } else {
                Log.addDebugLine("Последние 4 столбика на первом экране повышаются, но крайний правый закрылся ниже открытия");
            }
        }

        // второй экран

        // гистограмма должна быть выше нуля и начать снижаться: проверить на трех последних значениях

        Double macd3 = screen_2_MACD.get(screen_2_MACD.size() - 3).getHistogram(); // 3 от правого края
        Double macd2 = screen_2_MACD.get(screen_2_MACD.size() - 2).getHistogram(); // 2 от правого края
        Double macd1 = screen_2_MACD.get(screen_2_MACD.size() - 1).getHistogram(); // последняя

        boolean histogramAboveZero = macd3 > 0 && macd2 > 0 && macd1 > 0;
        if (!histogramAboveZero) {
            Log.recordCode(HISTOGRAM_NOT_ABOVE_ZERO, screen_2);
            Log.addDebugLine("Гистограмма на втором экране не выше нуля");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_ABOVE_ZERO);
        }

        boolean ascendingHistogram = macd3 > macd2 && macd2 > macd1;
        if (!ascendingHistogram) {
            Log.recordCode(HISTOGRAM_NOT_DESCENDING, screen_2);
            Log.addDebugLine("Гистограмма на втором экране не снижается");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_DESCENDING);
        }

        // стохастик должен снижаться из зоны перекупленности: проверить на трех последних значениях

        Stoch stoch3 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 3);
        Stoch stoch2 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        Stoch stoch1 = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);
        // %D снижается (достаточно, чтобы последний был ниже прошлых двух)
        boolean ascendingStochastic = stoch1.getSlowD() < stoch2.getSlowD() && stoch1.getSlowD() < stoch3.getSlowD();
        if (!ascendingStochastic) {
            Log.recordCode(STOCH_NOT_DESCENDING, screen_2);
            Log.addDebugLine("Стохастик %D не снижается на втором экране");
            return new BlockResult(lastChartQuote, STOCH_NOT_DESCENDING);
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
            Log.recordCode(STOCH_NOT_DESCENDING_FROM_OVERBOUGHT, screen_2);
            Log.addDebugLine("Стохастик не снижается из перекупленности " + STOCH_OVERBOUGHT + ". %D: " + isOverboughtD + "; %K: " + isOverboughtK);
            return new BlockResult(lastChartQuote, STOCH_NOT_DESCENDING_FROM_OVERBOUGHT);
        }

        // ценовые бары должны пересекать ЕМА13 и должны снижаться

        // убедиться сначала, что low у последних трех столбиков снижается
        Quote quote3 = screen_2_Quotes.get(screen_2_MinBarCount - 3);
        Quote quote2 = screen_2_Quotes.get(screen_2_MinBarCount - 2);
        Quote quote1 = screen_2_Quotes.get(screen_2_MinBarCount - 1);
        // наверно descendingBarLow=false + descendingBarClose=false достаточно для отказа
        boolean descendingBarLow = quote3.getLow() > quote2.getLow() && quote2.getLow() < quote1.getLow();
        boolean descendingBarClose = quote3.getClose() > quote2.getClose() && quote2.getClose() > quote1.getClose();

        if (!descendingBarLow) {
            Log.recordCode(BlockResultCode.QUOTE_LOW_NOT_LOWING, screen_2);
            Log.addDebugLine("Quote.low не снижается последовательно");
            if (!descendingBarClose) {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_NOT_LOWING, screen_2);
                Log.addDebugLine("Quote.close не снижается последовательно");
                // третий с конца весь выше ЕМА13, а второй и последний пересекли ее
                boolean thirdBarAboveEMA13 = quote3.getLow() > screen_2_EMA13.get(screen_2_MinBarCount - 3).getValue()
                        && quote3.getHigh() > screen_2_EMA13.get(screen_2_MinBarCount - 3).getValue();
                boolean secondBarCrossesEMA13 = quote2.getLow() <= screen_2_EMA13.get(screen_2_MinBarCount - 2).getValue()
                        && quote2.getHigh() >= screen_2_EMA13.get(screen_2_MinBarCount - 2).getValue();
                boolean lastBarCrossesEMA13 = quote1.getLow() <= screen_2_EMA13.get(screen_2_MinBarCount - 1).getValue()
                        && quote1.getHigh() >= screen_2_EMA13.get(screen_2_MinBarCount - 1).getValue();
                boolean crossingRule = thirdBarAboveEMA13 && (secondBarCrossesEMA13 || lastBarCrossesEMA13);
                if (!crossingRule) {
                    Log.addDebugLine("Третий с конца" + (thirdBarAboveEMA13 ? " " : " не ") + "выше ЕМА13");
                    Log.addDebugLine("Предпоследний" + (secondBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.addDebugLine("Последний" + (lastBarCrossesEMA13 ? " " : " не ") + "пересекает ЕМА13");
                    Log.recordCode(CROSSING_RULE_VIOLATED, screen_2);
                    return new BlockResult(lastChartQuote, CROSSING_RULE_VIOLATED);
                } else {
                    Log.recordCode(BlockResultCode.CROSSING_RULE_PASSED, screen_2);
                    Log.addDebugLine("Правило пересечения выполняется");
                }
            } else {
                Log.recordCode(BlockResultCode.QUOTE_CLOSE_LOWING, screen_2);
                Log.addDebugLine("Есть снижение Quote.close");
            }
        } else {
            Log.recordCode(BlockResultCode.QUOTE_HIGH_LOWING, screen_2);
            Log.addDebugLine("Есть снижение Quote.high");
        }

        // нужно фильтровать ситуацию, когда третий и второй пересекают ЕМА13, а последний целиком ниже (то есть уже момент потерян)
        // третий может открыться и закрыться ниже, и это допустимо
        boolean thirdCrossesEMA13 = quote3.getLow() < screen_2_EMA13.get(screen_2_MinBarCount - 3).getValue()
                && quote3.getHigh() > screen_2_EMA13.get(screen_2_MinBarCount - 3).getValue();
        boolean secondCrossesEMA13 = quote2.getLow() < screen_2_EMA13.get(screen_2_MinBarCount - 2).getValue()
                && quote2.getHigh() > screen_2_EMA13.get(screen_2_MinBarCount - 2).getValue();
        boolean lastBelowEMA13 = quote1.getLow() < screen_2_EMA13.get(screen_2_MinBarCount - 1).getValue()
                && quote1.getHigh() < screen_2_EMA13.get(screen_2_MinBarCount - 1).getValue();
        if (thirdCrossesEMA13 && secondCrossesEMA13 && lastBelowEMA13) {
            Log.recordCode(LAST_BAR_BELOW, screen_2);
            Log.addDebugLine("Третий и второй пересекли ЕМА13, а последний полностью ниже");
            return new BlockResult(lastChartQuote, LAST_BAR_BELOW);
        }

        // фильтровать ситуации, когда последний столбик имеет тень ниже, чем третий с конца
        // это не обязательно
//        if (quote1.getLow() < quote2.getLow() && quote1.getLow() < quote3.getLow()) {
//            Log.addDebugLine("Последний столбик имеет тень ниже предыдуших двух");
//            return false;
//        }

        //попробовать посчитать среднюю длину баров и сравнить с ней последние три
        Double sum = screen_2_Quotes.stream().map(quote -> quote.getHigh() - quote.getLow()).reduce(Double::sum).get();
        double averageBarLength = sum / screen_2_MinBarCount;
        double quote1Length = quote1.getHigh() - quote1.getLow();
        double quote2Length = quote2.getHigh() - quote2.getLow();
        double quote3Length = quote3.getHigh() - quote3.getLow();
        boolean quote1StrangeLength = quote1Length >= averageBarLength * multiplier;
        boolean quote2StrangeLength = quote2Length >= averageBarLength * multiplier;
        boolean quote3StrangeLength = quote3Length >= averageBarLength * multiplier;
        if (quote1StrangeLength || quote2StrangeLength || quote3StrangeLength) {
            Log.addDebugLine("Внимание: один из последних трех столбиков в " + multiplier + " раза больше среднего");
        }

        return new BlockResult(quote1, OK);
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
