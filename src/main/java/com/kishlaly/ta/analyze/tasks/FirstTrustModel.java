package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.model.BlockResult;
import com.kishlaly.ta.model.Screens;

import java.util.List;

public class FirstTrustModel extends AbstractTask {

    public static class Config {
        public static int MONTHS = 3;
    }

    public static BlockResult buy(Screens screens, List<TaskBlock> blocks) {
        return check(screens, blocks);
    }

}
