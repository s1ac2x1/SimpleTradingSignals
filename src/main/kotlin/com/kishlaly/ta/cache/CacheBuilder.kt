package com.kishlaly.ta.cache

import com.google.common.collect.Lists
import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.testing.HistoricalTesting
import com.kishlaly.ta.analyze.testing.sl.*
import com.kishlaly.ta.analyze.testing.tp.TakeProfitFixedKeltnerTop
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy
import com.kishlaly.ta.analyze.testing.tp.TakeProfitVolatileKeltnerTop
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.loaders.Alphavantage
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.FileUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class CacheBuilder {

    companion object {

        fun buildCache(timeframes: Array<Array<Timeframe>>, reloadMissed: Boolean = false) {
            val directory = File("${Context.outputFolder}${Context.fileSeparator}cache")
            if (!directory.exists()) {
                directory.mkdir()
            }
            val symbols = AtomicReference(Context.symbols)
            timeframes.forEach { _ ->
                // only one timeframe is loaded Context.aggregationTimeframe
                Context.timeframe = Context.aggregationTimeframe
                if (reloadMissed) {
                    symbols.set(CacheReader.getMissedSymbols())
                }
                cacheQuotes(symbols.get())
            }
            val p = Context.limitPerMinute / Context.parallelRequests
            CacheReader.requestPeriod = (p * 1000).toInt() + 1000 // +1 second for margin
            CacheReader.queueExecutor.scheduleAtFixedRate(
                { processQueue() },
                CacheReader.requestPeriod.toLong(),
                CacheReader.requestPeriod.toLong(),
                TimeUnit.MILLISECONDS
            )
            if (reloadMissed) {
                timeframes.forEach { screens ->
                    screens.forEach { screen ->
                        Context.timeframe = screen
                        val file = File(CacheReader.getFolder() + Context.fileSeparator + "missed.txt")
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                }
            }
        }

        private fun cacheQuotes(symbols: Set<String>) {
            val symbolsToCache = CacheReader.removeCachedQuotesSymbols(symbols)
            symbolsToCache.ifEmpty {
                println("${Context.timeframe.name} quotes already cached")
                return
            }
            Lists.partition(symbolsToCache, Context.parallelRequests.toInt())
                .forEach { chunk ->
                    val existingRequest = CacheReader.requests.filter { request ->
                        request.cacheType == CacheType.QUOTE
                                && request.timeframe == Context.timeframe
                                && request.symbols.containsAll(chunk)
                    }.firstOrNull()
                    existingRequest?.let {
                        println("Already in the queue: ${chunk.size} ${Context.timeframe.name} QUOTE")
                    } ?: run {
                        CacheReader.requests.offer(LoadRequest(CacheType.QUOTE, Context.timeframe, chunk))
                    }
                }
        }

        fun processQueue() {
            println("")
            val seconds = CacheReader.requests.size * CacheReader.requestPeriod / 1000
            val hours = seconds / 3600
            var remainderSeconds = seconds - hours * 3600
            val mins = remainderSeconds / 60
            remainderSeconds = remainderSeconds - mins * 60
            val secs = remainderSeconds
            println("$hours:$mins:$secs left...")
            if (CacheReader.requests.size == 0) {
                CacheReader.queueExecutor.shutdownNow()
            }
            // to run new requests to the API, if not all of the last batch has been completed
            for (i in CacheReader.callsInProgress.indices) {
                if (!CacheReader.callsInProgress[i].isDone) {
                    println("Previous batch is still in progress, skipping this round")
                    return
                }
            }
            CacheReader.requests.poll()?.let { request ->
                val symbols = request.symbols
                val timeframe = request.timeframe
                Context.timeframe = timeframe
                if (request.cacheType == CacheType.QUOTE) {
                    println("Loading ${timeframe.name} quotes...")
                    symbols.forEach { symbol ->
                        val future = CacheReader.apiExecutor.submit {
                            val quotes = Alphavantage.loadQuotes(symbol, timeframe)
                            if (!quotes.isEmpty()) {
                                saveQuote(symbol, quotes)
                            }
                        }
                        CacheReader.callsInProgress.add(future)
                    }
                }
            }
        }

        fun saveTable(result: List<HistoricalTesting>) {
            val table = StringBuilder("<table>")
            val groupBy = result
                .groupBy { it.symbol }
                .forEach { bySymbol ->
                    val symbol = bySymbol.key
                    table.append("<tr>")
                    table.append("<td style=\"vertical-align: left;\">$symbol</td>")
                    table.append("<td>")
                    val innerTable = StringBuilder("<table>")
                    val testings = bySymbol.value.sortedBy { it.balance }
                    testings.groupBy { it.blocksGroup }.entries
                        .forEach { byTask ->
                            val blocksGroup = byTask.key
                            val historicalTestings = byTask.value
                            val best = historicalTestings[historicalTestings.size - 1]
                            innerTable.append("<tr>")
                            innerTable.append("<td style=\"vertical-align: top text-align: left;\">${blocksGroup.javaClass.simpleName}</td>")
                            innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>")

                            innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">${best.printTPSLNumber()}</td>")
                            innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>")

                            innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">${best.printTPSLPercent()}</td>")
                            innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>")

                            innerTable.append("<td style=\"vertical-align: top text-align: left;\">${best.balance}</td>")
                            innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>")

                            innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">${best.stopLossStrategy}</td>")
                            innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>")

                            innerTable.append("<td style=\"vertical-align: top text-align: left; white-space: nowrap;\">${best.takeProfitStrategy}</td>")
                            innerTable.append("<td style=\"vertical-align: top text-align: center;\">&nbsp;&nbsp;&nbsp;</td>")

                            innerTable.append("</tr>")
                        }
                    innerTable.append("</table>")
                    table.append(innerTable)
                    table.append("</td>")
                    table.append("</tr>")
                }
            table.append("</table>")
            FileUtils.writeToFile("tests/table.html", table.toString())
        }

        fun buildTasksAndStrategiesSummary(
            timeframes: Array<Array<Timeframe>>,
            task: TaskType,
            blocksGroups: List<BlocksGroup>,
            stopLossStrategy: StopLossStrategy,
            takeProfitStrategy: TakeProfitStrategy
        ) {
            val result = mutableListOf<HistoricalTesting>()
            val total = getSLStrategies().size * getTPStrategies().size
            val current = AtomicInteger(1)
            if (stopLossStrategy == null || takeProfitStrategy == null) {
                getSLStrategies().forEach { sl ->
                    getTPStrategies().forEach { tp ->
                        Context.stopLossStrategy = sl
                        Context.takeProfitStrategy = tp
                        println("${current.get().toString()}/${total} ${sl} / ${tp}")
                        blocksGroups.forEach { group -> result.addAll(test(timeframes, task, group)) }
                        current.getAndIncrement()
                    }
                }
            } else {
                Context.stopLossStrategy = stopLossStrategy
                Context.takeProfitStrategy = takeProfitStrategy
                blocksGroups.forEach { group -> result.addAll(test(timeframes, task, group)) }
            }
            saveTable(result)
            saveSummaryPerGroup(result)
        }

        fun getSLStrategies() = listOf<StopLossStrategy>(
            StopLossFixedPrice(0.27),
            StopLossFixedKeltnerBottom(),
            StopLossVolatileKeltnerBottom(80),
            StopLossVolatileKeltnerBottom(100),
            StopLossVolatileLocalMin(0.27),
            StopLossVolatileATR()
        )

        fun getTPStrategies() = listOf<TakeProfitStrategy>(
            TakeProfitFixedKeltnerTop(80),
            TakeProfitFixedKeltnerTop(100),
            TakeProfitVolatileKeltnerTop(80),
            TakeProfitVolatileKeltnerTop(100)
        )

        private fun saveQuote(symbol: String, quotes: List<Quote>) {
            val folder = "${Context.outputFolder}/cache/{${Context.timeframe.name.lowercase()}}"
            val directory: File = File(folder)
            if (!directory.exists()) {
                directory.mkdir()
            }
            val zone = ZoneId.of(Context.myTimezone)
            val currentYear = LocalDateTime.ofInstant(Instant.now(), zone).year
            val filteredByHistory = quotes.filter {
                val quoteYear = LocalDateTime.ofInstant(Instant.ofEpochSecond(it.timestamp), zone).year
                currentYear - quoteYear <= Context.yearsToAnalyze
            }
            val json = CacheReader.gson.toJson(filteredByHistory)
            Files.write(Paths.get("${folder}/${symbol}_quotes.txt"), json.toByteArray())
        }

    }

}