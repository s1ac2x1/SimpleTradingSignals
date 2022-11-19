package com.kishlaly.ta.analyze.tasks

class ThreeDisplays : AbstractTask() {

    object Config {
        // 3 gives fewer signals, but they are more reliable
        var NUMBER_OF_EMA26_VALUES_TO_CHECK = 4
        var STOCH_OVERSOLD = 40
        var STOCH_OVERBOUGHT = 70
        var STOCH_VALUES_TO_CHECK = 5

        // in percent from the middle to the top of the channel
        var FILTER_BY_KELTNER = 50

        // filtering signals if a quote closed above FILTER_BY_KELTNER
        // tests show better results when this check is turned off
        var FILTER_BY_KELTNER_ENABLED = true

        // configurable indicator values
        var STOCH_CUSTOM = 30
        var EMA26_TOTAL_BARS_CHECK = 7
        var EMA26_ABOVE_BARS = 4
        var BOLLINGER_TOTAL_BARS_CHECK = 3
        var BOLLINGER_CROSSED_BOTTOM_BARS = 1
        var QUOTE_FROM_END_TO_USE = 2
    }

}