package com.kishlaly.ta.model

import com.kishlaly.ta.utils.Dates

abstract class AbstractModel {

    companion object {
        val exchangeTimezome = "US/Eastern"
    }

    // in epoch seconds
    open public val timestamp: Long
    public val nativeDate: String
    public val myDate: String

    constructor(timestamp: Long) {
        this.timestamp = timestamp
        this.nativeDate = Dates.getTimeInExchangeZone(timestamp, exchangeTimezome).toString()
        this.myDate = Dates.getBarTimeInMyZone(timestamp, exchangeTimezome).toString()
    }

    abstract fun valuesPresent(): Boolean

}