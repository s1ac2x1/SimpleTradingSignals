package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays.Config;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

// модификация ThreeDisplays_Buy_4 для поиска ультра коротких позиций
// 1 экран: последняя ЕМА выше и последний столбик зеленый
// 2 экран: добавлена проверка, чтобы последняя котировка не поднималась выше 10% от середины канала
//
// TP 50-70% от канала
//
// на исторических тестах показывает хороший баланс, но количество SL позиций сильно больше TP
public class ThreeDisplays_Buy_7 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        Config.FILTER_BY_KELTNER_ENABLED = true;
        Config.FILTER_BY_KELTNER = 10;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_Bars_TwoHighAscending());
            add(new Long_ScreenTwo_EMA_LastBarNotAbove());
            add(new Long_ScreenTwo_MACD_LastAscending());
            add(new Long_ScreenTwo_Stoch_D_K_LastAscending());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "Хорошая доходность, но много позиций";
    }

}
