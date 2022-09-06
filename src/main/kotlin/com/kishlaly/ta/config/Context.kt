package com.kishlaly.ta.config

import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy
import com.kishlaly.ta.model.ApiSource
import com.kishlaly.ta.model.SymbolsSourceJava
import com.kishlaly.ta.model.TimeframeJava

class Context {

    companion object {
        var accountBalance = 10000.0 // maximum position size

        var tradeCommission = 1 // %

        var basicTimeframes = arrayOf(arrayOf(TimeframeJava.WEEK, TimeframeJava.DAY))
        var outputFolder = "data"
        var api = ApiSource.ALPHAVANTAGE
        var parallelRequests = 10.0
        var limitPerMinute = 75.0
        var timeframe = TimeframeJava.DAY

        // main aggregation timeframe. Weekly quotes will be calculated from it
        var aggregationTimeframe = TimeframeJava.DAY
        var source = arrayOf(SymbolsSourceJava.SP500)
        var testOnly: List<String> = ArrayList()
        var symbols: Set<String>? = null
        var yearsToAnalyze = 5
        var lowPricesOnly = false
        var lowPriceLimit = 20

        // to convert to my timezone and compare with TradingView charts
        var myTimezone = "Europe/Berlin"

        var workingTime = 1440 // 1440 minutes in a day


        // If less, then skip this stock
        var minimumWeekBarsCount = 50
        var minimumDayBarsCount = 100
        var minimumHourBarsCount = 300

        // for logs
        var logTimeframe1: TimeframeJava? = null
        var logTimeframe2: TimeframeJava? = null
        var runGroups: TimeframeJava? = null

        // for testing on historical data
        var testMode = false
        var stopLossStrategy: StopLossStrategy = StopLossFixedPrice(0.27)
        var takeProfitStrategy: TakeProfitStrategy = TakeProfitFixedKeltnerTop(80)
        var massTesting = false
        var takeProfitStrategies: List<TakeProfitStrategy>? = null

        // misc
        var trendCheckIncludeHistogram = true
        var TRIM_DATA = true
        var trimToDate: String? = null
        var fileSeparator = System.getProperty("file.separator")

        const val TESTS_FOLDER = "tests"
        const val SINGLE_TXT = "single.txt"
        const val MASS_TXT = "mass.txt"
        const val MIN_POSSIBLE_QUOTES = 11

    }

}