package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheckJava;
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
public class ThreeDisplays_Buy_5 implements BlocksGroupJava {

    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_SoftTrendCheckJava());

            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureUJava());
            add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversoldJava());
            add(new Long_ScreenTwo_Stoch_D_ThreeFigureU());
            add(new Long_ScreenTwo_Stoch_D_LastAscendingJava());
            add(new Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossingJava());
            add(new Long_ScreenTwo_FilterLateEntryJava());
        }};
    }

    @Override
    public String comments() {
        return "Tracks U-turn of indicators. Good TP/SL ratio";
    }

}
