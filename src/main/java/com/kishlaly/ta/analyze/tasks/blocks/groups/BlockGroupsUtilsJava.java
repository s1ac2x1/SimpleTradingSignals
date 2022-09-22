package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.TaskTypeJava;

public class BlockGroupsUtilsJava {

    public static BlocksGroupJava[] getAllGroups(TaskTypeJava taskType) {
        switch (taskType) {
            case THREE_DISPLAYS_BUY:
                // TODO use reflections
                return new BlocksGroupJava[]{
                        new ThreeDisplays_Buy_1(),
                        new ThreeDisplays_Buy_2(),
                        new ThreeDisplays_Buy_3(),
                        new ThreeDisplays_Buy_4(),
                        new ThreeDisplays_Buy_5(),
                        new ThreeDisplays_Buy_6(),
                        new ThreeDisplays_Buy_7(),
                        new ThreeDisplays_Buy_8(),
                        new ThreeDisplays_Buy_9(),
                        new FirstScreen_Buy_1Java(),
                        new ThreeDisplays_Buy_Bollinger_1(),
                        new ThreeDisplays_Buy_Bollinger_1_2(),
                        new ThreeDisplays_Buy_Bollinger_2(),
                        new ThreeDisplays_Buy_Bollinger_3(),
                        new ThreeDisplays_Buy_Bollinger_4(),
                        new ThreeDisplays_Buy_Experiments(),
                        new ThreeDisplays_Buy_EFI_1(),
                        new ThreeDisplays_Buy_EFI_2(),
                        new ThreeDisplays_Buy_EFI_3(),
                };
            default:
                return new BlocksGroupJava[]{};
        }
    }

}
