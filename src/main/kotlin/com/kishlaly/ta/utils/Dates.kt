package com.kishlaly.ta.utils

import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.model.exchangeTimezome
import java.time.*

class Dates {

    companion object {

        fun beautifyQuoteDate(quote: Quote): String {
            return getBarTimeInMyZone(quote.timestamp, exchangeTimezome).toString()
        }

        fun getDateFormat(): String {
            return when (Context.timeframe) {
                Timeframe.HOUR -> "yyyy-MM-dd HH:mm:ss"
                Timeframe.WEEK -> "yyyy-MM-dd"
                else -> "yyyy-MM-dd"
            }
        }

        // on the input may be the date of the form yyyyy-MM-dd for the day timeframe, so I shift the clock to the beginning of the exchange
        fun getTimeInExchangeZone(timestamp: Long, exchangeTimezoneStr: String): ZonedDateTime {
            val exchangeTimezone = ZoneId.of(exchangeTimezoneStr)
            val exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), exchangeTimezone)
            val exchangeZonedDateTime = exchangeLocalDateTime.atZone(exchangeTimezone)
            return exchangeZonedDateTime
        }

        // on the input may be the date of the form yyyyy-MM-dd for the day timeframe, so I shift the clock to the beginning of the exchange
        fun getBarTimeInMyZone(timestamp: Long, exchangeTimezoneStr: String): ZonedDateTime {
            val exchangeTimezone = ZoneId.of(exchangeTimezoneStr)
            val myTimezone = ZoneId.of(Context.myTimezone)

            val exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), exchangeTimezone)
            val exchangeZonedDateTime = exchangeLocalDateTime.atZone(exchangeTimezone)

            return exchangeZonedDateTime.withZoneSameInstant(OffsetDateTime.now(myTimezone).offset).withZoneSameInstant(myTimezone)
        }

        // At the entrance of the exact time, so there is no need to reset the clock at the time of the opening of the exchange
        fun getBarTimeInMyZone(timestamp: Long, exchangeTimezoneStr: String): ZonedDateTime {

        }

        fun shortDateToZoned(datePart: String): ZonedDateTime {
            val split = datePart.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val date = split[2] + "-" + split[1] + "-" + split[0] + "T09:30-04:00[US/Eastern]"
            return ZonedDateTime.parse(date)
        }
    }

}