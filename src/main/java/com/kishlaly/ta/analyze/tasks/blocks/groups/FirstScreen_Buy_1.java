package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_LastBarCrosses;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_X_OutOf_Y_Above;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarGreen;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending;

import java.util.ArrayList;
import java.util.List;

/**
 * Price returns to the EMA on an uptrend
 * <p>
 * First screen
 * 1) last bar is green
 * 2) last bar crosses the EMA26
 * 3) out of last 7 bars at least 4 are completely above EMA26
 * <p>
 * SL: try StopLossVolatileLocalMin
 */
public class FirstScreen_Buy_1 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplaysJava.Config.EMA26_TOTAL_BARS_CHECK = 7;
        ThreeDisplaysJava.Config.EMA26_ABOVE_BARS = 4;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_LastBarGreen());
            add(new Long_ScreenOne_EMA_LastBarCrosses());
            add(new Long_ScreenOne_EMA_X_OutOf_Y_Above());

            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
        }};

    }

    @Override
    public String comments() {
        return "Looking for the return of the price to EMA26 on the uptrend of the first screen";
    }
}
