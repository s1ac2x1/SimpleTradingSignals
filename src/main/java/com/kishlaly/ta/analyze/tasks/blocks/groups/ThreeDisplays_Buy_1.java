package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.*;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;
import com.kishlaly.ta.utils.Context;

import java.util.ArrayList;
import java.util.List;

public class ThreeDisplays_Buy_1 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            // для безопасности на медвежьих рынках:
            add(new Long_ScreenOne_SoftTrendCheck());
            add(new Long_ScreenOne_StrictTrendCheck());
            add(new Long_ScreenOne_LastBarHigher());
            add(new Long_ScreenOne_EMA_ThreeAscending());
            add(new Long_ScreenOne_MACD_LastAscending());

            // тут только сохраняем значение в контекст, сама проверка всегда ОК
            add(new Long_ScreenOne_EMA50_Over_EMA200());

//            // Внутри бычьего режима выбираем дневной тренд-чек
//            if (Context.EMA50_OVER_EMA200) {
//                add(new Long_ScreenTwo_EMA26_Grows());
//                add(new Long_ScreenTwo_ClosePrice_Above_EMA26());
//                add(new Long_ScreenTwo_ADX_AdaptiveAbove(14, 50, 1.2));
//            } else {
//                // в остальных случаях (медвежье + боковик) - жёсткий фильтр Элдера
//                add(new Long_ScreenTwo_StrictTrendCheck());
//                add(new Long_ScreenOne_EMA_ThreeAscending()); // тут нужны дневные котировки
//            }

            // фильтруем дальше
            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_ThreeAscending());
            add(new Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold());
            add(new Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing());
            add(new Long_ScreenTwo_EMA_LastBarTooHigh());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "Often good results, low SL ratio";
    }
}
