package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_StrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;
import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice;

import java.util.ArrayList;
import java.util.List;

/**
 * Первого экрана нету
 * Второй экран:
 * 1) последняя гистограмма MACD повысилась
 * 2) одно из двух значений %D стохастика меньше 20
 * 3) две последние котировки ниже ЕМА13
 * 4) последняя котировка - зеленая
 * <p>
 * TP не выше 50% расстояния от середины до вершины канала Кельтнера
 */
public class ThreeDisplays_Buy_6 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 20;
        StopLossFixedPrice.LAST_QUOTES_TO_FIND_MIN = 40;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            //add(new Long_ScreenOne_StrictTrendCheck());
            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_MACD_LastAscending());
            add(new Long_ScreenTwo_Stoch_D_TwoOrOneBelow_X());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            add(new Long_ScreenTwo_Bars_LastGreen());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};

    }

    @Override
    public String comments() {
        return "Хорошая доходность, но число SL сильно перевешивает";
    }
}
