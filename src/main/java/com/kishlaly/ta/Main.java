package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.sl.StopLossVolatileKeltnerBottom;
import com.kishlaly.ta.analyze.testing.sl.StopLossVolatileLocalMin;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitDisabled;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitKeltner;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;

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

        Context.aggregationTimeframe = Timeframe.DAY;
//        Context.aggregationTimeframe = Timeframe.HOUR;

        Timeframe[][] timeframes = {
                {Timeframe.WEEK, Timeframe.DAY},
//                {Timeframe.DAY, Timeframe.HOUR},
        };

//        Context.source = "symbols/sp500.txt";
        Context.source = "symbols/screener_2.txt";
//        Context.source = "symbols/screener_many.txt";
//        Context.source = "symbols/naga.txt";
//        Context.testOnly = new ArrayList<String>() {{
//            add("WDC");
//        }};


        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                //THREE_DISPLAYS_BUY, // лучше работает для DAY-HOUR
                THREE_DISPLAYS_BUY_TYPE2, // лучше работает для WEEK-DAY
                //FIRST_TRUST_MODEL, // искать на S&P500
        };

        buildCache(timeframes, tasks, false);
//        checkCache(timeframes, tasks);
//        run(timeframes, tasks);
//        testFixed(timeframes, tasks);
//        testVolatile(timeframes, tasks);

    }

    private static void testVolatile(Timeframe[][] timeframes, TaskType[] tasks) {
        StopLossStrategy stopLossStrategy = new StopLossVolatileKeltnerBottom();
        Context.stopLossStrategy = stopLossStrategy;

        TakeProfitStrategy takeProfitStrategy = new TakeProfitDisabled();
        Context.takeProfitStrategy = takeProfitStrategy;

        test(timeframes, tasks);
    }

    private static void testFixedMany(Timeframe[][] timeframes, TaskType[] tasks) {
        Context.massTesting = true;

        StopLossStrategy stopLossStrategy = new StopLossFixedPrice(0.27);
        Context.stopLossStrategy = stopLossStrategy;

        Context.takeProfitStrategies = new ArrayList<>();
        for (int i = 80; i <= 100; i++) {
            TakeProfitStrategy tp = new TakeProfitKeltner(i);
            Context.takeProfitStrategies.add(tp);
        }
        test(timeframes, tasks);
    }

    private static void testFixed(Timeframe[][] timeframes, TaskType[] tasks) {
        StopLossStrategy stopLossStrategy = new StopLossVolatileLocalMin(0.27);
        Context.stopLossStrategy = stopLossStrategy;

        TakeProfitStrategy takeProfitStrategy = new TakeProfitKeltner(100);
        Context.takeProfitStrategy = takeProfitStrategy;

        test(timeframes, tasks);
    }

}