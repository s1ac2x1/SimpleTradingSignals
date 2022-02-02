package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.TakeProfitKeltner;
import com.kishlaly.ta.analyze.testing.TakeProfitStrategy;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.MACD_BULLISH_DIVERGENCE;
import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY_TYPE2;
import static com.kishlaly.ta.analyze.testing.TaskTester.test;
import static com.kishlaly.ta.cache.CacheBuilder.buildCache;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class Main {

    public static void main(String[] args) throws Exception {

//        Context.aggregationTimeframe = Timeframe.DAY;
        Context.aggregationTimeframe = Timeframe.HOUR;

//        Context.source = "symbols/sp500.txt";
        Context.source = "symbols/screener_2.txt";
//        Context.testOnly = new ArrayList<String>() {{
//            add("AEP");
//        }};

        Timeframe[][] timeframes = {
//                {Timeframe.WEEK, Timeframe.DAY},
                {Timeframe.DAY, Timeframe.HOUR},
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

        try {
            run(timeframes, tasks);
        } catch (Exception e) {
            System.out.println(e);
        }

//        try {
//            StopLossStrategy stopLossStrategy = StopLossStrategy.FIXED;
//            Context.stopLossStrategy = stopLossStrategy;
//
//            TakeProfitStrategy takeProfitStrategy = TakeProfitStrategy.KELTNER;
//            takeProfitStrategy.setConfig(80);
//            Context.takeProfitStrategy = takeProfitStrategy;
//
//            test(timeframes, tasks);
//        } catch (Exception e) {
//            System.out.println(e);
//        }

//        try {
//            StopLossStrategy stopLossStrategy = StopLossStrategy.FIXED;
//            Context.stopLossStrategy = stopLossStrategy;
//
//            Context.massTesting = true;
//            Context.takeProfitStrategies = new ArrayList<>();
//            for (int i = 80; i <= 100; i++) {
//                TakeProfitStrategy tp = new TakeProfitKeltner(i);
//                Context.takeProfitStrategies.add(tp);
//            }
//            test(timeframes, tasks);
//        } catch (Exception e) {
//            System.out.println(e);
//        }

    }

}