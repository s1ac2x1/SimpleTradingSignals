package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenSoftValidation;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_FirstTrustModelMainLogic;

import java.util.ArrayList;
import java.util.List;

public class FirstTrustModel_Buy_1 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenSoftValidation());
            add(new Long_ScreenTwo_FirstTrustModelMainLogic());
        }};
    }
}
