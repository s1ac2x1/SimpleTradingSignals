package com.kishlaly.ta.cache

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.*
import com.kishlaly.ta.model.indicators.Indicator
import com.kishlaly.ta.utils.Dates
import com.kishlaly.ta.utils.IndicatorUtils
import com.kishlaly.ta.utils.Quotes
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

class CacheReader {

    companion object {
        var gson = Gson()
        var queueExecutor = Executors.newScheduledThreadPool(1)
        var apiExecutor = Executors.newCachedThreadPool()
        var requestPeriod = 0
        var requests = ConcurrentLinkedDeque<LoadRequest>()
        var callsInProgress = CopyOnWriteArrayList<Future<*>>()

        fun checkCache(timeframes: Array<Array<Timeframe>>, tasks: Array<TaskType>) {
            val screenNumber = AtomicInteger(0)
            val missedData = mutableMapOf<Timeframe, MutableSet<String>>()
            timeframes.forEach { screens ->
                // only check the availability of quotes in the cache
                // it is assumed that only one Context.aggregationTimeframe is loaded
                screenNumber.getAndIncrement()
                Context.timeframe = Context.aggregationTimeframe
                val missingQuotes = removeCachedQuotesSymbols(Context.symbols)
                val missingQuotesCollectedByScreen1 = missedData[screens[0]]
                missedData.putIfAbsent(screens[0], mutableSetOf())
                missingQuotesCollectedByScreen1?.addAll(missingQuotes)
                missedData[Context.timeframe] = missingQuotesCollectedByScreen1!!
            }
            missedData.forEach { tf, quotes ->
                Context.timeframe = tf
                try {
                    Files.write(
                        Paths.get("${getFolder()}${Context.fileSeparator}missed.txt"),
                        quotes.joinToString { System.lineSeparator() }.toByteArray()
                    )
                    println("Logged ${quotes.size} missed ${tf.name} quotes")
                } catch (e: IOException) {
                    println("Couldn't log missed quotes")
                }
            }
        }

        fun getSymbols(): Set<String> {
            val collector = mutableListOf<String>()
            return if (!Context.testOnly.isEmpty()) {
                collector.addAll(Context.testOnly)
                collector.toSet()
            } else {
                Context.source.forEach { source ->
                    var lines = File("${Context.outputFolder}/${source.filename}").readLines()
                    if (source.random) {
                        lines = lines.shuffled().subList(0, 30)
                    }
                    collector.addAll(lines)
                }
                collector.toSet()
            }
        }

        fun getMissedSymbols(): Set<String> {
            return File("${getFolder()}${Context.fileSeparator}missed.txt").readLines().toSet()
        }

        fun removeCachedIndicatorSymbols(src: Set<String>, indicator: Indicator): List<String> {
            return src.filter { symbol ->
                File("${getFolder()}${Context.fileSeparator}${symbol}${indicator.name.lowercase()}.txt").exists()
            }.toList()
        }

        fun removeCachedQuotesSymbols(src: Set<String>): List<String> {
            return src.filter { symbol: String ->
                val file = File("${getFolder()}${Context.fileSeparator}${symbol}_quotes.txt")
                !file.exists()
            }.toList()
        }

        fun loadQuotesFromDiskCache(symbol: String): List<Quote> {
            val cachedQuotes = QuotesInMemoryCache[symbol, Context.timeframe]
            return if (!cachedQuotes.isEmpty()) {
                cachedQuotes
            } else {
                try {
                    var quotes = gson.fromJson<List<Quote>>(
                        File("${getFolder()}${Context.fileSeparator}${symbol}_quotes.txt").readText(),
                        object : TypeToken<List<Quote>>() {}.type
                    )
                    when (Context.aggregationTimeframe) {
                        Timeframe.DAY -> {
                            if (Context.timeframe == Timeframe.WEEK) {
                                quotes = Quotes.dayToWeek(quotes)
                            }
                            if (Context.timeframe == Timeframe.HOUR) {
                                throw RuntimeException("Requested HOUR quotes, but aggregationTimeframe = DAY")
                            }
                        }
                        Timeframe.HOUR -> {
                            if (Context.timeframe == Timeframe.WEEK) {
                                quotes = Quotes.hourToDay(quotes)
                                quotes = Quotes.dayToWeek(quotes)
                            }
                            if (Context.timeframe == Timeframe.DAY) {
                                quotes = Quotes.hourToDay(quotes)
                            }
                        }
                        else -> {}
                    }
                    quotes = quotes.filter { it.valuesPresent() }.sortedBy { it.timestamp }
                    Context.trimToDate?.let {
                        val filterAfter = Dates.shortDateToZoned(it)
                        quotes = quotes.filter { it.timestamp <= filterAfter.toEpochSecond() }
                    }
                    QuotesInMemoryCache.put(symbol, Context.timeframe, quotes)
                    quotes
                } catch (e: Exception) {
                    emptyList<Quote>()
                }
            }
        }

        fun calculateIndicatorFromCachedQuotes(symbol: String, indicator: Indicator): List<out AbstractModel> {
            val quotes = loadQuotesFromDiskCache(symbol)
            return when (indicator) {
                Indicator.MACD -> IndicatorUtils.buildMACDHistogram(symbol, quotes)
                Indicator.EMA13 -> IndicatorUtils.buildEMA(symbol, quotes, 13)
                Indicator.EMA26 -> IndicatorUtils.buildEMA(symbol, quotes, 26)
                Indicator.STOCH -> IndicatorUtils.buildStochastic(symbol, quotes)
                Indicator.KELTNER -> IndicatorUtils.buildKeltnerChannels(symbol, quotes)
                Indicator.BOLLINGER -> IndicatorUtils.buildBollingerBands(symbol, quotes)
                Indicator.EFI -> IndicatorUtils.buildEFI(symbol, quotes)
            }
        }

        fun getSymbolData(timeframeIndicators: TimeframeIndicators, symbol: String): SymbolData {
            Context.timeframe = timeframeIndicators.timeframe
            val screen = SymbolData(symbol, timeframeIndicators.timeframe, loadQuotesFromDiskCache(symbol))
            timeframeIndicators.indicators.forEach { indicator ->
                val data = calculateIndicatorFromCachedQuotes(symbol, indicator)
                screen.indicators.put(indicator, data)
            }
            return screen
        }

        fun getFolder(): String {
            return "${Context.outputFolder}${Context.fileSeparator}cache${Context.fileSeparator}${
                Context.aggregationTimeframe.name.lowercase(
                    Locale.getDefault()
                )
            }"
        }

        fun clearCacheFolder(name: String) {
            try {
                Files.walk(Paths.get(name))
                    .sorted(Comparator.reverseOrder())
                    .map { obj: Path -> obj.toFile() }
                    .forEach { obj: File -> obj.delete() }
            } catch (e: IOException) {
                println(e.message)
            }
        }

    }

}