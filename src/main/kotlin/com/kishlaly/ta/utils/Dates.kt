package com.kishlaly.ta.utils

import java.time.*

class Dates {

    companion object {

        fun getTimeInExchangeZone(timestamp: Long, exchangeTimezoneStr: String): ZonedDateTime {
            val exchangeTimezone = ZoneId.of(exchangeTimezoneStr)
            val exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), exchangeTimezone)
            val exchangeZonedDateTime = exchangeLocalDateTime.atZone(exchangeTimezone)
            return exchangeZonedDateTime
        }

        fun getBarTimeInMyZone(timestamp: Long, exchangeTimezoneStr: String): ZonedDateTime {
            val exchangeTimezone = ZoneId.of(exchangeTimezoneStr)
            val myTimezone = ZoneId.of(Context.myTimezone)

            val exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), exchangeTimezone)
            val exchangeZonedDateTime = exchangeLocalDateTime.atZone(exchangeTimezone)

            return exchangeZonedDateTime.withZoneSameInstant(OffsetDateTime.now(myTimezone).offset).withZoneSameInstant(myTimezone)
        }
    }

}