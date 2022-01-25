package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.TakeProfitStrategy;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.*;
import static com.kishlaly.ta.analyze.testing.TaskTester.test;
import static com.kishlaly.ta.cache.CacheBuilder.buildCache;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Context.trendCheckIncludeHistogram = false;

        Context.aggregationTimeframe = Timeframe.DAY;
//        Context.aggregationTimeframe = Timeframe.HOUR;

        Context.source = "symbols/sp500.txt";
//        Context.source = "symbols/screener_2.txt";
        Context.singleSymbol = "AAPL";

        Timeframe[][] timeframes = {
                {Timeframe.WEEK, Timeframe.DAY},
//                {Timeframe.DAY, Timeframe.HOUR},
        };

        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                //THREE_DISPLAYS_BUY, // лучше работает для DAY-HOUR
                //THREE_DISPLAYS_SELL,
                THREE_DISPLAYS_BUY_TYPE2, // лучше работает для WEEK-DAY
                //FIRST_TRUST_MODEL,
                //ABC_BUY
        };

//        buildCache(timeframes, tasks, false);
//        checkCache(timeframes, tasks);

//        try {
//            run(timeframes, tasks);
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        try {
            StopLossStrategy stopLossStrategy = StopLossStrategy.FIXED;
            Context.stopLossStrategy = stopLossStrategy;

            TakeProfitStrategy takeProfitStrategy = TakeProfitStrategy.KELTNER;
            takeProfitStrategy.setConfig(80);
            Context.takeProfitStrategy = takeProfitStrategy;

            test(timeframes, tasks);
        } catch (Exception e) {
            System.out.println(e);
        }

        // если не проверять гистограмму при проверке тренда на первом экране?
        // будет ли тогда сигнал у [D] MSFT 5 Oct 2021 у THREE_DISPLAYS_BUY_TYPE2 ?

        // что там все-таки за третий экран у Элдера? как его использовать? подозреваю, что пригодятся часовые графики

        // проверить стратегию ABC от Элдера

        // стратегии поиска свечных моделей

        // [D] INFO 4 Mar 2021
        // BK https://drive.google.com/file/d/14PlpZMZV7lwsIwP2V7bww0LKSVjdn70Q/view?usp=sharing и https://drive.google.com/file/d/1-a0ZtMuLQyuamez_402v6YkViNWzY6RS/view?usp=sharing

        // когда будет готова система тестирования на исторических данных со статистикой
        // попробовать разные значения индикаторов, например, ЕМА 14 на втором экране
        // а так же тестировать точки входа и выхода, например, 75% от верхней границы канала
        // и проверить скользящий стоплосс, например, по середней линии канала
        // проверить стоплосс на уровне нижней границы канала в точке сигнала

        // добавить в дивергенции расчет EFI, тогда, может быть, не придется фильтровать по SECOND_BOTTOM_RATIO ?
        // если EFI покажет правильную дивергенцию, которая подтверждает сигналы МАСD, то стоит обратить на это внимание

        // реализовать тестирование шортов

        // реализовать проверку медвежьей дивергенции

    }

}