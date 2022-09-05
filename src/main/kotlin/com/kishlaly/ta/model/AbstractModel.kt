package com.kishlaly.ta.model

abstract class AbstractModel {

    companion object {
        val exchangeTimezome = "US/Eastern"
    }

    protected val timestamp: Long
    protected val nativeDate: String
    protected val myDate: String

    constructor(timestamp: Long) {
        this.timestamp = timestamp
        this.nativeDate = ""
        this.myDate = ""
    }


}