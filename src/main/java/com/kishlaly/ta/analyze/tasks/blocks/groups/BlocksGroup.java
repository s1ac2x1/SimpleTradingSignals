package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;

import java.util.List;

public interface BlocksGroup {

    List<TaskBlockJava> blocks();

    String comments();

}
