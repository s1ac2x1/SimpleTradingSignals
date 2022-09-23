package com.kishlaly.ta.analyze.tasks.blocks.groups

import com.kishlaly.ta.analyze.TaskType

class BlockGroupsUtils {

    fun getAllGroups(taskType: TaskType): Array<BlocksGroup> {
        return when (taskType) {
            TaskType.THREE_DISPLAYS_BUY ->
                arrayOf(
                    ThreeDisplays_Buy_1Java(),
                    ThreeDisplays_Buy_2Java(),
                    ThreeDisplays_Buy_3Java(),
                    ThreeDisplays_Buy_4Java(),
                    ThreeDisplays_Buy_5Java(),
                    ThreeDisplays_Buy_6Java(),
                    ThreeDisplays_Buy_7Java(),
                    ThreeDisplays_Buy_8Java(),
                    ThreeDisplays_Buy_9Java(),
                    FirstScreen_Buy_1Java(),
                    ThreeDisplays_Buy_Bollinger_1(),
                    ThreeDisplays_Buy_Bollinger_1_2(),
                    ThreeDisplays_Buy_Bollinger_2(),
                    ThreeDisplays_Buy_Bollinger_3(),
                    ThreeDisplays_Buy_Bollinger_4(),
                    ThreeDisplays_Buy_Experiments(),
                    ThreeDisplays_Buy_EFI_1(),
                    ThreeDisplays_Buy_EFI_2(),
                    ThreeDisplays_Buy_EFI_3()
                )
            else -> arrayOf()
        }

    }

}