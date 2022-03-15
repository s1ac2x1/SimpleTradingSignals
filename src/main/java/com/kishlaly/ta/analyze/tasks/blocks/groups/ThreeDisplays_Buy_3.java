package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_StrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

// проверить buy стратегию (вдохновитель [D] CFLT 20 Dec 2021)
// первый экран - подумать TODO
// второй экран -
//    перепроданность ниже 20 у трех значений медленной линии стохастика и она повышается
//    последние три столбика гистограммы повышаются
//    два из трех последних баров зеленые
//    последние два бара повышаются (quote.low & quote.high)
//    последние два бара полностью ниже ЕМА13
// вход на 7 центов выше закрытия последнего бара
// TP на середине верхней половины канала Кельтнера
public class ThreeDisplays_Buy_3 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_StrictTrendCheck());

            add(new Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromStrongOversold());
            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
            add(new Long_ScreenTwo_TwoBarsGreen());
            add(new Long_ScreenTwo_TwoBarsAscending());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
        }};
    }

}
