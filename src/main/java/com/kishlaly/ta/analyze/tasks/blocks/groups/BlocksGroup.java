package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;

import java.util.List;

public interface BlocksGroup {

    List<TaskBlock> blocks();

    String comments();

}
