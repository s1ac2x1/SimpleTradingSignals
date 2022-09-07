package com.kishlaly.ta.utils

import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.QuoteJava
import java.time.temporal.ChronoUnit

class Quotes {

    companion object {

        fun dayToWeek(dailyQuotes: List<Quote>): List<Quote> {
            val weeklyQuotes = mutableListOf<Quote>()
            val week = mutableSetOf<Quote>()
            var weekSorted = mutableListOf<Quote>()
            for (i in dailyQuotes.indices - 1) {
                try {
                    val currentQuote = dailyQuotes[i]
                    val nextQuote = dailyQuotes[i + 1]
                    week.add(currentQuote)

                    // check the day of the week for the next quote and programmatically calculate the next day for the current one
                    // check the day of the week for the next quote and programmatically calculate the next day for the current one
                    val currentQuoteDate = Dates.getTimeInExchangeZone(currentQuote.timestamp, QuoteJava.exchangeTimezome)
                    val currentQuotePlusOneDayDate = Dates.getTimeInExchangeZone(currentQuote.timestamp, QuoteJava.exchangeTimezome).plusDays(1)
                    val nextQuoteDate = Dates.getTimeInExchangeZone(nextQuote.timestamp, QuoteJava.exchangeTimezome)

                    // If the next quote is exactly one day later - we are within the working week
                    // if currentQuotePlusOneDayDate is a day off, then nextQuoteDate will be different
                    if (nextQuoteDate.getDayOfWeek() == currentQuotePlusOneDayDate.getDayOfWeek()) {
                        // we exclude the situation like that:
                        // week 1: MON TUE WED THU -
                        // week 2: -    -   -   -  FRI
                        // There will always be a difference of more than five days

                        // we exclude the situation like that:
                        // week 1: MON TUE WED THU -
                        // week 2: -    -   -   -  FRI
                        // There will always be a difference of more than five days
                        val daysBetweenCurrentAndNextQuote = ChronoUnit.DAYS.between(currentQuoteDate, nextQuoteDate)
                        if (daysBetweenCurrentAndNextQuote < 5) {
                            week.add(currentQuote)
                            week.add(nextQuote)
                        } else {
                            weekSorted = mutableListOf(*week.toTypedArray())
                            weekSorted.sortBy { it.timestamp }
                            val timestamp: Long = weekSorted[0].timestamp
                            val high = weekSorted.map { it.high }.max()
                            val open: Double = weekSorted.first().open
                            val close: Double = weekSorted.last().close
                            val low = weekSorted.map { it.low }.min()
                            val volume = weekSorted.map { it.volume }.sum()
                            val weeklyQuote = Quote(timestamp, high.round(), open, close, low.round(), volume)
                            weeklyQuotes.add(weeklyQuote)
                            week.clear()
                        }
                    }
                } catch (e: Exception) {
                    println(e)
                }
            }
            weeklyQuotes.sortBy { it.timestamp }
            return weeklyQuotes
        }

    }

}