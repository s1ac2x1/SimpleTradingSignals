package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_ThreeAscendingJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_FilterLateEntryJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_ThreeBelowZeroAndAscendingJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_LastAscendingJava;

import java.util.ArrayList;
import java.util.List;

/**
 * an experimental strategy for trying out different ideas that come to mind
 */
public class ThreeDisplays_Buy_Experiments implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplaysJava.Config.STOCH_CUSTOM = 30;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());

            add(new Long_ScreenOne_EMA_ThreeAscendingJava());

            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscendingJava());
            add(new Long_ScreenTwo_Stoch_D_LastAscendingJava());
            add(new Long_ScreenTwo_FilterLateEntryJava());
        }};

    }

    @Override
    public String comments() {
        return "Experiments";
    }
}
// =================
//
// SL [Fixed] price 0.27
// TP [Fixed] Keltner 80% top
// TP/SL = 54/19 = 80.6% / 28.36%%
//
// add(new ScreenBasicValidation());
// add(new Long_ScreenOne_EMA_ThreeAscending());
// add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
//
//
// =================
//
// 	SL [Fixed] price 0.27
//	TP [Fixed] Keltner 80% top
//	TP/SL = 29/9 = 76.32% / 23.69%%
//
// add(new ScreenBasicValidation());
// add(new Long_ScreenOne_EMA_ThreeAscending());
// add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
// add(new Long_ScreenTwo_Stoch_D_LastAscending());
// add(new Long_ScreenTwo_FilterLateEntry());
//
// =================
