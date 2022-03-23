package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_ThreeAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Первый экран
 * 1) повышение трех последних EMA26 (нету проверок гистограммы и столбиков)
 * Второй экран
 * 1) две последние котировки ниже ЕМА13
 * 2) две последние котировки повышаются (low & high растут)
 * 3) последняя котировка зеленая
 * 4) гистограмма МАСD отризательная и повышается
 * 5) последние две %D стохастика ниже 20 и последняя выше предпоследней
 * 6) одна из двух последних %K стохастика ниже 20 и последняя выше предпоследней
 */
public class ThreeDisplays_Buy_8 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_EMA_ThreeAscending());

            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_Bars_TwoAscending());
            add(new Long_ScreenTwo_Bars_OneGreen());
            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoBelow_20());
            add(new Long_ScreenTwo_Stoch_D_TwoAscending());
            add(new Long_ScreenTwo_Stoch_K_TwoOrOneBelow_20());
            add(new Long_ScreenTwo_Stoch_K_TwoAscending());
        }};

    }
}
