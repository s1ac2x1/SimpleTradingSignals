package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneStrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskTypeDefaults {

    // все декартово произведение или один из его вариантов?
    private List<TaskBlock> customBlocks = new ArrayList<>();

    // загрузить блоки из списка, подготовленным findBestStrategyForSymbols, если есть
    // иначе если есть customVariants, то их
    // иначе дефолтные через switch
    public static List<TaskBlock> get(TaskType taskType) {
        switch (taskType) {
            case THREE_DISPLAYS_BUY:
                return new ArrayList<TaskBlock>() {{
                    add(new ScreenValidation());
                    add(new ScreenOneStrictTrendCheck());

                    add(new ScreenTwoMACDCheck3Bars());
                    add(new ScreenTwoStochAscending3Values());
                    add(new ScreenTwoStochOversold3Check());
                    add(new ScreenTwoLast3BarsEMACheck());
                    add(new ScreenTwoLastBarTooHighCheck());
                }};
            default:
                return Collections.emptyList();
        }
    }

    public List<TaskBlock> getCustomBlocks() {
        return this.customBlocks;
    }

    public void setCustomBlocks(final List<TaskBlock> customBlocks) {
        this.customBlocks = customBlocks;
    }

    public List<TaskBlock> threeDisplaysType2() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenValidation());
            add(new ScreenOneStrictTrendCheck());


        }};

    }

}
