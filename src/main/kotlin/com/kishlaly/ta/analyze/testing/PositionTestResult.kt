package com.kishlaly.ta.analyze.testing

import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.Dates

class PositionTestResult(
        var openedTimestamp: Long? = null,
        var closedTimestamp: Long? = null,
        var openPositionPrice: Double? = null,
        var openPositionCost: Double? = null,
        var closePositionPrice: Double? = null,
        var closePositionCost: Double? = null,
        var commissions: Double? = null,
        var closed: Boolean = false,
        var profitable: Boolean = false,
        var profit: Double? = null,
        var loss: Double? = null,
        var roi: Double? = null,
        var gapUp: Boolean = false,
        var gapDown: Boolean = false) {

    fun getPositionDuration(timeframe: Timeframe): String {
        return if (closed) {
            Dates.getDuration(timeframe, openedTimestamp!!, closedTimestamp!!)
        } else ""
    }

    fun getPositionDurationInSeconds(timeframe: Timeframe) = closedTimestamp!! - openedTimestamp!!


}