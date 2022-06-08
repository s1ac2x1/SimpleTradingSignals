package com.kishlaly.ta.analyze.tasks;

/**
 * Named after Alex Elder's famous three-displays-technique
 * however, here is used only Longterm and Midterm
 * <p>
 * Indicators:
 * EMA26 (close) on the first screen
 * MACD (12 26 9 close) on the second screen
 * EMA13 (close) on the second screen
 * STOCH (14 1 3 close) on the second screen
 */
public class ThreeDisplays extends AbstractTask {

    public static class Config {

        // 3 gives fewer signals, but they are more reliable
        public static int NUMBER_OF_EMA26_VALUES_TO_CHECK = 4;

        public static int STOCH_OVERSOLD = 40;
        public static int STOCH_OVERBOUGHT = 70;
        public static int STOCH_VALUES_TO_CHECK = 5;

        // in percent from the middle to the top of the channel
        public static int FILTER_BY_KELTNER = 20;

        // filtering signals if a quote closed above FILTER_BY_KELTNER
        // tests show better results when this check is turned off
        public static boolean FILTER_BY_KELTNER_ENABLED = true;

        // configurable indicator values
        public static int STOCH_CUSTOM = -1;
        public static int EMA26_TOTAL_BARS_CHECK = -1;
        public static int EMA26_ABOVE_BARS = -1;
        public static int BOLLINGER_TOTAL_BARS_CHECK = -1;
        public static int BOLLINGER_CROSSED_BOTTOM_BARS = -1;
        public static int QUOTE_FROM_END_TO_USE = -1;
    }

}
