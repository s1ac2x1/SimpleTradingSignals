package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;

import java.util.ArrayList;
import java.util.List;

/**
 * First screen: last EMA is rising and the last quote is green
 * Second screen:
 * 1) the last MACD histogram is rising
 * 2) one of two stochastic %D values is below 20
 * 3) last two quotes are below EMA13
 * 4) Last quote is green
 * <p>
 * TP is not higher than 50% of the distance from the middle to the top of the Keltner channel
 */
public class ThreeDisplays_Buy_6 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 20;
        StopLossFixedPrice.LAST_QUOTES_TO_FIND_MIN = 40;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_MACD_LastAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoOrOneBelow_X());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_Bars_LastGreen());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};

    }

    @Override
    public String comments() {
        return "A variation on the search for a rise out of oversold. Good returns and number of positions";
    }
}
