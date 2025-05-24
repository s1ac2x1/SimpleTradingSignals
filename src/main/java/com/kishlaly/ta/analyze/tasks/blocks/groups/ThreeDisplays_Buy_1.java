package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.*;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

public class ThreeDisplays_Buy_1 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_StrictTrendCheck());
            add(new Long_ScreenOne_LastBarHigher());
            add(new Long_ScreenOne_EMA_ThreeAscending());
            add(new Long_ScreenOne_MACD_LastAscending());

            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_ThreeAscending());
            add(new Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold());
            add(new Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing());
            add(new Long_ScreenTwo_EMA_LastBarTooHigh());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "Often good results, low SL ratio";
    }
}
