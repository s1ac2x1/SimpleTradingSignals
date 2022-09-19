package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheckJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Touching the bottom Bollinger band and a hint of growth.
 * First screen: last EMA is higher and the last bar is green
 * Second screen:
 * + there was a crossing of the lower Bollinger band by one of the last three bars
 * + two MACD histograms below zero and last one is growing
 * + two %D stochastics below 30 and last one is growing
 * + the last bar is green and higher than the previous one
 * + filter late entry
 * <p>
 * SL can be sliding on the average Bollinger band
 */
public class ThreeDisplays_Buy_Bollinger_1 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplaysJava.Config.BOLLINGER_TOTAL_BARS_CHECK = 3;
        ThreeDisplaysJava.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1;
        ThreeDisplaysJava.Config.STOCH_CUSTOM = 30;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            //add(new Long_ScreenOne_StrictTrendCheck());
            add(new Long_ScreenOne_SoftTrendCheckJava());

            add(new Long_ScreenTwo_Bollinger_Bottom_X_Of_Y_LastBarsCrossed());
            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoBelow_X());
            add(new Long_ScreenTwo_Stoch_D_LastAscending());
            add(new Long_ScreenTwo_Bars_LastGreenJava());
            add(new Long_ScreenTwo_Bars_TwoAscending());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};

    }

    @Override
    public String comments() {
        return "Price recently touched the bottom band, good TP/SL ratio";
    }
}
