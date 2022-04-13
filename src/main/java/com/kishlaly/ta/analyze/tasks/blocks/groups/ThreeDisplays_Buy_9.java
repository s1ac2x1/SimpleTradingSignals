package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_ThreeAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Keltner_Bottom_X_FromEndCrossed;

import java.util.ArrayList;
import java.util.List;

/**
 * Первый экран
 * + повышение трех последних EMA26 (нету проверок гистограммы и столбиков)
 * Второй экран
 * + третья с конца котировка пересекла нижнию границу канала Кельтнера
 * + вторая и первая с конца котировки зеленые
 * + вторая и первая с конца котировки ниже ЕМА13
 * + одно из трех последних %D стохастика ниже 20
 * + последняя гистограмма растет
 * <p>
 * TP не выше 50% канала
 */
//TODO закончить
// TODO проверить на [D] SEAS 14.04.2022 & 09.03.2022
public class ThreeDisplays_Buy_9 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.QUOTE_FROM_END_TO_USE = 3;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_EMA_ThreeAscending());

            add(new Long_ScreenTwo_Keltner_Bottom_X_FromEndCrossed());
        }};

    }

    @Override
    public String comments() {
        return "Поиск плавного волнообразного подъема из перепроданности";
    }
}
