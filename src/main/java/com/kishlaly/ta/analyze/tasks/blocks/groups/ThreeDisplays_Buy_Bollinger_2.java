package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Bottom_LastBarCrossed;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Bollinger_Bottom_PreLastBelow;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_Stoch_D_LastAscending;

import java.util.ArrayList;
import java.util.List;

/**
 * Редкое явление, когда цена уходит вниз за ленту Боллинжера
 * <p>
 * Первый экран: последняя ЕМА выше и последний столбик зеленый
 * Второй экран:
 * + вторая с конца котировка ниже нижней ленты Боллинжера
 * + последняя котировка пересекает нижнюю ленту Боллинжера
 * + 2 гистограммы MACD отрицательные и последняя выше
 * + последняя %D стохастика растет
 */
public class ThreeDisplays_Buy_Bollinger_2 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_Bollinger_Bottom_PreLastBelow());
            add(new Long_ScreenTwo_Bollinger_Bottom_LastBarCrossed());
            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_LastAscending());
        }};

    }

    @Override
    public String comments() {
        return "Поиск выпадания за нижнюю ленту. Плохо работает";
    }
}
