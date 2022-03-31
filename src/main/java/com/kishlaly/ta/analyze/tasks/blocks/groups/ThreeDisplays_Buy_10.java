package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Возвращение цены к ЕМА на восходящем тренде
 * <p>
 * Первый экран - не учитывается?
 * Второй экран:
 * + было пересечение нижней лены Боллинжера одним из последних трех баров
 * + две гистограммы MACD ниже нуля и последняя выше
 * + две %D стохастика ниже 30 и последняя выше
 */
public class ThreeDisplays_Buy_10 implements BlocksGroup {
    @Override
    public List<TaskBlock> blocks() {
        ThreeDisplays.Config.BOLLINGER_TOTAL_BARS_CHECK = 3;
        ThreeDisplays.Config.BOLLINGER_CROSSED_BOTTOM_BARS = 1;

        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

        }};

    }
}
