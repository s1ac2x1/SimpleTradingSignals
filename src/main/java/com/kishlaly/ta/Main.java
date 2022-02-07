package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitKeltner;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.FIRST_TRUST_MODEL;
import static com.kishlaly.ta.analyze.testing.TaskTester.test;

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
//        Context.testOnly = new ArrayList<String>() {{
//            add("PYPL");
//        }};


        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                //THREE_DISPLAYS_BUY, // лучше работает для DAY-HOUR
                //THREE_DISPLAYS_SELL,
                //THREE_DISPLAYS_BUY_TYPE2, // лучше работает для WEEK-DAY
                FIRST_TRUST_MODEL
        };

//        buildCache(timeframes, tasks, false);
//        checkCache(timeframes, tasks);
        run(timeframes, tasks);
//        testPlain(timeframes, tasks);
//        testDynamicTP(timeframes, tasks);

        // добавить стратегию поиска акций с гэпом вниз

    }

    private static void testDynamicTP(Timeframe[][] timeframes, TaskType[] tasks) {
        StopLossStrategy stopLossStrategy = new StopLossFixedPrice(0.27);
        Context.stopLossStrategy = stopLossStrategy;

        Context.massTesting = true;
        Context.takeProfitStrategies = new ArrayList<>();
        for (int i = 80; i <= 100; i++) {
            TakeProfitStrategy tp = new TakeProfitKeltner(i);
            Context.takeProfitStrategies.add(tp);
        }
        test(timeframes, tasks);
    }

    private static void testPlain(Timeframe[][] timeframes, TaskType[] tasks) {
        StopLossStrategy stopLossStrategy = new StopLossFixedPrice(0.27);
        Context.stopLossStrategy = stopLossStrategy;

        TakeProfitStrategy takeProfitStrategy = new TakeProfitKeltner(100);
        Context.takeProfitStrategy = takeProfitStrategy;

        test(timeframes, tasks);
    }

}