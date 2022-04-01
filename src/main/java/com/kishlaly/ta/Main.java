package com.kishlaly.ta;

import com.kishlaly.ta.analyze.TaskType;
import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.groups.*;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedKeltnerBottom;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.sl.StopLossVolatileLocalMin;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitDisabled;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.FIRST_TRUST_MODEL;
import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
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
                SymbolsSource.SP500,
                //SymbolsSource.NAGA,
                //SymbolsSource.SCREENER_FILTERED
                //SymbolsSource.SCREENER_MANY_RANDOM
                //SymbolsSource.TEST
        };

//        Context.testOnly = new ArrayList<String>() {{
//            add("INTC");
//        }};
        Context.symbols = getSymbols();
        Context.yearsToAnalyze = 5;

//        buildCache(timeframes, false);
//        findBestStrategyForSymbols(THREE_DISPLAYS_BUY);
        ThreeDisplays.Config.FILTER_BY_KELTNER_ENABLED = true;
        ThreeDisplays.Config.FILTER_BY_KELTNER = 20;
//        run(timeframes, FIRST_TRUST_MODEL, false, new FirstTrustModel_Buy_1()); // только для SP500
//        run(timeframes, THREE_DISPLAYS_BUY, false, new ThreeDisplays_Buy_2());
//        run(timeframes, THREE_DISPLAYS_BUY, false, new ThreeDisplays_Buy_4());
//        run(timeframes, THREE_DISPLAYS_BUY, false, new ThreeDisplays_Buy_8()); // сильная перегпроданность
//        run(timeframes, THREE_DISPLAYS_BUY, false, new ThreeDisplays_Buy_9()); // отслеживать недельные
//        run(timeframes, THREE_DISPLAYS_BUY, false, new ThreeDisplays_Buy_10()); // Боллинжер, лучше SP500
//        run(timeframes, THREE_DISPLAYS_BUY, false, new ThreeDisplays_Buy_Experiments());
//        runBest(timeframes);
//        testOneStrategy(timeframes, THREE_DISPLAYS_BUY, new ThreeDisplays_Buy_10(), new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(50));

//        приоритет:
//        1) стратегии с полосами Боллинжера
//           частный случай: когда 2 из последних 3 баров целиком ниже нинжей ленты, последний бар касается ее, три гистограммы отрицательные и растут, три %D ниже 20 и растут (INTC 24.05.19)
//        2) что там Элдер писал про анализ графиков и отклонения цен?
//        3) дивергенции EFI
//        4) тестировать на определенных участках между датами

//        buildTasksAndStrategiesSummary(
//                timeframes,
//                THREE_DISPLAYS_BUY,
//                new ArrayList<BlocksGroup>(){{
//                    add(new ThreeDisplays_Buy_1());
//                    add(new ThreeDisplays_Buy_2());
//                    add(new ThreeDisplays_Buy_3());
//                    add(new ThreeDisplays_Buy_4());
//                    add(new ThreeDisplays_Buy_5());
//                    add(new ThreeDisplays_Buy_6());
//                    add(new ThreeDisplays_Buy_7());
//                    add(new ThreeDisplays_Buy_8());
//                    add(new ThreeDisplays_Buy_Experiments());
//                }},
//                new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(80));

    }

}