package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Второй экран:
 * + три последних значений верхней полосы уменьшаются
 *
 * Пример: [D] AAPL 15.11.2021, 7.06.2021
 *
 * вход в сделку на 7 центов выше последнего столбика (который красный)
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
        return "Наблюдать: цена пересекла нижнюю ленту красным столбиком";
    }
}
