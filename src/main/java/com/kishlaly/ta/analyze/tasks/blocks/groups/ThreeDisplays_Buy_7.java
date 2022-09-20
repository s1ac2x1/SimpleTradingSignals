package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava.Config;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheckJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

// ThreeDisplays_Buy_4 modification to search for ultra-short positions
// 1 screen: last EMA is higher and the last bar is green
// 2 screen: added check that the last quote does not go higher than 10% from the middle of the channel
//
// TP 50-70% of the channel
//
// on historical tests shows a good balance, but the number of SL positions is much higher than the TP
public class ThreeDisplays_Buy_7 implements BlocksGroupJava {

    public List<TaskBlockJava> blocks() {
        Config.FILTER_BY_KELTNER_ENABLED = true;
        Config.FILTER_BY_KELTNER = 10;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_SoftTrendCheckJava());

            add(new Long_ScreenTwo_Bars_TwoHighAscendingJava());
            add(new Long_ScreenTwo_EMA_LastBarNotAboveJava());
            add(new Long_ScreenTwo_MACD_LastAscendingJava());
            add(new Long_ScreenTwo_Stoch_D_K_LastAscending());
            add(new Long_ScreenTwo_FilterLateEntryJava());
        }};
    }

    @Override
    public String comments() {
        return "Good returns, but a lot of positions";
    }

}
