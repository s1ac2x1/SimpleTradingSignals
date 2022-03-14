package com.kishlaly.ta.analyze;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.*;
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
                    add(new Long_ScreenOne_StrictTrendCheck());

                    add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
                    add(new Long_ScreenTwo_Stoch_D_ThreeAscending());
                    add(new Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold());
                    add(new Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing());
                    add(new Long_ScreenTwo_EMA_LastBarTooHigh());
                }};
            case THREE_DISPLAYS_SELL:
                return new ArrayList<TaskBlock>() {{
                    add(new ScreenValidation());
                    add(new Short_ScreenOne_StrictTrendCheck());

                    add(new Short_ScreenTwo_MACD_ThreeAboveZeroAndDescending());
                    add(new Short_ScreenTwo_Stoch_D_ThreeDescending());
                    add(new Short_ScreenTwo_Stoch_D_K_ThreeDescendingFromOverbought());
                    add(new Short_ScreenTwo_EMA_ThreeBarsDescendingAndCrossing());
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

    public List<TaskBlock> threeDisplaysBuyType2() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenValidation());

            add(new Long_ScreenOne_StrictTrendCheck());

            add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
            add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversold());
            add(new Long_ScreenTwo_Stoch_D_LastAscending());
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
    public List<TaskBlock> threeDisplaysBuyType3() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenValidation());

            add(new Long_ScreenOne_StrictTrendCheck());

            add(new Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromStrongOversold());
            add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
            add(new Long_ScreenTwo_TwoBarsGreen());
            add(new Long_ScreenTwo_TwoBarsAscending());
            add(new Long_ScreenTwo_EMA_TwoBarsBelow());
        }};
    }

    // модификация buySignalType2 с попыткой остлеживания начала долгосрочного тренда
    // 1 экран: отслеживание начала движения выше ЕМА по двум столбикам
    //   последний столбик зеленый
    //   последний столбик выше предпоследнего
    //   последний столбик пересекает ЕМА26
    //   последняя гистограмма растет
    // 2 экран: смотреть на два последних столбика
    //   high последнего столбика выше предпоследнего
    //   последний столбик не выше ЕМА13
    //   последняя гистограмма растет
    //   %D и %K последнего стохастика должны быть выше, чем у предпоследнего
    // ВНИМАНИЕ:
    // после сигнала проверить вручную, чтобы на втором экране послединй столбик не поднимался слишком высоко от ЕМА13
    public List<TaskBlock> threeDisplaysBuyType4() {
        return new ArrayList<TaskBlock>() {{
            add(new ScreenValidation());

            add(new Long_ScreenOne_LastBarGreen());
            add(new Long_ScreenOne_LastBarHigher());
            add(new Long_ScreenOne_EMA_LastBarCrosses());
            add(new Long_ScreenOne_MACD_LastAscending());

            add(new Long_ScreenTwo_TwoBarsHighAscending());
            add(new Long_ScreenTwo_EMA_LastBarNotAbove());
            add(new Long_ScreenTwo_MACD_LastAscending());
            add(new Long_ScreenTwo_Stoch_D_K_LastAscending());
            add(new Long_ScreenTwo_FilterLateEntry());
        }};
    }

}
