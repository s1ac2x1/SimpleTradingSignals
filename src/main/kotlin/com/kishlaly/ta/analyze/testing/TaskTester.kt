package com.kishlaly.ta.analyze.testing

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.cache.CacheReader
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.*
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.FileUtils
import com.kishlaly.ta.utils.Quotes
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
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
                        data.forEach { log.append("    $it").append(System.lineSeparator()) }
                        log.append(System.lineSeparator())
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
                            .append(System.lineSeparator())
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
            testLog.append(historicalTesting.symbol + System.lineSeparator())
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

    }

}