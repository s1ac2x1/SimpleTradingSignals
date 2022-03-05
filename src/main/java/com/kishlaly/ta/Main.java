package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskRunner.runBest;
import static com.kishlaly.ta.analyze.TaskType.*;
import static com.kishlaly.ta.analyze.testing.TaskTester.testOneStrategy;
import static com.kishlaly.ta.cache.CacheBuilder.*;
import static com.kishlaly.ta.cache.CacheReader.getSymbols;

/**
 * @author Vladimir Kishlaly
 * @since 15.11.2021
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Context.aggregationTimeframe = Timeframe.DAY;

        Timeframe[][] timeframes = {
                {Timeframe.WEEK, Timeframe.DAY},
//                {Timeframe.DAY, Timeframe.HOUR},
        };

        Context.source = new SymbolsSource[]{
                //SymbolsSource.SP500,
                //SymbolsSource.NAGA,
                //SymbolsSource.SCREENER_FILTERED
        };
//        Context.testOnly = new ArrayList<String>() {{
//            add("ALSN");
//        }};
        Context.symbols = getSymbols();
        Context.yearsToAnalyze = 5;

        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                THREE_DISPLAYS_BUY, // лучше работает для DAY-HOUR
                //FIRST_TRUST_MODEL, // искать на S&P500
        };

//        buildCache(timeframes, tasks, false);
//        findBestStrategyForSymbols();
//        checkCache(timeframes, tasks);
//        run(timeframes, tasks, false);
        runBest(timeframes);
//        testOneStrategy(timeframes, tasks, new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(100));
//        buildTasksAndStrategiesSummary(timeframes, tasks, new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(100));
//        buildTasksAndStrategiesSummary(timeframes, tasks, null, null);
    }

}