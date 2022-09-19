package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheckJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

// inspired by [D] CFLT 20 Dec 2021
//
// first screen: last EMA is risign and the last bar is green
// second screen:
// oversold below 20 at TWO %D stochastic values
// last histogram is rising
// last bar is green
// last two bars go up (quote.low & quote.high)
// last two bars fully below EMA13
//
// entry 7 cents above the close of the last bar
// TP in the middle of the upper half of the Keltner channel
public class ThreeDisplays_Buy_3 implements BlocksGroupJava {

    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            ThreeDisplaysJava.Config.BOLLINGER_TOTAL_BARS_CHECK = 3;
            ThreeDisplaysJava.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1;

            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_SoftTrendCheckJava());

            add(new Long_ScreenTwo_Stoch_D_TwoStrongOversold());
            add(new Long_ScreenTwo_MACD_LastAscending());
            add(new Long_ScreenTwo_Bars_LastGreenJava());
            add(new Long_ScreenTwo_Bars_TwoAscending());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "Often good results, but the average SL is high";
    }

}
