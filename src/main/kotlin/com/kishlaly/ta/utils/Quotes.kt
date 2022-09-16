package com.kishlaly.ta.utils

import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.model.exchangeTimezome
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit

class Quotes {

    companion object {

        fun dayToWeek(dailyQuotes: List<Quote>): List<Quote> {
            val weeklyQuotes = mutableListOf<Quote>()
            val week = mutableSetOf<Quote>()
            var weekSorted: MutableList<Quote>
            for (i in dailyQuotes.indices - 1) {
                try {
                    val currentQuote = dailyQuotes[i]
                    val nextQuote = dailyQuotes[i + 1]
                    week.add(currentQuote)

                    // check the day of the week for the next quote and programmatically calculate the next day for the current one
                    val currentQuoteDate = Dates.getTimeInExchangeZone(currentQuote.timestamp, exchangeTimezome)
                    val currentQuotePlusOneDayDate =
                        Dates.getTimeInExchangeZone(currentQuote.timestamp, exchangeTimezome).plusDays(1)
                    val nextQuoteDate = Dates.getTimeInExchangeZone(nextQuote.timestamp, exchangeTimezome)

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
                            val timestamp = weekSorted.first().timestamp
                            val high = weekSorted.map { it.high }.max()
                            val open = weekSorted.first().open
                            val close = weekSorted.last().close
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
            return weeklyQuotes.sortedBy { it.timestamp }
        }

        fun hourToDay(hourQuotes: List<Quote>): List<Quote> {
            val dayQuotes = mutableListOf<Quote>()
            val duringDay = mutableListOf<Quote>()
            for (i in 0 until hourQuotes.size - 1) {
                val currentQuote = hourQuotes[i]
                val currentQuoteDayOfWeek: DayOfWeek =
                    Dates.getTimeInExchangeZone(currentQuote.timestamp, exchangeTimezome).getDayOfWeek()
                val nextQuote = hourQuotes[i + 1]
                val nextQuoteDayOfWeek = Dates.getTimeInExchangeZone(nextQuote.timestamp, exchangeTimezome).dayOfWeek
                if (currentQuoteDayOfWeek == nextQuoteDayOfWeek) {
                    duringDay.add(nextQuote)
                } else {
                    collectDayQuote(duringDay, dayQuotes)
                    duringDay.add(nextQuote)
                }
            }
            collectDayQuote(duringDay, dayQuotes)
            return dayQuotes
        }

        private fun collectDayQuote(hourQuotesInsideOneDay: MutableList<Quote>, dayQuotes: MutableList<Quote>) {
            val dayQuotesSorted = mutableListOf<Quote>(*hourQuotesInsideOneDay.toTypedArray()).sortedBy { it.timestamp }
            dayQuotesSorted.ifEmpty {
                println("Warning: hourQuotesInsideOneDay is empty")
                return
            }
            val timestamp = dayQuotesSorted.first().timestamp
            val high = dayQuotesSorted.map { it.high }.max()
            val open = dayQuotesSorted.first().open
            val close = dayQuotesSorted.last().close
            val low = dayQuotesSorted.map { it.low }.min()
            val volume = dayQuotesSorted.map { it.volume }.max()
            val dayQuote = Quote(timestamp, high.round(), open, close, low.round(), volume)
            dayQuotes.add(dayQuote)
            hourQuotesInsideOneDay.clear()
        }

        fun resolveMinBarsCount(timeframe: Timeframe): Int {
            // less is not allowed, otherwise StopLossFixedPrice will not work
            // If the aggregation is based on hourly quotes (which are ~550), then the daily quotes will be ~35, and there is no point in considering weekly ones
            // If the aggregation on the basis of daily quotes (which are up to 5500), then the weekly will be up to 1110
            return 21
        }

        fun trim(screen: SymbolData) {
            if (!screen.quotes.isNullOrEmpty()) {
                screen.quotes = screen.quotes.subList(
                    screen.quotes.size - resolveMinBarsCount(screen.timeframe),
                    screen.quotes.size
                )
            } else {
                screen.quotes = listOf()
            }
        }

    }

}