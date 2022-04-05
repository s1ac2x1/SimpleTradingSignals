package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_ThreeAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Касание цены нижней ленты Боллинжера и намек на рост.
 * Первый экран: мягкая проверка трех ЕМА26
 * Второй экран:
 * + было пересечение нижней лены Боллинжера одним из последних трех баров
 * + две гистограммы MACD ниже нуля и последняя выше
 * + две %D стохастика ниже 30 и последняя выше
 * + последний столбик зеленый и выше предыдущего
 * + фильтровать late entry
 * <p>
 * SL можно скользящий по средней ленте Боллинжера
 */
public class ThreeDisplays_Buy_Bollinger_1_2 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK = 3;
        ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1;
        ThreeDisplays.Config.STOCH_CUSTOM = 30;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_EMA_ThreeAscending());

            add(new Long_ScreenTwo_Bollinger_Low_X_Of_Y_LastBarsCrossed());
            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoBelow_X());
            add(new Long_ScreenTwo_Stoch_D_LastAscending());
            add(new Long_ScreenTwo_Bars_LastGreen());
            add(new Long_ScreenTwo_Bars_TwoAscending());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};

    }

    @Override
    public String comments() {
        return "Цена недавно касалась нижней ленты, проверки долгосрочного тренда нету";
    }
}
