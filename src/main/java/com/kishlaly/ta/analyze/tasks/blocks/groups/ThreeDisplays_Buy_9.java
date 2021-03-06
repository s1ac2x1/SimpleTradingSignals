package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

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
public class ThreeDisplays_Buy_9 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK = 3;
        ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1;
        ThreeDisplays.Config.STOCH_VALUES_TO_CHECK = 3;
        ThreeDisplays.Config.STOCH_OVERSOLD = 20;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_Bollinger_Bottom_X_Of_Y_LastBarsCrossed());
            add(new Long_ScreenTwo_Bars_TwoGreen());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversold());
            add(new Long_ScreenTwo_MACD_LastAscending());
        }};

    }

    @Override
    public String comments() {
        return "Searching for a smooth wave-like rise from oversold";
    }
}
