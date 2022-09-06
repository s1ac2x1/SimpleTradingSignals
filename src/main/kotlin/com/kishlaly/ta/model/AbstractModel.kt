package com.kishlaly.ta.model

import com.kishlaly.ta.utils.Dates

abstract class AbstractModel {

    companion object {
        val exchangeTimezome = "US/Eastern"
    }

    protected val timestamp: Long
    protected val nativeDate: String
    protected val myDate: String

    constructor(timestamp: Long) {
        this.timestamp = timestamp
        this.nativeDate = Dates.getTimeInExchangeZone(timestamp, exchangeTimezome).toString()
        this.myDate = Dates.getBarTimeInMyZone(timestamp, exchangeTimezome).toString()
    }


}