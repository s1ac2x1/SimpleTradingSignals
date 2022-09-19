package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Short_ScreenOne_StrictTrendCheckJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

public class ThreeDisplays_Sell_1 implements BlocksGroupJava {

    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());
            add(new Short_ScreenOne_StrictTrendCheckJava());

            add(new Short_ScreenTwo_MACD_ThreeAboveZeroAndDescending());
            add(new Short_ScreenTwo_Stoch_D_ThreeDescending());
            add(new Short_ScreenTwo_Stoch_D_K_ThreeDescendingFromOverbought());
            add(new Short_ScreenTwo_EMA_ThreeBarsDescendingAndCrossing());
            add(new Short_ScreenTwo_EMA_LastBarTooLow());
        }};
    }

    @Override
    public String comments() {
        return "The first short strategy. Not stable";
    }
}
