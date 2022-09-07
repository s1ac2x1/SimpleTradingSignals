package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * The first screen is not used
 * The second screen checks the three EFI values:
 * + third from the end is negative
 * + penultimate and last are positive
 * <p>
 * SL sliding on the average Bollinger band or TP at the top of the channel, if the last quote is not very high
 * <p>
 * !!! Can't flip for shorts !!!
 */
//TODO finish
public class ThreeDisplays_Buy_EFI_1 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 30;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidation());

        }};

    }

    @Override
    public String comments() {
        return "EFI rose above zero and two values are positive";
    }
}