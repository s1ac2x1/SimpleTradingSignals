package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * First screen: last EMA is rising and the last bar is green
 * Second screen:
 * 1) last two quotes are below EMA13
 * 2) last two quotes rise (low & high rise)
 * 3) last quote is green
 * 4) MACD histogram is negative and last is rising
 * 5) last two %D stochastics are below 20 and the last is rising
 * 6) the last two %K stochastics are below 20 and the last one is rising
 * <p>
 * TP not higher than 50% of the channel
 */
public class ThreeDisplays_Buy_8 implements BlocksGroup {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 20;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_Bars_TwoAscending());
            add(new Long_ScreenTwo_Bars_LastGreen());
            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoBelow_X());
            add(new Long_ScreenTwo_Stoch_D_TwoAscending());
            add(new Long_ScreenTwo_Stoch_K_TwoOrOneBelow_X());
            add(new Long_ScreenTwo_Stoch_K_TwoAscending());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};

    }

    @Override
    public String comments() {
        return "Finding a strong oversold";
    }
}
