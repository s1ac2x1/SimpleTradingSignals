package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenSoftValidation;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_FirstTrustModelMainLogic;
import com.kishlaly.ta.utils.ContextJava;

import java.util.ArrayList;
import java.util.List;

public class FirstTrustModel_Buy_1 implements BlocksGroup {

    public List<TaskBlockJava> blocks() {
        ContextJava.TRIM_DATA = false;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenSoftValidation());
            add(new Long_ScreenTwo_FirstTrustModelMainLogic());
        }};
    }

    @Override
    public String comments() {
        return "The First Trust Model";
    }
}
