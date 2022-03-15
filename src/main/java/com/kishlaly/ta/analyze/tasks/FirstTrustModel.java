package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenSoftValidation;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_FirstTrustModelMainLogic;

import java.util.ArrayList;
import java.util.List;

/**
 * Описана в книжке Булковского по свинговой торговле
 */
public class FirstTrustModel extends AbstractTask {

    public static class Config {
        public static int MONTHS = 3;
    }

    public static class Default {
        public static List<TaskBlock> blocks() {
            return new ArrayList<TaskBlock>() {{
                add(new ScreenSoftValidation());
                add(new Long_ScreenTwo_FirstTrustModelMainLogic());
            }};
        }
    }

}
