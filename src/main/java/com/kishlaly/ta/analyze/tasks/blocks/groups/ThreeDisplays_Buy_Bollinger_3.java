package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Low_LastBarCrossed;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Low_PreLastBelow;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_LastAscending;

import java.util.ArrayList;
import java.util.List;

/**
 * Поиск сужения полос с признаками роста бычьего настроения
 * <p>
 * Второй экран:
 * + три последних значений верхней полосы уменьшаются
 * + три последних значения нижней полосы растут
 * + три последние гистограммы MACD растут (необязательно все три ниже нуля!)
 * + последние 2 %D стохастика растут
 * + предпоследняя %D стохастика ниже 40
 *
 * Пример: [D] AAPL 15.11.2021, 7.06.2021
 */
//TODO закончить
public class ThreeDisplays_Buy_Bollinger_3 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

        }};

    }

    @Override
    public String comments() {
        return "Сужение полос Боллинжера с признаками роста бычьего настроения";
    }
}
