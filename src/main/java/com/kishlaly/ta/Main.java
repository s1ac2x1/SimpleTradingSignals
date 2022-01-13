package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.TakeProfitStrategy;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY_TYPE2;
import static com.kishlaly.ta.analyze.testing.TaskTester.test;
import static com.kishlaly.ta.cache.CacheBuilder.buildCache;
import static com.kishlaly.ta.cache.CacheReader.checkCache;
import static com.kishlaly.ta.utils.Context.ApiSource.ALPHAVANTAGE;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Context.api = ALPHAVANTAGE;
        Context.aggregationTimeframe = Timeframe.DAY;

        Context.source = "symbols/sp500.txt";
        //Context.source = "symbols/from_screener.txt";
        Context.singleSymbol = "CSCO";

        Timeframe[][] timeframes = {
                {Timeframe.WEEK, Timeframe.DAY},
                //{Timeframe.DAY, Timeframe.HOUR},
        };

        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                THREE_DISPLAYS_BUY_TYPE2,
                //THREE_DISPLAYS_SELL
        };

        //buildCache(timeframes, tasks, false);
        //checkCache(timeframes, tasks);

        //run(timeframes, tasks);

        try {
            Context.stopLossStrategy = StopLossStrategy.FIXED;
            Context.takeProfitStrategy = TakeProfitStrategy.KELTNER;
            test(timeframes, tasks);
        } catch (Exception e) {
            System.out.println(e);
        }

        // в statistics summary сверху добавить информацию про стратегию открытия позиций

        // сделать агрегацию часовых котировок

        // точно ли нужно требовать наличия минимум 100 баров, даже если они недельные? если акции меньше двух лет?

        // проверить buy стратегию (вдохновитель [D] CFLT 20 Dec 2021)
        // первый экран - подумать
        // второй экран -
        //    перепроданность ниже 20 у трех значений медленной линии стохастика и она повышается
        //    последние три столбика гистограммы повышаются
        //    два из трех последних баров зеленые
        //    последние два бара повышаются (quote.low & quote.high)
        //    последние два бара полностью ниже ЕМА13
        // вход на 7 центов выше закрытия последнего бара
        // TP на середине верхней половины канала Кельтнера

        // [D] INFO 4 Mar 2021
        // BK https://drive.google.com/file/d/14PlpZMZV7lwsIwP2V7bww0LKSVjdn70Q/view?usp=sharing и https://drive.google.com/file/d/1-a0ZtMuLQyuamez_402v6YkViNWzY6RS/view?usp=sharing

        // когда будет готова система тестирования на исторических данных со статистикой
        // попробовать разные значения индикаторов, например, ЕМА 14 на втором экране
        // а так же тестировать точки входа и выхода, например, 75% от верхней границы канала
        // и проверить скользящий стоплосс, например, по середней линии канала
        // проверить стоплосс на уровне нижней границы канала в точке сигнала

        // добавить в дивергенции расчет EFI, тогда, может быть, не придется фильтровать по SECOND_BOTTOM_RATIO ?
        // если EFI покажет правильную дивергенцию, которая подтверждает сигналы МАСD, то стоит обратить на это внимание

    }

}