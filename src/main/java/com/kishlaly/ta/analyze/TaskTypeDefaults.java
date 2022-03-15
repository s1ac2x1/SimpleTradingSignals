package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.Divergencies;
import com.kishlaly.ta.analyze.tasks.FirstTrustModel;
import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;

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
                return ThreeDisplays.Default.buy();
            case THREE_DISPLAYS_SELL:
                return ThreeDisplays.Default.sell();
            case MACD_BULLISH_DIVERGENCE:
                return Divergencies.Default.buy();
            case FIRST_TRUST_MODEL:
                return FirstTrustModel.Default.blocks();
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
