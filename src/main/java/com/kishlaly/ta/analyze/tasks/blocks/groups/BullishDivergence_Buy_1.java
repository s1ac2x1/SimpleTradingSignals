package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidationJava;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_StrictTrendCheckJava;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_BullishDivergenceMainLogicJava;

import java.util.ArrayList;
import java.util.List;

import static com.kishlaly.ta.analyze.tasks.DivergenciesJava.BullishConfig.ALLOW_ON_BEARISH_TREND;

public class BullishDivergence_Buy_1 implements BlocksGroupJava {

    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidationJava());
            if (!ALLOW_ON_BEARISH_TREND) {
                add(new Long_ScreenOne_StrictTrendCheckJava());
            }

            add(new Long_ScreenTwo_BullishDivergenceMainLogicJava());
        }};
    }

    @Override
    public String comments() {
        return "Not stable. Pls fix me";
    }

}
