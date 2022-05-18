package com.kishlaly.ta.analyze.tasks.blocks.groups;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_SoftTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.one.Long_ScreenOne_StrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * копия ThreeDisplays_Buy_2
 * добавлена Long_ScreenTwo_Stoch_D_ThreeFigureU
 * Long_ScreenTwo_MACD_TwoBelowZeroAndAscending заменена на Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureU
 * <p>
 * Лучше работает с TP 70% от канала
 */
public class ThreeDisplays_Buy_5 implements BlocksGroup {

    public List<TaskBlock> blocks() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenBasicValidation());

            // результат лучше без первого экрана
            //add(new Long_ScreenOne_StrictTrendCheck());
            add(new Long_ScreenOne_SoftTrendCheck());

            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndFigureU());
            add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversold());
            add(new Long_ScreenTwo_Stoch_D_ThreeFigureU());
            add(new Long_ScreenTwo_Stoch_D_LastAscending());
            add(new Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    @Override
    public String comments() {
        return "Отслеживает U развороты индикаторов. Терпимое отношение TP/SL";
    }

}
