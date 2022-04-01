package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Short_ScreenOne_StrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

public class ThreeDisplays_Sell_1 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());
            add(new Short_ScreenOne_StrictTrendCheck());

            add(new Short_ScreenTwo_MACD_ThreeAboveZeroAndDescending());
            add(new Short_ScreenTwo_Stoch_D_ThreeDescending());
            add(new Short_ScreenTwo_Stoch_D_K_ThreeDescendingFromOverbought());
            add(new Short_ScreenTwo_EMA_ThreeBarsDescendingAndCrossing());
            add(new Short_ScreenTwo_EMA_LastBarTooLow());
        }};
    }

    @Override
    public String comments() {
        return "Первый шорт, наверно нестабилен";
    }
}
