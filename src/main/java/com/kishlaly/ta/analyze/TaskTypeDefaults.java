package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.groups.BullishDivergence_Buy_1;
import com.kishlaly.ta.analyze.tasks.blocks.groups.FirstTrustModel_Buy_1;
import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_1;
import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Sell_1;

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
                return new ThreeDisplays_Buy_1().blocks();
            case THREE_DISPLAYS_SELL:
                return new ThreeDisplays_Sell_1().blocks();
            case MACD_BULLISH_DIVERGENCE:
                return new BullishDivergence_Buy_1().blocks();
            case FIRST_TRUST_MODEL:
                return new FirstTrustModel_Buy_1().blocks();
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

}
