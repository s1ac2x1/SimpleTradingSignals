package com.kishlaly.ta.analyze.testing

import com.kishlaly.ta.analyze.TaskType
import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.analyze.testing.sl.StopLossStrategy
import com.kishlaly.ta.analyze.testing.tp.TakeProfitStrategy
import com.kishlaly.ta.model.BlockResult
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.Numbers
import com.kishlaly.ta.utils.round

class HistoricalTesting(
    val taskType: TaskType,
    val blocksGroup: BlocksGroup,
    // indicator chart
    val data: SymbolData,
    // all results of the strategy run, scrolling the graph one bar back in time
    val blocksResults: List<BlockResult>,
    val stopLossStrategy: StopLossStrategy,
    val takeProfitStrategy: TakeProfitStrategy,
    val signalTestingResults: MutableMap<Quote, PositionTestResult> = mutableMapOf()
) {

    val symbol = data.symbol

    // loss is negative
    val balance = (totalProfit + totalLoss).round()

    val successfulRatio: Double
        get() {
            return if (allPositionsCount == 0) {
                0.0
            } else Numbers.percent(profitablePositionsCount.toDouble(), allPositionsCount.toDouble())
        }

    val lossRatio: Double
        get() {
            return if (allPositionsCount == 0) {
                0.0
            } else Numbers.percent(lossPositionsCount.toDouble(), allPositionsCount.toDouble())
        }

    val allPositionsCount = signalTestingResults.entries.filter { it.value.closed }.count()

    val profitablePositionsCount = signalTestingResults.entries.filter { it.value.profitable }.count()

    val lossPositionsCount = signalTestingResults.entries.filter { !it.value.profitable }.count()

    fun getMinPositionDurationSeconds() = positionDuration().min()

    fun getAveragePositionDurationSeconds() = positionDuration().average().round()

    fun getMaxPositionDurationSeconds() = positionDuration().max()

    fun getMinProfit() = profitsCollection().min()

    fun getAvgProfit() = profitsCollection().average().round()

    fun getMaxProfit() = profitsCollection().max()

    fun getMinLoss() = lossesCollection().min()

    fun getAvgLoss() = lossesCollection().average().round()

    fun getMaxLoss() = lossesCollection().max()

    val totalProfit: Double
        get() = signalTestingResults.entries
            .filter { it.value.profitable }
            .map { it.value.profit!! - it.value.commissions!! }
            .sum()

    val averageRoi: Double
        get() = signalTestingResults.entries
            .filter { it.value.profitable }
            .map { it.value.roi!! }
            .average().round()

    val totalLoss: Double
        get() = signalTestingResults.entries
            .filter { !it.value.profitable }
            .map { it.value.loss!! - it.value.commissions!! }
            .sum()

    fun addTestResult(signal: Quote, positionTestResult: PositionTestResult) {
        signalTestingResults.put(signal, positionTestResult)
    }

    fun getResult(signal: Quote) = signalTestingResults.get(signal)

    fun searchSignalByLongestPosition(): PositionTestResult? {
        val maxPositionDurationSeconds = getMaxPositionDurationSeconds()
        return signalTestingResults.entries
            .filter { it.value.getPositionDurationInSeconds(data.timeframe) == maxPositionDurationSeconds }
            .map { it.value }
            .firstOrNull()
    }

    fun searchSignalByLoss(loss: Double): PositionTestResult? {
        return signalTestingResults.entries
            .filter { !it.value.profitable && it.value.loss == loss }
            .map { it.value }
            .firstOrNull()
    }

    fun searchSignalByProfit(profit: Double): PositionTestResult? {
        return signalTestingResults.entries
            .filter { it.value.profitable && it.value.profit == profit }
            .map { it.value }
            .firstOrNull()
    }

    fun printSL() = stopLossStrategy.toString()

    fun printTP() = takeProfitStrategy.toString()

    fun printTPSLNumber() = "${getProfitablePositionsCount()}/${getLossPositionsCount()}"

    fun printTPSLPercent() = "${getSuccessfulRatio()}% / ${getLossRatio()}%"

    private fun lossesCollection() = signalTestingResults.entries
        .filter { !it.value.profitable }
        .map { it.value.loss!! }

    private fun profitsCollection() = signalTestingResults.entries
        .filter { it.value.profitable }
        .filter { it.value.profit != null }
        .map { it.value.profit!! }

    private fun positionDuration() = signalTestingResults.entries
        .filter { it.value.closed }
        .map { it.value.getPositionDurationInSeconds(data.timeframe) }


}