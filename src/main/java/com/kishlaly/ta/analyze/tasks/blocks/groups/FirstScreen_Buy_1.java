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
 * 2) последний столбик пересекает ЕМА26
 * 3) из последних 7 стобиков как минимум 4 полностью выше ЕМА26
 * <p>
 * попробовать StopLossVolatileLocalMin
 */
public class FirstScreen_Buy_1 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK = 7;
        ThreeDisplays.Config.EMA26_ABOVE_BARS = 4;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_LastBarGreen());
            add(new Long_ScreenOne_EMA_LastBarCrosses());
            add(new Long_ScreenOne_EMA_X_OutOf_Y_Above());

            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
        }};

    }

    @Override
    public String comments() {
        return "Ищет возвращение цены к ЕМА26 на восходящем тренде первого экрана";
    }
}
