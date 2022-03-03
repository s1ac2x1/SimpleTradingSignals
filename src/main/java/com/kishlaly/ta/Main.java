package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import static com.kishlaly.ta.analyze.TaskRunner.runBest;
import static com.kishlaly.ta.cache.CacheBuilder.buildCache;
import static com.kishlaly.ta.cache.CacheBuilder.findBestStrategyForSymbols;
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
                SymbolsSource.SP500,
                SymbolsSource.NAGA,
                SymbolsSource.SCREENER_FILTERED
        };
//        Context.testOnly = new ArrayList<String>() {{
//            add("HSY");
//        }};
        Context.symbols = getSymbols();
        Context.yearsToAnalyze = 5;

        TaskType[] tasks = {
                //MACD_BULLISH_DIVERGENCE,
                //THREE_DISPLAYS_BUY, // лучше работает для DAY-HOUR
                //THREE_DISPLAYS_BUY_TYPE_2, // лучше работает для WEEK-DAY
                //THREE_DISPLAYS_BUY_TYPE_4,
                //FIRST_TRUST_MODEL, // искать на S&P500
        };

//        buildCache(timeframes, tasks, false);
        findBestStrategyForSymbols();
//        checkCache(timeframes, tasks);
//        run(timeframes, tasks, false);
//        runBest(timeframes);
//        testOneStrategy(timeframes, tasks, new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(100));
//        buildTasksAndStrategiesSummary(timeframes, tasks, new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(100));
//        buildTasksAndStrategiesSummary(timeframes, tasks, null, null);
    }

}