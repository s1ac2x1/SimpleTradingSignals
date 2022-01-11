package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import static com.kishlaly.ta.analyze.TaskTester.test;
import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.cache.CacheBuilder.buildCache;
import static com.kishlaly.ta.utils.Context.ApiSource.ALPHAVANTAGE;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Context.api = ALPHAVANTAGE;

        Context.source = "symbols/sp500.txt";
        //Context.source = "symbols/from_screener.txt";
        Context.singleSymbol = "TER";

        Timeframe[][] timeframes = {
                {Timeframe.WEEK, Timeframe.DAY},
                //{Timeframe.DAY, Timeframe.HOUR},
        };

        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                THREE_DISPLAYS_BUY,
                //THREE_DISPLAYS_SELL
        };

        //buildCache(timeframes, tasks, false);
        //checkCache(timeframes, tasks);

        //run(timeframes, tasks);

        //Context.screenOneDay = "2020-09";
        //Context.screenTwoDay = "2021-11-15";
        test(timeframes, tasks);

        // [D] TER - 24 JUNE 2021 --- PROFIT 3.3% 5 days [till 29 JUNE 2021] почему не 23???

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
        // может быть, достаточно проверять последние два значения бара/гистограммы/стохастика на втором экране?

        // добавить в дивергенции расчет EFI, тогда, может быть, не придется фильтровать по SECOND_BOTTOM_RATIO ?
        // если EFI покажет правильную дивергенцию, которая подтверждает сигналы МАСD, то стоит обратить на это внимание

    }

}