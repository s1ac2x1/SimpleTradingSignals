package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Search for narrowing bands with signs of bullish growth
 * <p>
 * Second screen:
 * + the last three values of the upper band are declining -- maybe check the last 4-5 values? TODO
 * + the last three values of the lower band are rising -- maybe check the last 4-5 values? TODO
 * + the last three MACD histograms are rising (not necessarily all three below zero!)
 * + last two %D of stochastics are going up
 * + penultimate %D stochastic is below 40
 * <p>
 * SL sliding in the middle of the Keltner channel
 * <p>
 * Example: [D] AAPL 15.11.2021, 7.06.2021
 */
public class ThreeDisplays_Buy_Bollinger_3 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplaysJava.Config.STOCH_CUSTOM = 40;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenTwo_Bollinger_Top_ThreeDescending());
            add(new Long_ScreenTwo_Bollinger_Bottom_ThreeAscendingJava());
            add(new Long_ScreenTwo_MACD_ThreeAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoOrOneBelow_X());
        }};

    }

    @Override
    public String comments() {
        return "Narrowing of the Bollinger Bands with signs of bullish growth";
    }
}
