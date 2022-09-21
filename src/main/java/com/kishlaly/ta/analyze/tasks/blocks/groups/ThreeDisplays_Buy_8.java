package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheckJava;
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
public class ThreeDisplays_Buy_8 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplaysJava.Config.STOCH_CUSTOM = 20;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_SoftTrendCheckJava());

            add(new Long_ScreenTwo_EMA_TwoBarsBelowJava());
            add(new Long_ScreenTwo_Bars_TwoAscendingJava());
            add(new Long_ScreenTwo_Bars_LastGreenJava());
            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscendingJava());
            add(new Long_ScreenTwo_Stoch_D_TwoBelow_XJava());
            add(new Long_ScreenTwo_Stoch_D_TwoAscendingJava());
            add(new Long_ScreenTwo_Stoch_K_TwoOrOneBelow_X());
            add(new Long_ScreenTwo_Stoch_K_TwoAscending());
            add(new Long_ScreenTwo_FilterLateEntryJava());
        }};

    }

    @Override
    public String comments() {
        return "Finding a strong oversold";
    }
}
