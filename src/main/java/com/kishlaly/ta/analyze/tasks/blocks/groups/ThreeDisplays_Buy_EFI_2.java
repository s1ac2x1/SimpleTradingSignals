package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplaysJava;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * The first screen is not used.
 * On the second, three EFI values are checked:
 * + the third and second from the end are negative and rising
 * + the last one is higher and positive
 * <p>
 * SL sliding on the average Bollinger band or TP at the top of the channel, if the last quote is not very high
 * <p>
 * !!! Can't roll over for shorts !!!
 */
//TODO finish
public class ThreeDisplays_Buy_EFI_2 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplaysJava.Config.STOCH_CUSTOM = 30;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidation());

        }};

    }

    @Override
    public String comments() {
        return "EFI rose smoothly and consolidated above zero";
    }
}