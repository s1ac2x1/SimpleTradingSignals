package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_LastBarCrosses;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarGreen;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarHigher;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_MACD_LastAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

// modification of buySignalType2 with an attempt to track the beginning of a long-term trend
//
// 1 screen: tracking the beginning of a move above the EMA by two bars
// last bar is green
// last bar is higher than the penultimate one
// last bar crosses EMA26
// last histogram is going up
// 2nd screen: look at the last two bars
// last quote.high is bigger than the penultimate quote.high
// last quote is not above EMA13
// last histogram is going up
// %D and %K of the last stochastic should be higher than the previous
//
// On historical data this strategy most often gives better returns and extremely low SL positions ratio
public class ThreeDisplays_Buy_4 implements BlocksGroupJava {

    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_LastBarGreen());
            add(new Long_ScreenOne_LastBarHigher());
            add(new Long_ScreenOne_EMA_LastBarCrosses());
            add(new Long_ScreenOne_MACD_LastAscending());

            add(new Long_ScreenTwo_Bars_TwoHighAscending());
            add(new Long_ScreenTwo_EMA_LastBarNotAbove());
            add(new Long_ScreenTwo_MACD_LastAscending());
            add(new Long_ScreenTwo_Stoch_D_K_LastAscending());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "The best TP/SL and price";
    }

}
