package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.tasks.blocks.groups.*;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPriceJava;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTopJava;
import com.kishlaly.ta.model.TimeframeJava;

import java.util.ArrayList;

import static com.kishlaly.ta.analyze.TaskRunnerJava.run;
import static com.kishlaly.ta.analyze.TaskTypeJava.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.analyze.testing.TaskTesterJava.testAllStrategiesOnSpecificDate;
import static com.kishlaly.ta.analyze.testing.TaskTesterJava.testOneStrategy;
import static com.kishlaly.ta.cache.CacheBuilderJava.buildTasksAndStrategiesSummary;

public class RunUtilsJava {

    public static void buildTasksAndStrategiesSummary_() {
        buildTasksAndStrategiesSummary(
                ContextJava.basicTimeframes,
                THREE_DISPLAYS_BUY,
                new ArrayList<BlocksGroupJava>() {{
                    add(new ThreeDisplays_Buy_1Java());
                    add(new ThreeDisplays_Buy_2Java());
                    add(new ThreeDisplays_Buy_3Java());
                    add(new ThreeDisplays_Buy_4Java());
                    add(new ThreeDisplays_Buy_5Java());
                    add(new ThreeDisplays_Buy_6Java());
                    add(new ThreeDisplays_Buy_7Java());
//                    add(new ThreeDisplays_Buy_8());
//                    add(new ThreeDisplays_Buy_9());
//                    add(new ThreeDisplays_Buy_Bollinger_1());
//                    add(new ThreeDisplays_Buy_Bollinger_1_2());
//                    add(new ThreeDisplays_Buy_Bollinger_2());
//                    add(new ThreeDisplays_Buy_Bollinger_3());
//                    add(new ThreeDisplays_Buy_Bollinger_4());
//                    add(new ThreeDisplays_Buy_EFI_1());
//                    add(new ThreeDisplays_Buy_EFI_2());
//                    add(new ThreeDisplays_Buy_EFI_3());
//                    add(new ThreeDisplays_Buy_Experiments());
                }},
                new StopLossFixedPriceJava(0.27), new TakeProfitFixedKeltnerTopJava(70));
    }

    // format: dd.mm.yyyy
    public static void testStrategiesOnSpecificDate_(String date) {
        testAllStrategiesOnSpecificDate(date, THREE_DISPLAYS_BUY, ContextJava.basicTimeframes);
    }

    public static void testOneStrategy_(BlocksGroupJava strategy) {
        testOneStrategy(
                ContextJava.basicTimeframes,
                THREE_DISPLAYS_BUY,
                strategy,
                new StopLossFixedPriceJava(0.27),
                new TakeProfitFixedKeltnerTopJava(80));

    }

    public static void runAllDaily() {
        ContextJava.runGroups = TimeframeJava.DAY;
        run(ContextJava.basicTimeframes, THREE_DISPLAYS_BUY, false,
                new ThreeDisplays_Buy_1Java(),
                new ThreeDisplays_Buy_2Java(),
                new ThreeDisplays_Buy_3Java(),
                new ThreeDisplays_Buy_4Java(),
                new ThreeDisplays_Buy_5Java(),
                new ThreeDisplays_Buy_6Java(),
                new ThreeDisplays_Buy_7Java(),
                new ThreeDisplays_Buy_8Java(),
                new ThreeDisplays_Buy_9Java(),
                new ThreeDisplays_Buy_Bollinger_1_2Java()
        );
    }

    public static void runAllWeekly(TimeframeJava[][] timeframes) {
        ContextJava.runGroups = TimeframeJava.WEEK;
        run(ContextJava.basicTimeframes, THREE_DISPLAYS_BUY, false,
                new FirstScreen_Buy_1Java()
                //new FirstScreen_Buy_2()
        );
    }

    public static void singleSymbol(String symbol) {
        ContextJava.testOnly = new ArrayList<String>() {{
            add(symbol);
        }};
    }

}
