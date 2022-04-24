package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_EMA_ThreeAscending;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

// вдохновитель [D] CFLT 20 Dec 2021
// первый экран - мягкая проверка
// второй экран -
//    перепроданность ниже 20 у ДВУХ значений медленной линии стохастика
//    последняя гистограмма повышается
//    последний бар зеленый
//    последние два бара повышаются (quote.low & quote.high)
//    последние два бара полностью ниже ЕМА13
// вход на 7 центов выше закрытия последнего бара
// TP на середине верхней половины канала Кельтнера
public class ThreeDisplays_Buy_3 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK = 3;
            ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1;

            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_Stoch_D_TwoStrongOversold());
            add(new Long_ScreenTwo_MACD_LastAscending());
            add(new Long_ScreenTwo_Bars_LastGreen());
            add(new Long_ScreenTwo_Bars_TwoAscending());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "Часто хорошие результаты, но в среднем SL высоковат";
    }

}
