package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.ScreenOneStrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskTypeDefaults {

    // все декартово произведение или один из его вариантов?
    private List<TaskBlock> customBlocks = new ArrayList<>();

    // загрузить блоки из списка, подготовленным findBestStrategyForSymbols, если есть
    // иначе если есть customVariants, то их
    // иначе дефолтные через switch
    public static List<TaskBlock> get(TaskType taskType) {
        switch (taskType) {
            case THREE_DISPLAYS_BUY:
                return new ArrayList<TaskBlock>() {{
                    add(new ScreenValidation());
                    add(new ScreenOneStrictTrendCheck());

                    add(new Long_ScreenTwo_MACD_ThreeBarsBelowZeroAndAscending());
                    add(new Long_ScreenTwo_Stoch_ThreeValuesAscending());
                    add(new Long_ScreenTwo_Stoch_ThreeValuesAscendingFromOversold());
                    add(new Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing());
                    add(new Long_ScreenTwo_EMA_LastBarTooHigh());
                }};
            default:
                return Collections.emptyList();
        }
    }

    public List<TaskBlock> getCustomBlocks() {
        return this.customBlocks;
    }

    public void setCustomBlocks(final List<TaskBlock> customBlocks) {
        this.customBlocks = customBlocks;
    }

    public List<TaskBlock> threeDisplaysType2() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenValidation());
            add(new ScreenOneStrictTrendCheck());

            add(new Long_ScreenTwo_MACD_TwoBarsBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_TwoValuesAscending());
            add(new Long_ScreenTwo_Stoch_SomeValuesWereUnderOversold());
            add(new Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

    // проверить buy стратегию (вдохновитель [D] CFLT 20 Dec 2021)
    // первый экран - подумать TODO
    // второй экран -
    //    перепроданность ниже 20 у трех значений медленной линии стохастика и она повышается
    //    последние три столбика гистограммы повышаются
    //    два из трех последних баров зеленые
    //    последние два бара повышаются (quote.low & quote.high)
    //    последние два бара полностью ниже ЕМА13
    // вход на 7 центов выше закрытия последнего бара
    // TP на середине верхней половины канала Кельтнера
    public List<TaskBlock> threeDisplaysType3() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenValidation());
            add(new ScreenOneStrictTrendCheck());

            add(new Long_ScreenTwo_Stoch_ThreeValuesAscendingFromStrongOversold());
            add(new Long_ScreenTwo_MACD_ThreeBarsBelowZeroAndAscending());
            add(new Long_ScreenTwo_TwoGreenQuotes());
        }};
    }

}
