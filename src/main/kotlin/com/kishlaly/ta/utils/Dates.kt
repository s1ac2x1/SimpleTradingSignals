package com.kishlaly.ta.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class Dates {

    companion object {
        fun getTimeInExchangeZone(timestamp: Long, exchangeTimezoneStr: String): ZonedDateTime {
            val exchangeTimezone = ZoneId.of(exchangeTimezoneStr)
            val exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), exchangeTimezone)
            val exchangeZonedDateTime = exchangeLocalDateTime.atZone(exchangeTimezone)
            return exchangeZonedDateTime
        }
    }

}