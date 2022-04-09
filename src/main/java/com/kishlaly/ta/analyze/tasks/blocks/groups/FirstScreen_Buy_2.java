package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_MACD_TwoBelowZeroAndAscending;

import java.util.ArrayList;
import java.util.List;

/**
 * Длительный плавный подъем гистограммы MACD на первом экране
 * <p>
 * Первый экран
 * 1) последние 6 гистограмм MACD растут последовательно
 * 2) 6, 5 и 4 с конца - отрицательные
 * 3) 3, 2 1 с конца - положительные
 * Второй экран
 * + для надежности пусть гистограмма MACD повышается из отрицательного уровня
 */
//TODO закончить
public class FirstScreen_Buy_2 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.EMA26_TOTAL_BARS_CHECK = 7;
        ThreeDisplays.Config.EMA26_ABOVE_BARS = 4;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());


            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
        }};

    }

    @Override
    public String comments() {
        return "Ищет возвращение цены к ЕМА26 на восходящем тренде первого экрана";
    }
}
