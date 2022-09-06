package com.kishlaly.ta.model

import com.kishlaly.ta.utils.Dates

abstract class AbstractModel {

    companion object {
        val exchangeTimezome = "US/Eastern"
    }

    // in epoch seconds
    val timestamp: Long
    val nativeDate: String
    val myDate: String

    constructor(timestamp: Long) {
        this.timestamp = timestamp
        this.nativeDate = Dates.getTimeInExchangeZone(timestamp, exchangeTimezome).toString()
        this.myDate = Dates.getBarTimeInMyZone(timestamp, exchangeTimezome).toString()
    }


}