package com.kishlaly.ta.cache

import com.google.gson.Gson
import com.kishlaly.ta.analyze.TaskTypeJava
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.ContextJava
import java.io.File
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
        var requests = ConcurrentLinkedDeque<LoadRequestJava>()
        var callsInProgress = CopyOnWriteArrayList<Future<*>>()

        fun checkCache(timeframes: Array<Array<Timeframe>>, tasks: Array<TaskTypeJava>) {
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
        }

        fun removeCachedQuotesSymbols(src: Set<String>): List<String> {
            return src.filter { symbol: String ->
                val file = File("${getFolder()}${ContextJava.fileSeparator}${symbol}_quotes.txt")
                !file.exists()
            }.toList()
        }

        fun getFolder(): String {
            return "${Context.outputFolder}${Context.fileSeparator}cache${Context.fileSeparator}${ContextJava.aggregationTimeframe.name.lowercase(Locale.getDefault())}"
        }


    }

}