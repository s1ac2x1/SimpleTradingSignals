package com.kishlaly.ta.cache

import com.google.common.collect.Lists
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.ContextJava
import java.io.File
import java.util.concurrent.atomic.AtomicReference

class CacheBuilder {

    fun buildCache(timeframes: Array<Array<Timeframe>>, reloadMissed: Boolean = false) {
        val directory = File("${Context.outputFolder}${Context.fileSeparator}cache")
        if (!directory.exists()) {
            directory.mkdir()
        }
        val symbols = AtomicReference(Context.symbols)
        timeframes.forEach { screens ->
            // only one timeframe is loaded Context.aggregationTimeframe
            Context.timeframe = Context.aggregationTimeframe
            if (reloadMissed) {
                symbols.set(CacheReader.getMissedSymbols())
            }
            cacheQuotes(symbols.get())
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
                        println("Already in the queue: ${chunk.size} ${ContextJava.timeframe.name} QUOTE")
                    } ?: run {
                        CacheReader.requests.offer(LoadRequest(CacheType.QUOTE, Context.timeframe, chunk))
                    }
                }
    }

}