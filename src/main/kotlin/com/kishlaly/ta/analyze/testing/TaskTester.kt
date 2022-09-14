package com.kishlaly.ta.analyze.testing

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.cache.IndicatorsInMemoryCache
import com.kishlaly.ta.cache.QuotesInMemoryCache
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.*
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.Dates
import com.kishlaly.ta.utils.FileUtils
import com.kishlaly.ta.utils.Quotes
import com.kishlaly.ta.utils.round
import java.io.File
import java.io.IOException
import java.lang.System.lineSeparator
import java.nio.file.Files
import java.nio.file.Paths
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class TaskTester {

    companion object {

        private val testLog = StringBuilder()

        fun test(
            timeframes: Array<Array<Timeframe>>,
            task: TaskType,
            blocksGroup: BlocksGroup
        ): List<HistoricalTesting> {
            Context.testMode = true
            val log = StringBuilder()
            val allTests = mutableListOf<HistoricalTesting>()
            timeframes.forEach { screens ->
                task.updateTimeframeForScreen(1, screens[0])
                task.updateTimeframeForScreen(2, screens[1])
                val readableOutput = mutableMapOf<String, MutableSet<String>>()
                val currSymbol = AtomicInteger(1)
                val totalSymbols = Context.symbols.size
                Context.symbols.forEach { symbol ->
                    println("[$currSymbol/$totalSymbols] Testing $symbol")
                    currSymbol.getAndIncrement()
                    val screen1 = CacheReader.getSymbolData(task.getTimeframeIndicators(1), symbol)
                    val screen2 = CacheReader.getSymbolData(task.getTimeframeIndicators(2), symbol)
                    val symbolDataForTesting = CacheReader.getSymbolData(task.getTimeframeIndicators(2), symbol)
                    val blockResults = mutableListOf<BlockResult>()
                    if (!isDataFilled(screen1, screen2)) {
                        emptyList<HistoricalTesting>()
                    }
                    try {
                        while (hasHistory(screen1, screen2)) {
                            rewind(task, blocksGroup, screen1, screen2, blockResults)
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                    if (!blockResults.isEmpty()) {
                        var testing: HistoricalTesting? = null
                        if (Context.massTesting) {
                            if (Context.takeProfitStrategies != null) {
                                Context.takeProfitStrategies.forEach { takeProfitStrategy ->
                                    val massTesting = HistoricalTesting(
                                        task,
                                        blocksGroup,
                                        symbolDataForTesting,
                                        blockResults,
                                        Context.stopLossStrategy,
                                        takeProfitStrategy
                                    )
                                    calculateStatistics(massTesting)
                                    allTests.add(massTesting)
                                }
                            }
                        } else {
                            testing = HistoricalTesting(
                                task,
                                blocksGroup,
                                symbolDataForTesting,
                                blockResults,
                                Context.stopLossStrategy,
                                Context.takeProfitStrategy
                            )
                            calculateStatistics(testing)
                            allTests.add(testing)
                            val key = "[${screens[0].name}][${screens[1]}] ${task.name} - ${symbol}"
                            readableOutput.putIfAbsent(key, mutableSetOf())
                            val signalResults = readableOutput[key]
                            signalResults!!.add(formatTestingSummary(testing))

                            // at this stage HistoricalTesting contains tests of positions by signals
                            // as well as all failure results
                            // TaskResult.lastChartQuote can be null if the strategy did not have enough quotes for the test

                            // first print the item report
                            printPositionsReport(screen2.timeframe, testing, signalResults)

                            // then a log of all other quotes with the reason why the strategy failed
                            printNoSignalsReport(screen2.timeframe, testing, signalResults)

                            readableOutput[key] = signalResults

                        }
                    }

                    // hint for GC
                    clean(screen1, screen2)
                }
                if (!Context.massTesting) {
                    readableOutput.forEach { (key, data) ->
                        data.forEach { log.append("    $it").append(lineSeparator()) }
                        log.append(lineSeparator())
                    }
                }
            }
            val directory = File(Context.TESTS_FOLDER)
            if (!directory.exists()) {
                directory.mkdir()
            }
            if (!Context.massTesting) {
                try {
                    Files.write(
                        Paths.get(Context.TESTS_FOLDER + Context.fileSeparator + Context.SINGLE_TXT),
                        log.toString().toByteArray()
                    )
                } catch (e: IOException) {
                    println(e.message)
                }
            } else {
                val builder = StringBuilder()
                allTests.forEach { testing ->
                    if (Context.takeProfitStrategies != null) {
                        builder
                            .append("TP: ${testing.printTP()} => TP/SL = ${testing.printTPSLNumber()} (${testing.printTPSLPercent()}); balance = ${testing.balance}")
                            .append(lineSeparator())
                    }
                    try {
                        Files.write(
                            Paths.get(Context.TESTS_FOLDER + Context.fileSeparator + Context.MASS_TXT),
                            builder.toString().toByteArray()
                        )
                    } catch (e: IOException) {
                        println(e.message)
                    }
                }
            }
            return allTests
        }

        private fun calculateStatistics(historicalTesting: HistoricalTesting) {
            historicalTesting.blocksResults
                .filter { it.isOk() }
                .forEach { testPosition(it, historicalTesting) }
            testLog.append(historicalTesting.symbol + lineSeparator())
            FileUtils.writeToFile(
                "${Context.outputFolder}${Context.fileSeparator}stats${Context.fileSeparator}${historicalTesting.symbol}_test_log.txt",
                TaskTester.testLog.toString()
            )
        }

        private fun rewind(
            task: TaskType,
            blocksGroup: BlocksGroup,
            screen1: SymbolData,
            screen2: SymbolData,
            blockResults: MutableList<BlockResult>
        ) {
            val lastScreen1Quote = screen1.quotes[screen1.quotes.size - 1]
            val lastScreen2Quote = screen2.quotes[screen2.quotes.size - 1]
            blockResults.add(task.function.apply(Screens(screen1, screen2), blocksGroup.blocks()))
            if (lastScreen2Quote.timestamp < lastScreen1Quote.timestamp) {
                rewind(screen1, 1)
            } else {
                rewind(screen2, 1)
            }
        }

        private fun rewind(screen: SymbolData, i: Int) {
            screen.quotes = screen.quotes.subList(0, screen.quotes.size - i)
            val indicators = mutableMapOf<Indicator, List<AbstractModel>>()
            screen.indicators.forEach { (indicator, data) ->
                indicators[indicator] = data.subList(0, data.size - i)
            }
            screen.indicators = indicators
        }

        private fun isDataFilled(screen1: SymbolData, screen2: SymbolData): Boolean {
            val filledData = AtomicBoolean(true)

            val screenOneMinBarCount = Quotes.resolveMinBarsCount(screen1.timeframe)
            val screenTwoMinBarCount = Quotes.resolveMinBarsCount(screen2.timeframe)

            if (screen1.quotes.size < screenOneMinBarCount || screen2.quotes.size < screenTwoMinBarCount) {
                filledData.set(false)
            }

            screen1.indicators.forEach { (_, data) ->
                if (data.size < screenOneMinBarCount) {
                    filledData.set(false)
                    return@forEach
                }
            }
            screen2.indicators.forEach { (indicator, data) ->
                if (data.size < screenTwoMinBarCount) {
                    filledData.set(false)
                    return@forEach
                }
            }

            return filledData.get()
        }

        private fun hasHistory(screen1: SymbolData, screen2: SymbolData): Boolean {
            val hasHistory = AtomicBoolean(true)
            if (screen1.quotes.isEmpty() || screen2.quotes.isEmpty()) {
                hasHistory.set(false)
            }
            screen1.indicators.forEach { (_, data) ->
                if (data.isEmpty()) {
                    hasHistory.set(false)
                    return@forEach
                }
            }
            screen2.indicators.forEach { (_, data) ->
                if (data.isEmpty()) {
                    hasHistory.set(false)
                    return@forEach
                }
            }
            return hasHistory.get()
        }

        fun formatTestingSummary(testing: HistoricalTesting): String {
            val timeframesInfo =
                "[${testing.taskType.getTimeframeForScreen(1)}][${testing.taskType.getTimeframeForScreen(2)}]"
            val result = StringBuilder()

            result
                .append(timeframesInfo).append(" ")
                .append(testing.data.symbol).append(" - ")
                .append(testing.taskType.name).append(" - ")
                .append(testing.blocksGroup.javaClass.getSimpleName()).append(lineSeparator())

            result
                .append("\ttrendCheckIncludeHistogram = ")
                .append(Context.trendCheckIncludeHistogram).append(lineSeparator())

            result
                .append("\teach trade size = $").append(Context.accountBalance).append(lineSeparator())

            result.append("\t${testing.printSL()}${lineSeparator()}")
            result.append("\t${testing.printTP()}${lineSeparator()}")
            result.append("\tTP/SL = ").append(testing.printTPSLNumber()).append(" = ")
            result.append(testing.printTPSLPercent()).append(lineSeparator())

            result
                .append("\tTotal profit after ").append(Context.tradeCommission)
                .append("% commissions per trade = ").append(testing.balance.round())
                .append(lineSeparator())

            result.append("\tTotal profit / loss = ${testing.totalProfit}")
                .append(" /${testing.totalLoss}").append(lineSeparator())

            val longestPositionRange = formatRange(testing) { it.searchSignalByLongestPosition() }
            when (testing.data.timeframe) {
                Timeframe.DAY -> {
                    result
                        .append("\tmin duration = ")
                        .append(TimeUnit.SECONDS.toDays(testing.minPositionDurationSeconds))
                        .append(" days").append(lineSeparator())
                        .append("\tmax duration = ")
                        .append(TimeUnit.SECONDS.toDays(testing.maxPositionDurationSeconds))
                        .append(" days ").append(longestPositionRange).append(lineSeparator())
                        .append("\tavg duration = ")
                        .append(TimeUnit.SECONDS.toDays(testing.averagePositionDurationSeconds.toLong()))
                        .append(" days").append(lineSeparator())
                }
                Timeframe.HOUR -> {
                    result
                        .append("\tmin duration = ")
                        .append(TimeUnit.SECONDS.toHours(testing.minPositionDurationSeconds))
                        .append(" hours").append(lineSeparator())
                        .append("\tmax duration = ")
                        .append(TimeUnit.SECONDS.toHours(testing.maxPositionDurationSeconds))
                        .append(" hours").append(lineSeparator()) // TODO сюда тоже диапазон
                        .append("\tavg duration = ")
                        .append(TimeUnit.SECONDS.toHours(testing.averagePositionDurationSeconds.toLong()))
                        .append(" hours").append(lineSeparator())

                }
                else -> {}
            }
            result.append(formatByTPSL(testing, testing.searchSignalByProfit(testing.minProfit), "\tmin profit = "))
            result.append(formatByTPSL(testing, testing.searchSignalByProfit(testing.maxProfit), "\tmax profit = "))
            result.append(formatByTPSL(testing, testing.searchSignalByProfit(testing.minLoss), "\tmin loss = "))
            result.append(formatByTPSL(testing, testing.searchSignalByProfit(testing.maxLoss), "\tmax loss = "))
            result.append("\tavg profit / loss = ${testing.avgProfit} / ${testing.avgLoss}").append(lineSeparator())

            return result.toString()
        }

        fun printPositionsReport(timeframe: Timeframe, testing: HistoricalTesting, report: MutableSet<String>) {
            testing.blocksResults
                .filter { it.lastChartQuote != null }
                .filter { it.isOk() }
                .forEach { taskResult ->
                    val line = StringBuilder()
                    val quote = taskResult.lastChartQuote
                    val quoteDateFormatted = formatDate(timeframe, quote.timestamp)
                    // результаты тестирования сигналов
                    val positionTestResult = testing.getResult(quote);
                    if (!positionTestResult!!.closed) {
                        line.append(" NOT CLOSED")
                    } else {
                        line.append(if (positionTestResult.profitable) "PROFIT " else "LOSS ")
                        line.append(positionTestResult.roi).append("%")
                        line.append(if (positionTestResult.gapUp) " (gap up)" else "")
                        line.append(if (positionTestResult.gapDown) " (gap down)" else "")
                        line.append(" ${positionTestResult.getPositionDuration(timeframe)}")
                        val endDate =
                            Dates.getBarTimeInMyZone(positionTestResult.closedTimestamp!!, exchangeTimezome).toString()
                        val parsed = ZonedDateTime.parse(endDate)
                        var parsedEndDate = parsed.dayOfMonth.toString() + " " + parsed.month + " " + parsed.year
                        if (timeframe == Timeframe.HOUR) {
                            parsedEndDate += " ${parsed.hour}:${parsed.minute}"
                        }
                        line.append(" [till ${parsedEndDate}]")
                    }
                    report.add(quoteDateFormatted + " --- " + line)
                }
        }

        fun printNoSignalsReport(
            timeframe: Timeframe,
            testing: HistoricalTesting,
            signalResults: MutableSet<String>
        ) {
            signalResults.add(lineSeparator())
            signalResults.add(lineSeparator())
            signalResults.add(lineSeparator())
            testing.blocksResults
                .filter { it.lastChartQuote != null }
                .filter { !it.isOk() }
                .forEach { taskResult ->
                    val quoteDateFormatted =
                        formatDate(timeframe, taskResult.lastChartQuote.timestamp)
                    signalResults.add("${quoteDateFormatted} ### ${taskResult.code}")
                }
        }

        private fun formatByTPSL(
            testing: HistoricalTesting,
            positionTestResult: PositionTestResult?,
            title: String
        ): String {
            val result = StringBuilder()
            positionTestResult?.let { value ->
                result
                    .append(title)
                    .append(value.roi)
                    .append("% ")
                    .append(formatRange(testing, value))
                    .append(lineSeparator())
            }
            return result.toString()
        }

        private fun formatRange(
            testing: HistoricalTesting,
            positionTestResult: PositionTestResult
        ): String {
            val output = StringBuilder()
            positionTestResult?.let {
                output
                    .append("[")
                    .append(
                        formatDate(
                            testing.data.timeframe,
                            positionTestResult.openedTimestamp!!
                        )
                    )
                    .append(" - ")
                    .append(
                        formatDate(
                            testing.data.timeframe,
                            positionTestResult.closedTimestamp!!
                        )
                    )
                    .append("]")
            }
            return output.toString()
        }

        private fun formatRange(
            testing: HistoricalTesting,
            function: (HistoricalTesting) -> PositionTestResult?
        ): String {
            return formatRange(testing, function(testing)!!)
        }

        private fun formatDate(timeframe: Timeframe, timestamp: Long): String {
            var date = Dates.getBarTimeInMyZone(timestamp, exchangeTimezome).toString()
            val parsedDate = ZonedDateTime.parse(date)
            date = "${parsedDate.dayOfMonth.toString()} ${parsedDate.month} ${parsedDate.year}"
            if (timeframe == Timeframe.HOUR) {
                date += " ${parsedDate.hour}:${parsedDate.minute}"
            }
            return date
        }

        private fun clean(screen1: SymbolData, screen2: SymbolData) {
            QuotesInMemoryCache.clear()
            IndicatorsInMemoryCache.clear()
            screen1.indicators.clear()
            screen2.indicators.clear()
        }

        private fun testPosition(blockResult: BlockResult, historicalTesting: HistoricalTesting) {

        }

    }

}