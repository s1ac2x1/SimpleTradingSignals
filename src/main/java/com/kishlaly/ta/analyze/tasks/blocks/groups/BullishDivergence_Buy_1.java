package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_StrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_BullishDivergenceMainLogic;

import java.util.ArrayList;
import java.util.List;

import static com.kishlaly.ta.analyze.tasks.Divergencies.BullishConfig.ALLOW_ON_BEARISH_TREND;

public class BullishDivergence_Buy_1 implements BlocksGroupJava {

    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidation());
            if (!ALLOW_ON_BEARISH_TREND) {
                add(new Long_ScreenOne_StrictTrendCheck());
            }

            add(new Long_ScreenTwo_BullishDivergenceMainLogic());
        }};
    }

    @Override
    public String comments() {
        return "Not stable. Pls fix me";
    }

}
