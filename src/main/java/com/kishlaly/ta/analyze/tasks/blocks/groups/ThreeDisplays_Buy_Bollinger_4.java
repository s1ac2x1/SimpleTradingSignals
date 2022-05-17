package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Второй экран:
 * + Пересечение нижней ленты Боллинжера (цвет столбика неважен)
 *
 * вход в сделку на 7 центов выше последнего столбика
 * или TP у середины канала
 */
//TODO закончить
public class ThreeDisplays_Buy_Bollinger_4 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

        }};

    }

    @Override
    public String comments() {
        return "Наблюдать: цена пересекла нижнюю ленту";
    }
}
