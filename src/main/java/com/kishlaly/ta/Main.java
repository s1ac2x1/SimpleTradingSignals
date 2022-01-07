package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskTester.test;
import static com.kishlaly.ta.analyze.TaskType.*;
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

        Context.source = "symbols/sp500.txt";
        //Context.source = "symbols/from_screener.txt";
        Context.singleSymbol = "SCHW";

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

        //Context.day = "2021-11-12";
        test(timeframes, tasks);

        // если усилить проверку тренда на первом экране требованием, чтобы последние N quote.low не снижались/поднимались последовательно?
        // потому что часто сигналы на втором экране в точке, за которой следует легкий разворот (опасность срабатывания столплоса)
        // как было у SCHW 12 NOVEMBER 2021

        // проверить стратегию:
        // на первом экране
        //    1) последная котировка открывается ниже и закрывается выше ЕМА26
        //    2) последние две гистограммы MACD растут
        // на втором экране все проверки для второго экрана из ThreeDisplays

        // [D] INFO 4 Mar 2021
        // BK https://drive.google.com/file/d/14PlpZMZV7lwsIwP2V7bww0LKSVjdn70Q/view?usp=sharing и https://drive.google.com/file/d/1-a0ZtMuLQyuamez_402v6YkViNWzY6RS/view?usp=sharing

        // добавить в дивергенции расчет EFI, тогда, может быть, не придется фильтровать по SECOND_BOTTOM_RATIO ?
        // если EFI покажет правильную дивергенцию, которая подтверждает сигналы МАСD, то стоит обратить на это внимание

    }

}