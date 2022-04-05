package com.kishlaly.ta;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.groups.*;
import com.kishlaly.ta.analyze.testing.sl.*;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitDisabled;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitVolatileKeltnerTop;
import com.kishlaly.ta.model.SymbolsSource;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.FIRST_TRUST_MODEL;
import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.analyze.testing.TaskTester.testOneStrategy;
import static com.kishlaly.ta.cache.CacheBuilder.buildCache;
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
                SymbolsSource.NAGA,
                //SymbolsSource.SCREENER_FILTERED
                //SymbolsSource.SCREENER_MANY
                //SymbolsSource.TEST
        };

//        Context.testOnly = new ArrayList<String>() {{
//            add("TER");
//        }};
        Context.symbols = getSymbols();
        Context.yearsToAnalyze = 5;

//        buildCache(timeframes, false);
//        findBestStrategyForSymbols(THREE_DISPLAYS_BUY);
        ThreeDisplays.Config.FILTER_BY_KELTNER_ENABLED = true;
        ThreeDisplays.Config.FILTER_BY_KELTNER = 20;

//        run(timeframes, THREE_DISPLAYS_BUY, false,
//                new ThreeDisplays_Buy_1(),
//                new ThreeDisplays_Buy_2(),
//                new ThreeDisplays_Buy_4(),
//                new ThreeDisplays_Buy_8(),
//                new ThreeDisplays_Buy_9(),
//                new ThreeDisplays_Buy_Bollinger_1(),
//                new ThreeDisplays_Buy_Bollinger_1_2(),
//                new ThreeDisplays_Buy_Bollinger_2()
//        );

//        runBest(timeframes);
//        testOneStrategy(timeframes, THREE_DISPLAYS_BUY, new ThreeDisplays_Buy_Bollinger_1(), new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(70));

//        закончить стратегии по EFI
//           так же добавить в другие стратегии шаг "фильтровать точку входа, если EFI ниже нуля" и проверить
//        закончить ThreeDisplays_Buy_Bollinger_3
//        что там Элдер писал про анализ графиков и отклонения цен?
//        дивергенции EFI

// найти как скачать график по золоту (тикер GOLD?) и проанализвать касание ценой лент Боллинжера:
//    касание нижней ленты - длинная позиция с TP чуть ниже среднейл ленты
//    касание верхней ленты - короткая позиция с TP чуть выше средней
// вопросы: нужно проверять долгосрочный тренд? какоие фреймы использовать - 2часа и 25минут?
// протестировать все

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
//                    add(new ThreeDisplays_Buy_10());
//                    add(new ThreeDisplays_Buy_Experiments());
//                }},
//                new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(80));

    }

}