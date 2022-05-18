package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_StrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Первый экран
 * + строгая проверка тренда
 * Второй экран
 * + было пересечение нижней лены Боллинжера одним из последних трех баров
 * + вторая и первая с конца котировки зеленые
 * + вторая и первая с конца котировки ниже ЕМА13
 * + одно из трех последних %D стохастика ниже 20
 * + последняя гистограмма растет
 * <p>
 * TP не выше 50% канала
 */
public class ThreeDisplays_Buy_9 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK = 3;
        ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1;
        ThreeDisplays.Config.STOCH_VALUES_TO_CHECK = 3;
        ThreeDisplays.Config.STOCH_OVERSOLD = 20;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            //add(new Long_ScreenOne_StrictTrendCheck());
            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_Bollinger_Low_X_Of_Y_LastBarsCrossed());
            add(new Long_ScreenTwo_Bars_TwoGreen());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversold());
            add(new Long_ScreenTwo_MACD_LastAscending());
        }};

    }

    @Override
    public String comments() {
        return "Поиск плавного волнообразного подъема из перепроданности. Очень редкий сигнал, но крайне мало SL";
    }
}
