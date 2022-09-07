package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlockJava;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Second screen:
 * + Intersection of the bottom Bollinger band (the color of the bar is not important)
 * <p>
 * entry 7 cents above the last bar
 * or TP at the middle of the channel
 */
//TODO finish
public class ThreeDisplays_Buy_Bollinger_4 implements BlocksGroupJava {
    @Override
    public List<TaskBlockJava> blocks() {
        return new ArrayList<TaskBlockJava>() {{
            add(new ScreenBasicValidation());

        }};

    }

    @Override
    public String comments() {
        return "Observe: the price has crossed the bottom band";
    }
}
