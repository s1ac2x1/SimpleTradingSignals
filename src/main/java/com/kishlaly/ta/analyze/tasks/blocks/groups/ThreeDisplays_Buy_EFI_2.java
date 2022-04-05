package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Первый экран не используется
 * На втором проверяются три значения EFI:
 * + третье и второе отрицательные и растут
 * + последняя еще выше и положительная
 * <p>
 * SL скользящий по средней ленте Боллинжера или TP у вершины канала, если последняя котировка не очень высокая
 * <p>
 * !!! Нельзя перевернуть для шортов !!!
 */
public class ThreeDisplays_Buy_EFI_2 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 30;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

        }};

    }

    @Override
    public String comments() {
        return "Вторая стратегия по значениям EFI";
    }
}