package com.kishlaly.ta.analyze.testing

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.groups.BlocksGroup
import com.kishlaly.ta.cache.CacheReader.Companion.getSymbolData
import com.kishlaly.ta.cache.IndicatorsInMemoryCache
import com.kishlaly.ta.cache.QuotesInMemoryCache
import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.Screens
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.ContextJava
import com.kishlaly.ta.utils.IndicatorUtils
import com.kishlaly.ta.utils.Log
import com.kishlaly.ta.utils.Quotes
import com.kishlaly.ta.utils.Quotes.Companion.resolveMinBarsCount
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class TaskRunner {

    companion object {

        val signals = mutableListOf<Signal>()

        fun run(
            timeframes: Array<Array<Timeframe>>,
            task: TaskType,
            findOptimal: Boolean,
            vararg blocksGroups: BlocksGroup
        ) {
            timeframes.forEach { screens ->
                task.updateTimeframeForScreen(1, screens[0])
                task.updateTimeframeForScreen(2, screens[1])
                Context.logTimeframe1 = screens[0]
                Context.logTimeframe2 = screens[1]
                twoTimeframeFunction(task, *blocksGroups)
                println("\n")
                saveLog(task)
            }
        }

        private fun twoTimeframeFunction(task: TaskType, vararg blocksGroups: BlocksGroup) {
            Context.timeframe.set(task.getTimeframeIndicators(1).timeframe)
            val processingSymbol = AtomicInteger(1)
            val totalSymbols = Context.symbols.get().size
            Context.symbols.get().forEach { symbol ->
                val screen1 = getSymbolData(task.getTimeframeIndicators(1), symbol)
                val screen2 = getSymbolData(task.getTimeframeIndicators(2), symbol)

                // skip symbols if they have less than resolveMinBarsCount quotes on the weekly frame
                if (screen1.quotesCount < resolveMinBarsCount(screen1.timeframe)
                    || screen2.quotesCount < resolveMinBarsCount(screen2.timeframe)
                ) {
                    screen1.clear()
                    screen2.clear()
                    return
                }

                if (Context.TRIM_DATA) {
                    Quotes.trim(screen1)
                    Quotes.trim(screen2)
                    IndicatorUtils.trim(screen1)
                    IndicatorUtils.trim(screen2)
                }

                if (Context.lowPricesOnly && screen2.quote(screen2.quotesCount - 1).close > Context.lowPriceLimit) {
                    screen1.clear()
                    screen2.clear()
                    println("Skipped high price stock")
                    return
                }

                Log.addDebugLine("")
                Log.addDebugLine(" === $symbol === ")

                try {
                    blocksGroups.forEach { blocksGroup ->
                        println("[${processingSymbol.get()}/${totalSymbols}] Applying ${task.name} ${blocksGroup.javaClass.simpleName} on ${symbol} +  ...")
                        val blockResult = task.function.apply(Screens(screen1, screen2), blocksGroup.blocks())
                        Log.addDebugLine(if (blockResult.isOk()) "To check" else "Nope")
                        Log.addDebugLine("")
                        if (blockResult.isOk()) {
                            //TODO Here can be added a run on candlestick patterns
                            Log.addLine(symbol)
                            val signal = Signal(symbol, screen1.timeframe, screen2.timeframe, task)
                            signals.add(signal)
                            Log.addSummary(
                                task.name,
                                blocksGroup,
                                symbol
                            ) // TODO add a list of found candlestick patterns
                        }
                    }
                } catch (e: Exception) {
                    println("Function failed for symbol ${symbol} with message: ${e.message}")
                }
                processingSymbol.getAndIncrement()
                QuotesInMemoryCache.clear()
                IndicatorsInMemoryCache.clear()
                screen1.clear()
                screen2.clear()
            }
        }

        private fun saveLog(task: TaskType) {
            val d = File(Context.outputFolder + "/debug")
            if (!d.exists()) {
                d.mkdir()
            }
            val s = File(Context.outputFolder + "/signal")
            if (!s.exists()) {
                s.mkdir()
            }
            val prefix =
                "[${Context.logTimeframe1?.name}][${Context.logTimeframe2?.name}][${Context.source[0].name}]"
            val customDebugFolder =
                Context.outputFolder + "/debug/" + prefix + task.name.lowercase(Locale.getDefault())
            val d2 = File(customDebugFolder)
            if (!d2.exists()) {
                d2.mkdir()
            }
            //Log.saveSignal(Context.outputFolder + "/signal/" + prefix + task.name().toLowerCase() + ".txt");
            //Log.saveDebug(customDebugFolder + "/all.txt");
            //Log.saveCodes(customDebugFolder);
            Log.saveSummary(ContextJava.outputFolder + "/signal/" + prefix + task.name.lowercase(Locale.getDefault()) + ".html")
            Log.clear()
        }
    }

}

data class Signal(
    val symbol: String,
    val timeframe1: Timeframe,
    val timeframe2: Timeframe,
    val task: TaskType
)