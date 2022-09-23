package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.groups.BullishDivergence_Buy_1Java;
import com.kishlaly.ta.analyze.tasks.blocks.groups.FirstTrustModel_Buy_1Java;
import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Buy_1Java;
import com.kishlaly.ta.analyze.tasks.blocks.groups.ThreeDisplays_Sell_1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskTypeDefaults {

    // TODO the whole Cartesian product or one of its variants?
    private List<TaskBlockJava> customBlocks = new ArrayList<>();

    // load blocks from the list prepared by findBestStrategyForSymbols, if any
    public static List<TaskBlockJava> get(TaskTypeJava taskType) {
        switch (taskType) {
            case THREE_DISPLAYS_BUY:
                return new ThreeDisplays_Buy_1Java().blocks();
            case THREE_DISPLAYS_SELL:
                return new ThreeDisplays_Sell_1().blocks();
            case MACD_BULLISH_DIVERGENCE:
                return new BullishDivergence_Buy_1Java().blocks();
            case FIRST_TRUST_MODEL:
                return new FirstTrustModel_Buy_1Java().blocks();
            default:
                return Collections.emptyList();
        }
    }

    public List<TaskBlockJava> getCustomBlocks() {
        return this.customBlocks;
    }

    public void setCustomBlocks(final List<TaskBlockJava> customBlocks) {
        this.customBlocks = customBlocks;
    }

}
