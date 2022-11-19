package com.kishlaly.ta.config

import com.kishlaly.ta.analyze.testing.sl.StopLossFixedPrice
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy
import com.kishlaly.ta.model.ApiSource
import com.kishlaly.ta.model.SymbolsSource
import com.kishlaly.ta.model.Timeframe
import org.ktorm.database.Database

class Context {

    companion object {
        var accountBalance = 10000.0 // maximum position size

        var tradeCommission = 1 // %

        var basicTimeframes = ThreadLocal.withInitial { arrayOf(arrayOf(Timeframe.WEEK, Timeframe.DAY)) }
        var outputFolder = "data"
        var api = ApiSource.ALPHAVANTAGE
        var parallelRequests = 10.0
        var limitPerMinute = 75.0
        var timeframe = ThreadLocal.withInitial { Timeframe.DAY }

        // main aggregation timeframe. Weekly quotes will be calculated from it
        var aggregationTimeframe = ThreadLocal.withInitial { Timeframe.DAY }
        var source = arrayOf(SymbolsSource.SP500)
        var testOnly: List<String> = ArrayList()
        var symbols = ThreadLocal.withInitial { setOf<String>() }
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
        var logTimeframe1: ThreadLocal<Timeframe> = ThreadLocal.withInitial { null }
        var logTimeframe2: ThreadLocal<Timeframe> = ThreadLocal.withInitial { null }
        var runGroups: Timeframe? = null
        var useDBLogging: Boolean = false
        var database: Database? = null

        // for testing on historical data
        var testMode = false
        var stopLossStrategy: ThreadLocal<StopLossStrategy> = ThreadLocal.withInitial { StopLossFixedPrice(0.27) }
        var takeProfitStrategy: ThreadLocal<TakeProfitStrategy> =
            ThreadLocal.withInitial { TakeProfitFixedKeltnerTop(80) }
        var massTesting = false
        var takeProfitStrategies: MutableList<TakeProfitStrategy> = mutableListOf()

        // misc
        var trendCheckIncludeHistogram = true
        var TRIM_DATA = true
        var trimToDate: String? = null // format: dd.mm.yyyy
        var fileSeparator = System.getProperty("file.separator")

        const val TESTS_FOLDER = "tests"
        const val SINGLE_TXT = "single.txt"
        const val MASS_TXT = "mass.txt"
        const val MIN_POSSIBLE_QUOTES = 11

    }

}