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

    private static BlockResult check(Screens screens, List<TaskBlock> blocks) {

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

    public static BlockResult buy(Screens screens, List<TaskBlock> blocks) {
        return check(screens, blocks);
    }

    public static BlockResult sell(Screens screens, List<TaskBlock> blocks) {
        return check(screens, blocks);

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
