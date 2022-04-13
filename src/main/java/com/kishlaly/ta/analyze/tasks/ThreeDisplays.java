package com.kishlaly.ta.analyze.tasks;

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

        // плавающие значения индикаторов
        public static int STOCH_CUSTOM = -1;
        public static int EMA26_TOTAL_BARS_CHECK = -1;
        public static int EMA26_ABOVE_BARS = -1;
        public static int BOLLINGER_TOTAL_BARS_CHECK = -1;
        public static int BOLLINGER_CROSSED_BOTTOM_BARS = -1;
        public static int QUOTE_FROM_END_TO_USE = -1;
    }

}
