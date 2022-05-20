package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

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
 * <p>
 * SL скользящий по середине канала Кельтнера
 * <p>
 * Пример: [D] AAPL 15.11.2021, 7.06.2021
 */
//TODO закончить
public class ThreeDisplays_Buy_Bollinger_3 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 40;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenTwo_Bollinger_Top_ThreeDescending());
            add(new Long_ScreenTwo_Bollinger_Bottom_ThreeAscending());
            add(new Long_ScreenTwo_MACD_ThreeAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoOrOneBelow_X());
        }};

    }

    @Override
    public String comments() {
        return "Сужение полос Боллинжера с признаками роста бычьего настроения";
    }
}
