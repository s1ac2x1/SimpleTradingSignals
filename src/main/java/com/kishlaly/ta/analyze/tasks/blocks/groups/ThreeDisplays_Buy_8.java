package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Первый экран: последняя ЕМА выше и последний столбик зеленый
 * Второй экран:
 * 1) две последние котировки ниже ЕМА13
 * 2) две последние котировки повышаются (low & high растут)
 * 3) последняя котировка зеленая
 * 4) гистограмма МАСD отрицательная и повышается
 * 5) последние две %D стохастика ниже 20 и последняя выше предпоследней
 * 6) одна из двух последних %K стохастика ниже 20 и последняя выше предпоследней
 * <p>
 * TP не выше 50% канала
 */
public class ThreeDisplays_Buy_8 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 20;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_Bars_TwoAscending());
            add(new Long_ScreenTwo_Bars_LastGreen());
            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoBelow_X());
            add(new Long_ScreenTwo_Stoch_D_TwoAscending());
            add(new Long_ScreenTwo_Stoch_K_TwoOrOneBelow_X());
            add(new Long_ScreenTwo_Stoch_K_TwoAscending());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};

    }

    @Override
    public String comments() {
        return "Поиск сильной перепроданности";
    }
}
