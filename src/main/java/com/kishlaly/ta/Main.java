package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.tasks.blocks.groups.*;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.analyze.testing.TaskTester.testOneStrategy;
import static com.kishlaly.ta.cache.CacheBuilder.buildCache;
import static com.kishlaly.ta.cache.CacheBuilder.buildTasksAndStrategiesSummary;
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
                //SymbolsSource.NAGA,
                //SymbolsSource.SCREENER_FILTERED
        };
        Context.testOnly = new ArrayList<String>() {{
            add("XOM");
        }};
        Context.symbols = getSymbols();
        Context.yearsToAnalyze = 5;

//        buildCache(timeframes, false);
//        findBestStrategyForSymbols();
//        checkCache(timeframes, tasks);
//        run(timeframes, THREE_DISPLAYS_BUY, false, new ThreeDisplays_Buy_4());
//        runBest(timeframes);
//        testOneStrategy(timeframes, THREE_DISPLAYS_BUY, new ThreeDisplays_Buy_4(), new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(100));

        buildTasksAndStrategiesSummary(
                timeframes,
                THREE_DISPLAYS_BUY,
                new ArrayList<BlocksGroup>(){{
                    add(new ThreeDisplays_Buy_1());
                    add(new ThreeDisplays_Buy_2());
                    add(new ThreeDisplays_Buy_4());
                }},
                new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(100));
    }

}