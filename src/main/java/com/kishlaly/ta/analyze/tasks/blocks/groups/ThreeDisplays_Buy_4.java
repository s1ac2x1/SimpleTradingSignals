package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_LastBarCrosses;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarGreen;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_LastBarHigher;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_MACD_LastAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

// модификация buySignalType2 с попыткой остлеживания начала долгосрочного тренда
// 1 экран: отслеживание начала движения выше ЕМА по двум столбикам
//   последний столбик зеленый
//   последний столбик выше предпоследнего
//   последний столбик пересекает ЕМА26
//   последняя гистограмма растет
// 2 экран: смотреть на два последних столбика
//   high последнего столбика выше предпоследнего
//   последний столбик не выше ЕМА13
//   последняя гистограмма растет
//   %D и %K последнего стохастика должны быть выше, чем у предпоследнего
// ВНИМАНИЕ:
// после сигнала проверить вручную, чтобы на втором экране послединй столбик не поднимался слишком высоко от ЕМА13
public class ThreeDisplays_Buy_4 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_LastBarGreen());
            add(new Long_ScreenOne_LastBarHigher());
            add(new Long_ScreenOne_EMA_LastBarCrosses());
            add(new Long_ScreenOne_MACD_LastAscending());

            add(new Long_ScreenTwo_TwoBarsHighAscending());
            add(new Long_ScreenTwo_EMA_LastBarNotAbove());
            add(new Long_ScreenTwo_MACD_LastAscending());
            add(new Long_ScreenTwo_Stoch_D_K_LastAscending());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

}
