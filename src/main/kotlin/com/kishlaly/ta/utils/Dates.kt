package com.kishlaly.ta.utils

import com.kishlaly.ta.config.Context
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.model.exchangeTimezome
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
        fun getTimeInExchangeZone(date: String, exchangeTimezone: String): ZonedDateTime {
            val formatter = DateTimeFormatter.ofPattern(getDateFormat())
            return when (Context.timeframe) {
                Timeframe.HOUR -> {
                    val localDate = LocalDateTime.parse(date, formatter)
                    localDate
                            .atZone(ZoneId.of(exchangeTimezone))
                            .withZoneSameInstant(ZoneId.of(exchangeTimezone))
                }
                Timeframe.WEEK, Timeframe.DAY -> LocalDate.parse(date, formatter)
                        .atStartOfDay(ZoneId.of(exchangeTimezone))
                        .withZoneSameInstant(ZoneId.of(exchangeTimezone))
                        .plus(9, ChronoUnit.HOURS)
                        .plus(30, ChronoUnit.MINUTES)
            }
        }

        // on the input may be the date of the form yyyyy-MM-dd for the day timeframe, so I shift the clock to the beginning of the exchange
        fun getBarTimeInMyZone(date: String, exchangeTimezone: String): ZonedDateTime {
            val formatter = DateTimeFormatter.ofPattern(getDateFormat())
            return when (Context.timeframe) {
                Timeframe.HOUR -> {
                    val localDate = LocalDateTime.parse(date, formatter)
                    localDate
                            .atZone(ZoneId.of(exchangeTimezone))
                            .withZoneSameInstant(ZoneId.of(Context.myTimezone))
                }
                Timeframe.WEEK, Timeframe.DAY -> LocalDate.parse(date, formatter)
                        .atStartOfDay(ZoneId.of(exchangeTimezone))
                        .withZoneSameInstant(ZoneId.of(Context.myTimezone))
                        .plus(9, ChronoUnit.HOURS)
                        .plus(30, ChronoUnit.MINUTES)
            }
        }

        // At the entrance of the exact time, so there is no need to reset the clock at the time of the opening of the exchange
        fun getBarTimeInMyZone(timestamp: Long, exchangeTimezoneStr: String): ZonedDateTime {
            val exchangeTimezone = ZoneId.of(exchangeTimezoneStr)
            val myTimezone = ZoneId.of(Context.myTimezone)
            val exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), exchangeTimezone)
            val exchangeZonedDateTime = exchangeLocalDateTime.atZone(exchangeTimezone)
            return exchangeZonedDateTime.withZoneSameInstant(OffsetDateTime.now(myTimezone).offset).withZoneSameInstant(myTimezone)
        }

        fun getTimeInExchangeZone(timestamp: Long, exchangeTimezoneStr: String): ZonedDateTime {
            val exchangeTimezone = ZoneId.of(exchangeTimezoneStr)
            val exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp!!), exchangeTimezone)
            return exchangeLocalDateTime.atZone(exchangeTimezone)
        }

        fun getDuration(timeframe: Timeframe, start: Long, end: Long): String {
            val timezone = ZoneId.of(Context.myTimezone)
            val startDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(start), timezone)
            val endDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(end), timezone)
            return when (timeframe) {
                Timeframe.WEEK -> ChronoUnit.WEEKS.between(startDate, endDate).toString() + " weeks"
                Timeframe.DAY -> ChronoUnit.DAYS.between(startDate, endDate).toString() + " days"
                Timeframe.HOUR -> ChronoUnit.HOURS.between(startDate, endDate).toString() + " hours"
            }
        }

        fun shortDateToZoned(datePart: String): ZonedDateTime {
            val split = datePart.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val date = split[2] + "-" + split[1] + "-" + split[0] + "T09:30-04:00[US/Eastern]"
            return ZonedDateTime.parse(date)
        }
    }

}