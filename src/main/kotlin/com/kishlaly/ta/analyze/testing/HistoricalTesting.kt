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

    fun addTestResult(signal: Quote, positionTestResult: PositionTestResult) {
        signalTestingResults.put(signal, positionTestResult)
    }

    fun getResult(signal: Quote) = signalTestingResults.get(signal)

    fun getSuccessfulRatio(): Double {
        val allPositions = getAllPositionsCount()
        val profitablePositions = getProfitablePositionsCount()
        return if (allPositions == 0) {
            0.0
        } else Numbers.percent(profitablePositions.toDouble(), allPositions.toDouble())
    }

    fun getLossRatio(): Double {
        val allPositions = getAllPositionsCount()
        val lossPossitions = getLossPositionsCount()
        return if (allPositions == 0) {
            0.0
        } else Numbers.percent(lossPossitions.toDouble(), allPositions.toDouble())
    }

    fun getAllPositionsCount() = signalTestingResults.entries.filter { it.value.closed }.count()

    fun getProfitablePositionsCount() = signalTestingResults.entries.filter { it.value.profitable }.count()

    fun getLossPositionsCount() = signalTestingResults.entries.filter { !it.value.profitable }.count()

    fun getMinPositionDurationSeconds() = positionDuration().min()

    fun getAveragePositionDurationSeconds() = positionDuration().average().round()

    fun getMaxPositionDurationSeconds() = positionDuration().max()

    fun getMinProfit() = profitsCollection().min()

    fun getAvgProfit() = profitsCollection().average().round()

    fun getMaxProfit() = profitsCollection().max()

    fun getMinLoss() = lossesCollection().min()

    fun getAvgLoss() = lossesCollection().average().round()

    fun getMaxLoss() = lossesCollection().max()

    fun getTotalProfit() = signalTestingResults.entries
        .filter { it.value.profitable }
        .map { it.value.profit!! - it.value.commissions!! }
        .sum()

    fun getAverageRoi() = signalTestingResults.entries
        .filter { it.value.profitable }
        .map { it.value.roi!! }
        .average().round()

    fun getTotalLoss() = signalTestingResults.entries
        .filter { !it.value.profitable }
        .map { it.value.loss!! - it.value.commissions!! }
        .sum()

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