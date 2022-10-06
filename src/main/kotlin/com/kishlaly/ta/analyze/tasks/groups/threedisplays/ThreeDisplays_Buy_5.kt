package com.kishlaly.ta.analyze.tasks.groups.threedisplays

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_FilterLateEntry
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureU
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_K_SomeWereOversold
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_LastAscending
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_ThreeFigureU

/**
 * a copy of ThreeDisplays_Buy_2
 * added Long_ScreenTwo_Stoch_D_ThreeFigureU
 * Long_ScreenTwo_MACD_TwoBelowZeroAndAscending replaced by Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureU
 * <p>
 * Works better with TP 70% of the channel
 */
class ThreeDisplays_Buy_5 : BlocksGroup {

    override fun blocks(): List<TaskBlock> {
        return object : ArrayList<TaskBlock>() {
            init {
                add(ScreenBasicValidation())

                add(Long_ScreenOne_SoftTrendCheck())

                add(Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureU())
                add(Long_ScreenTwo_Stoch_D_K_SomeWereOversold())
                add(Long_ScreenTwo_Stoch_D_ThreeFigureU())
                add(Long_ScreenTwo_Stoch_D_LastAscending())
                add(Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing())
                add(Long_ScreenTwo_FilterLateEntry())
            }
        }
    }

    override fun comments() = "Tracks U-turn of indicators. Good TP/SL ratio"

}