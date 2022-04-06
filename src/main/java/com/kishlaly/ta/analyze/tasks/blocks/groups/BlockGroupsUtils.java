package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.TaskType;

public class BlockGroupsUtils {

    public static BlocksGroup[] getAllGroups(TaskType taskType) {
        switch (taskType) {
            case THREE_DISPLAYS_BUY:
                return new BlocksGroup[]{
                        new ThreeDisplays_Buy_1(),
                        new ThreeDisplays_Buy_2(),
                        new ThreeDisplays_Buy_3(),
                        new ThreeDisplays_Buy_4(),
                        new ThreeDisplays_Buy_5(),
                        new ThreeDisplays_Buy_6(),
                        new ThreeDisplays_Buy_7(),
                        new ThreeDisplays_Buy_8(),
                        new ThreeDisplays_Buy_9(),
                        new ThreeDisplays_Buy_Bollinger_1(),
                        new ThreeDisplays_Buy_Bollinger_1_2(),
                        new ThreeDisplays_Buy_Bollinger_3(),
                        new ThreeDisplays_Buy_Experiments(),
                        new ThreeDisplays_Buy_EFI_1(),
                        new ThreeDisplays_Buy_EFI_2(),
                };
            default:
                return new BlocksGroup[]{};
        }
    }

}
