package com.kishlaly.ta.utils;

import com.kishlaly.ta.analyze.tasks.blocks.groups.*;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop;
import com.kishlaly.ta.model.Timeframe;

import java.util.ArrayList;

import static com.kishlaly.ta.analyze.TaskRunner.run;
import static com.kishlaly.ta.analyze.TaskType.THREE_DISPLAYS_BUY;
import static com.kishlaly.ta.analyze.testing.TaskTester.testOneStrategy;
import static com.kishlaly.ta.analyze.testing.TaskTester.testAllStrategiesOnSpecificDate;
import static com.kishlaly.ta.cache.CacheBuilder.buildTasksAndStrategiesSummary;

public class RunUtils {

    public static void buildTasksAndStrategiesSummary_() {
        buildTasksAndStrategiesSummary(
                new Timeframe[][]{{Timeframe.WEEK, Timeframe.DAY}},
                THREE_DISPLAYS_BUY,
                new ArrayList<BlocksGroup>() {{
//                    add(new ThreeDisplays_Buy_1());
//                    add(new ThreeDisplays_Buy_2());
//                    add(new ThreeDisplays_Buy_3());
//                    add(new ThreeDisplays_Buy_4());
//                    add(new ThreeDisplays_Buy_5());
//                    add(new ThreeDisplays_Buy_6());
//                    add(new ThreeDisplays_Buy_7());
//                    add(new ThreeDisplays_Buy_8());
                    add(new ThreeDisplays_Buy_9());
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
                new StopLossFixedPrice(0.27), new TakeProfitFixedKeltnerTop(70));
    }

    public static void testStrategiesOnSpecificDate_(String date) {
        testAllStrategiesOnSpecificDate(date, THREE_DISPLAYS_BUY, new Timeframe[][]{{Timeframe.WEEK, Timeframe.DAY}});
    }

    public static void testOneStrategy_() {
        testOneStrategy(new Timeframe[][]{{Timeframe.WEEK, Timeframe.DAY}}, THREE_DISPLAYS_BUY,
                new ThreeDisplays_Buy_5(),
                new StopLossFixedPrice(0.27),
                new TakeProfitFixedKeltnerTop(70));

    }

    public static void buyDaily() {
        Context.runGroups = Timeframe.DAY;
        run(new Timeframe[][]{{Timeframe.WEEK, Timeframe.DAY}}, THREE_DISPLAYS_BUY, false,
                new ThreeDisplays_Buy_1(),
                new ThreeDisplays_Buy_2(),
                new ThreeDisplays_Buy_3(),
                new ThreeDisplays_Buy_4(),
//                new ThreeDisplays_Buy_5(),
                new ThreeDisplays_Buy_6(),
                new ThreeDisplays_Buy_7(),
                new ThreeDisplays_Buy_8(),
                new ThreeDisplays_Buy_9()
//                new ThreeDisplays_Buy_Bollinger_1_2(),
//                new ThreeDisplays_Buy_Bollinger_2()
        );
    }

    public static void buyWeekly(Timeframe[][] timeframes) {
        Context.runGroups = Timeframe.WEEK;
        run(new Timeframe[][]{{Timeframe.WEEK, Timeframe.DAY}}, THREE_DISPLAYS_BUY, false,
                new FirstScreen_Buy_1()
                //new FirstScreen_Buy_2()
        );
    }


}
