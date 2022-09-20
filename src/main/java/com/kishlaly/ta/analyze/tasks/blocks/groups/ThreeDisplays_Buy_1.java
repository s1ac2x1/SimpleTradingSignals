package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheckJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

public class ThreeDisplays_Buy_1 implements BlocksGroupJava {

    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_SoftTrendCheckJava());

            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_ThreeAscending());
            add(new Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold());
            add(new Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing());
            add(new Long_ScreenTwo_EMA_LastBarTooHighJava());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "Often good results, low SL ratio";
    }
}
