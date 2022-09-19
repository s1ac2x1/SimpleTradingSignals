package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheckJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Bottom_LastBarCrossed;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Bottom_PreLastBelow;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_LastAscending;

import java.util.ArrayList;
import java.util.List;

/**
 * A rare occurrence when the price goes down beyond the Bollinger band
 * <p>
 * First screen: last EMA is higher and the last bar is green
 * Second screen:
 * + prelast quote is below bottom Bollinger band
 * + last quote crosses the bottom Bollinger band
 * + two last MACD histograms are negative and the last one is higher
 * + Last %D stochastic is rising
 */
public class ThreeDisplays_Buy_Bollinger_2 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_SoftTrendCheckJava());

            add(new Long_ScreenTwo_Bollinger_Bottom_PreLastBelow());
            add(new Long_ScreenTwo_Bollinger_Bottom_LastBarCrossed());
            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_LastAscending());
        }};

    }

    @Override
    public String comments() {
        return "Finding the fallout behind the bottom band. Doesn't work well.";
    }
}
