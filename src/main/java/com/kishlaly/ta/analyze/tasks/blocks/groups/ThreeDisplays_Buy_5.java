package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * a copy of ThreeDisplays_Buy_2
 * added Long_ScreenTwo_Stoch_D_ThreeFigureU
 * Long_ScreenTwo_MACD_TwoBelowZeroAndAscending replaced by Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureU
 * <p>
 * Works better with TP 70% of the channel
 */
public class ThreeDisplays_Buy_5 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureU());
            add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversold());
            add(new Long_ScreenTwo_Stoch_D_ThreeFigureU());
            add(new Long_ScreenTwo_Stoch_D_LastAscending());
            add(new Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "Tracks U-turn of indicators. Good TP/SL ratio";
    }

}
