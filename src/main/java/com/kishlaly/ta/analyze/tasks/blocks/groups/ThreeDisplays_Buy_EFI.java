package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Первый экран не используется
 * На втором проверяются три значения EFI:
 * + третье с конца отрицательное
 * + предпоследнее и последнии положительные
 * <p>
 * SL скользящий по средней ленте Боллинжера или TP у вершины канала, если последняя котировка не очень высокая
 * <p>
 * !!! Нельзя перевернуть для шортов !!!
 */
public class ThreeDisplays_Buy_EFI implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 30;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

        }};

    }

    @Override
    public String comments() {
        return "Простая стратегия по значениям EFI";
    }
}