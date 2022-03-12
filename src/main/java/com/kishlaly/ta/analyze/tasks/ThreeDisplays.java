package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.BlockResultCode;
import com.kishlaly.ta.analyze.functions.TrendFunctions;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneBlock;
import com.kishlaly.ta.analyze.tasks.blocks.two.ScreenTwoBlock;
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

    public static BlockResult buy(Screens screens, List<TaskBlock> blocks) {

        SymbolData screen1 = screens.getScreen1();
        SymbolData screen2 = screens.getScreen2();

        Quotes.trim(screen1);
        Quotes.trim(screen2);
        IndicatorUtils.trim(screen1);
        IndicatorUtils.trim(screen2);

        List<TaskBlock> screenOneBlocks = blocks
                .stream()
                .filter(block -> block instanceof ScreenOneBlock)
                .collect(Collectors.toList());

        boolean screenOneAllBlocksValid = true;
        BlockResult screenOneResult = null;

        for (int i = 0; i < screenOneBlocks.size(); i++) {
            TaskBlock screenOneBlock = screenOneBlocks.get(i);
            screenOneResult = screenOneBlock.check(screen1);
            if (!screenOneResult.isOk()) {
                screenOneAllBlocksValid = false;
                break;
            }
        }
        if (!screenOneAllBlocksValid) {
            return screenOneResult;
        }

        List<TaskBlock> screenTwoBlocks = blocks
                .stream()
                .filter(block -> block instanceof ScreenTwoBlock)
                .collect(Collectors.toList());
        BlockResult screenTwoResult = null;
        for (int i = 0; i < screenTwoBlocks.size(); i++) {
            TaskBlock screenTwoBlock = screenTwoBlocks.get(i);
            screenTwoResult = screenTwoBlock.check(screen2);
            if (!screenTwoResult.isOk()) {
                break;
            }
        }
        return screenTwoResult;
    }

    public static BlockResult buySignalType4(SymbolData screen_1, SymbolData screen_2) {

        // первый экран

        // последний столбик выше предпоследнего
        boolean lastBarHigher = screen_1_lastQuote.getLow() > screen_1_preLastQuote.getLow()
                && screen_1_lastQuote.getHigh() > screen_1_preLastQuote.getHigh();

        if (!lastBarHigher) {
            Log.recordCode(LAST_QUOTES_NOT_ASCENDING_SCREEN_2, screen_1);
            Log.addDebugLine("Последний столбик не выше предпоследнего на долгосрочном экране");
            return new BlockResult(lastChartQuote, LAST_QUOTES_NOT_ASCENDING_SCREEN_2);
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
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen_1);
            Log.addDebugLine("Гистограмма не растет на долгосрочном экране");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }

        // второй экран

        Quote screen_2_lastQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 1);
        Quote screen_2_preLastQuote = screen_2_Quotes.get(screen_2_Quotes.size() - 2);

        // high последнего столбика выше предпоследнего
        boolean screen_2_check1 = screen_2_lastQuote.getHigh() > screen_2_preLastQuote.getHigh();
        if (!screen_2_check1) {
            Log.recordCode(LAST_QUOTES_NOT_ASCENDING_SCREEN_2, screen_1);
            Log.addDebugLine("High последнего столбика не выше предпоследнего на втором экране");
            return new BlockResult(lastChartQuote, LAST_QUOTES_NOT_ASCENDING_SCREEN_2);
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
            Log.recordCode(HISTOGRAM_NOT_ASCENDING_SCREEN_2, screen_1);
            Log.addDebugLine("Гистограмма не растет на втором экране");
            return new BlockResult(lastChartQuote, HISTOGRAM_NOT_ASCENDING_SCREEN_2);
        }

        // %D и %K последнего стохастика должны быть выше, чем у предпоследнего
        Stoch screen_2_lastStoch = screen_2_Stochastic.get(screen_2_Stochastic.size() - 1);
        Stoch screen_2_preLastStoch = screen_2_Stochastic.get(screen_2_Stochastic.size() - 2);
        boolean screen_2_check3 = screen_2_lastStoch.getSlowK() > screen_2_preLastStoch.getSlowK()
                && screen_2_lastStoch.getSlowD() > screen_2_preLastStoch.getSlowD();
        if (!screen_2_check3) {
            Log.recordCode(STOCH_NOT_ASCENDING_SCREEN_2, screen_1);
            Log.addDebugLine("Стохастик не растет на втором экране");
            return new BlockResult(lastChartQuote, STOCH_NOT_ASCENDING_SCREEN_2);
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
                Log.recordCode(QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2, screen_2);
                return new BlockResult(lastChartQuote, QUOTE_CLOSED_ABOVE_KELTNER_RULE_SCREEN_2);
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
                    Log.recordCode(CROSSING_RULE_VIOLATED_SCREEN_2, screen_2);
                    return new BlockResult(lastChartQuote, CROSSING_RULE_VIOLATED_SCREEN_2);
                } else {
                    Log.recordCode(BlockResultCode.CROSSING_RULE_PASSED_SCREEN_2, screen_2);
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

}
