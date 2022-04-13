package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_ThreeAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

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
// TODO проверить на [D] SEAS 11.04.2022 & 09.03.2022
public class ThreeDisplays_Buy_9 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.QUOTE_FROM_END_TO_USE = 3;
        ThreeDisplays.Config.STOCH_VALUES_TO_CHECK = 3;
        ThreeDisplays.Config.STOCH_OVERSOLD = 20;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_EMA_ThreeAscending());

            add(new Long_ScreenTwo_Keltner_Bottom_X_FromEndCrossed());
            add(new Long_ScreenTwo_Bars_TwoGreen());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversold());
            add(new Long_ScreenTwo_MACD_LastAscending());
        }};

    }

    @Override
    public String comments() {
        return "Поиск плавного волнообразного подъема из перепроданности";
    }
}
