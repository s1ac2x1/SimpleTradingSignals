package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_MACD_Last_X_Ascending;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending;

import java.util.ArrayList;
import java.util.List;

/**
 * Long smooth rise of the MACD histogram on the first screen
 * <p>
 * First screen
 * 1) the last 6 MACD histograms go up in sequence
 * 2) 6, 5 and 4 from the end are negative
 * 3) 3, 2, 1 from the end are positive
 * Second screen
 * + for reliability let the MACD histogram go up from the negative level (can still stay negative)
 */
//TODO finish
public class FirstScreen_Buy_2 implements BlocksGroup {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK = 6;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_MACD_Last_X_Ascending());

            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
        }};

    }

    @Override
    public String comments() {
        return "Looking for the return of the price to EMA26 on the uptrend of the first screen";
    }
}
