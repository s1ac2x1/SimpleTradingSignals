package com.kishlaly.ta.analyze.tasks.groups

import com.kishlaly.ta.analyze.tasks.ThreeDisplays
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_LastAscending
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_LastBarCrosses
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_ThreeAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_FilterLateEntry
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_LastAscending

/**
 * an experimental strategy for trying out different ideas that come to mind
 */
class Experiments : AbstractBlocksGroup() {

    override fun init() {
        ThreeDisplays.Config.STOCH_CUSTOM = 30
    }

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                // Long_ScreenOne_SoftTrendCheck
                // not Long_late_entry
                // Long_ScreenOne_LastBarGreen
                // Long_ScreenOne_MACD_LastAscending
                // Long_ScreenTwo_Bars_TwoHighAscending
                // Long_ScreenTwo_Stoch_D_K_LastAscending
                add(Long_ScreenOne_EMA_LastAscending())
                add(Long_ScreenOne_EMA_LastAscending)
            }
        }
    }

    override fun comments() = "Experiments"

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