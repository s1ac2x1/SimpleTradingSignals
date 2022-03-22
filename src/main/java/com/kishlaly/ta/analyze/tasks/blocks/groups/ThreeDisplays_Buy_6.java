package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;

import java.util.List;

/**
 * Первого экрана нету
 * Второй экран:
 * 1) последняя гистограмма MACD повысилась
 * 2) одно из двух значений %D меньше 20
 * 3) две последние котировки ниже ЕМА13
 * 4) последняя котировка - зеленая
 */
public class ThreeDisplays_Buy_6 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        return null;
    }
}
