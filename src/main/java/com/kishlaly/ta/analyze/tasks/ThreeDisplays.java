package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.*;
import com.kishlaly.ta.analyze.tasks.blocks.two.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Индикаторы:
 * EMA26 (close) на первом экране
 * MACD (12 26 9 close) на втором экране
 * EMA13 (close) на втором экране
 * STOCH (14 1 3 close) на втором экране
 */
public class ThreeDisplays extends AbstractTask {

    public static class Config {

        // 3 дает меньше сигналов, но они надежнее
        public static int NUMBER_OF_EMA26_VALUES_TO_CHECK = 4;

        public static int STOCH_OVERSOLD = 40;
        public static int STOCH_OVERBOUGHT = 70;
        public static int STOCH_VALUES_TO_CHECK = 5;

        // в процентах от середины до вершины канала
        public static int FILTER_BY_KELTNER = 40;

        // фильтрация сигналов, если котировка закрылась выше FILTER_BY_KELTNER
        // тесты показывают результат лучше, когда эта проверка выключена
        public static boolean FILTER_BY_KELTNER_ENABLED;
    }

    public static class Default {

        public static List<TaskBlock> buy() {
            return new ArrayList<TaskBlock>() {{
                add(new ScreenBasicValidation());
                add(new Long_ScreenOne_StrictTrendCheck());

                add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
                add(new Long_ScreenTwo_Stoch_D_ThreeAscending());
                add(new Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromOversold());
                add(new Long_ScreenTwo_EMA_ThreeBarsAscendingAndCrossing());
                add(new Long_ScreenTwo_EMA_LastBarTooHigh());
            }};
        }

        public static List<TaskBlock> sell() {
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
    }

    public static class Type2 {

        public static List<TaskBlock> buy() {
            return new ArrayList<TaskBlock>() {{
                add(new ScreenBasicValidation());

                add(new Long_ScreenOne_StrictTrendCheck());

                add(new Long_ScreenTwo_MACD_TwoBelowZeroAndAscending());
                add(new Long_ScreenTwo_Stoch_D_K_SomeWereOversold());
                add(new Long_ScreenTwo_Stoch_D_LastAscending());
                add(new Long_ScreenTwo_EMA_TwoBarsAscendingAndCrossing());
                add(new Long_ScreenTwo_FilterLateEntry());
            }};
        }

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
    public static class Type3 {

        public static List<TaskBlock> buy() {
            return new ArrayList<TaskBlock>() {{
                add(new ScreenBasicValidation());

                add(new Long_ScreenOne_StrictTrendCheck());

                add(new Long_ScreenTwo_Stoch_D_K_ThreeAscendingFromStrongOversold());
                add(new Long_ScreenTwo_MACD_ThreeBelowZeroAndAscending());
                add(new Long_ScreenTwo_TwoBarsGreen());
                add(new Long_ScreenTwo_TwoBarsAscending());
                add(new Long_ScreenTwo_EMA_TwoBarsBelow());
            }};
        }

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
    //   TODO добавить проверку перепроданности?
    // ВНИМАНИЕ:
    // после сигнала проверить вручную, чтобы на втором экране послединй столбик не поднимался слишком высоко от ЕМА13
    public static class Type4 {

        public static List<TaskBlock> buy() {
            return new ArrayList<TaskBlock>() {{
                add(new ScreenBasicValidation());

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

}
