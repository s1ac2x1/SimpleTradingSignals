package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.TaskTypeJava;

public class BlockGroupsUtilsJava {

    public static BlocksGroupJava[] getAllGroups(TaskTypeJava taskType) {
        switch (taskType) {
            case THREE_DISPLAYS_BUY:
                return new BlocksGroupJava[]{
                        new ThreeDisplays_Buy_1Java(),
                        new ThreeDisplays_Buy_2Java(),
                        new ThreeDisplays_Buy_3Java(),
                        new ThreeDisplays_Buy_4Java(),
                        new ThreeDisplays_Buy_5Java(),
                        new ThreeDisplays_Buy_6Java(),
                        new ThreeDisplays_Buy_7Java(),
                        new ThreeDisplays_Buy_8Java(),
                        new ThreeDisplays_Buy_9Java(),
                        new FirstScreen_Buy_1Java(),
                        new ThreeDisplays_Buy_Bollinger_1Java(),
                        new ThreeDisplays_Buy_Bollinger_1_2Java(),
                        new ThreeDisplays_Buy_Bollinger_2Java(),
                        new ThreeDisplays_Buy_Bollinger_3Java(),
                        new ThreeDisplays_Buy_Bollinger_4Java(),
                        new ThreeDisplays_Buy_ExperimentsJava(),
                        new ThreeDisplays_Buy_EFI_1Java(),
                        new ThreeDisplays_Buy_EFI_2Java(),
                        new ThreeDisplays_Buy_EFI_3Java(),
                };
            default:
                return new BlocksGroupJava[]{};
        }
    }

}
