package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.ThreeDisplays;
import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * The first screen is not used.
 * The second one checks the three EFI values:
 * + all three last values are negative
 * + the second from the end value is lower than the third from the end (the lower point of the figure U)
 * + the last value is higher than the second from the end (the right point of the figure U)
 * <p>
 * SL sliding on the average Bollinger band or TP at the top of the channel, if the last quote is not very high
 * <p>
 * Note: Can't flip for shorts
 */
//TODO implement
public class ThreeDisplays_Buy_EFI_3 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        ThreeDisplays.Config.STOCH_CUSTOM = 30;

        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidation());

        }};

    }

    @Override
    public String comments() {
        return "EFI draws U below zero";
    }
}