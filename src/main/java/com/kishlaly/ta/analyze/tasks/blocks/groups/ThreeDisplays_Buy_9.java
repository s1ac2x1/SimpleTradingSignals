package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_LastBarCrosses;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_X_OutOf_Y_Above;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarGreen;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending;

import java.util.ArrayList;
import java.util.List;

/**
 * Возвращение цены к ЕМА на восходящем тренде
 * <p>
 * Первый экран
 * 1) последний столбик зеленый
 * 2) последний столбик пересекает ЕМА16
 * 3) из последних семи стобиков как минимум 4 полностью выше ЕМА26
 * <p>
 * попробовать StopLossVolatileLocalMin
 */
public class ThreeDisplays_Buy_9 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 20;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_LastBarGreen());
            add(new Long_ScreenOne_EMA_LastBarCrosses());
            add(new Long_ScreenOne_EMA_X_OutOf_Y_Above());

            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
        }};

    }
}
