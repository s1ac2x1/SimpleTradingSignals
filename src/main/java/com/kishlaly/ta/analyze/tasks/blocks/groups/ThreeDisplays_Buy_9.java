package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheckJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;
import com.kishlaly.ta.analyze.tasks.blocks.two.bollinger.Long_ScreenTwo_Bollinger_Bottom_X_Of_Y_LastBarsCrossed;

import java.util.ArrayList;
import java.util.List;

/**
 * First screen: last EMA above and the last bar is green
 * Second screen:
 * + there was a crossing of the lower Bollinger band by one of the last three bars
 * + second and first quotes from the end are green
 * + second and first quotes from the end below EMA13
 * + one of three last %D of stochastics is below 20
 * + last histogram is rising
 * <p>
 * TP not higher than 50% of the channel
 */
public class ThreeDisplays_Buy_9 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplaysJava.Config.BOLLINGER_TOTAL_BARS_CHECK = 3;
        ThreeDisplaysJava.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1;
        ThreeDisplaysJava.Config.STOCH_VALUES_TO_CHECK = 3;
        ThreeDisplaysJava.Config.STOCH_OVERSOLD = 20;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_SoftTrendCheckJava());

            add(new Long_ScreenTwo_Bollinger_Bottom_X_Of_Y_LastBarsCrossedJava());
            add(new Long_ScreenTwo_Bars_TwoGreenJava());
            add(new Long_ScreenTwo_EMA_TwoBarsBelowJava());
            add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversold());
            add(new Long_ScreenTwo_MACD_LastAscendingJava());
        }};

    }

    @Override
    public String comments() {
        return "Searching for a smooth wave-like rise from oversold";
    }
}
