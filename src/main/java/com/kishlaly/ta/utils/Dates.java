package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.Quote;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author Vladimir Kishlaly
 * @since 19.11.2021
 */
public class Dates {

    public static String beautifyQuoteDate(Quote quote) {
        return getBarTimeInMyZone(quote.getTimestamp(), quote.exchangeTimezome).toString();
    }

    public static String getDateFormat() {
        switch (Context.timeframe) {
            case HOUR:
                return "yyyy-MM-dd HH:mm:ss";
            case WEEK:
            default:
                return "yyyy-MM-dd";
        }
    }

    // на входе может быть дата вида yyyy-MM-dd для дневного таймфрейма, поэтому сдвигаю часы на начало работы биржи
    public static ZonedDateTime getTimeInExchangeZone(String date, String exchangeTimezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getDateFormat());
        switch (Context.timeframe) {
            case HOUR:
                LocalDateTime localDate = LocalDateTime.parse(date, formatter);
                return localDate
                        .atZone(ZoneId.of(exchangeTimezone))
                        .withZoneSameInstant(ZoneId.of(exchangeTimezone));
            case WEEK:
            case DAY:
            default:
                return LocalDate.parse(date, formatter)
                        .atStartOfDay(ZoneId.of(exchangeTimezone))
                        .withZoneSameInstant(ZoneId.of(exchangeTimezone))
                        .plus(9, ChronoUnit.HOURS)
                        .plus(30, ChronoUnit.MINUTES);
        }
    }

    // на входе может быть дата вида yyyy-MM-dd для дневного таймфрейма, поэтому сдвигаю часы на начало работы биржи
    public static ZonedDateTime getBarTimeInMyZone(String date, String exchangeTimezone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getDateFormat());
        switch (Context.timeframe) {
            case HOUR:
                LocalDateTime localDate = LocalDateTime.parse(date, formatter);
                return localDate
                        .atZone(ZoneId.of(exchangeTimezone))
                        .withZoneSameInstant(ZoneId.of(Context.myTimezone));
            case WEEK:
            case DAY:
            default:
                return LocalDate.parse(date, formatter)
                        .atStartOfDay(ZoneId.of(exchangeTimezone))
                        .withZoneSameInstant(ZoneId.of(Context.myTimezone))
                        .plus(9, ChronoUnit.HOURS)
                        .plus(30, ChronoUnit.MINUTES);
        }
    }

    // На входе точное время, поэтому не нужно сбрасывать часы на время открытия биржи
    public static ZonedDateTime getBarTimeInMyZone(Long timestamp, String exchangeTimezoneStr) {
        ZoneId exchangeTimezone = ZoneId.of(exchangeTimezoneStr);
        ZoneId myTimezone = ZoneId.of(Context.myTimezone);

        LocalDateTime exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), exchangeTimezone);
        ZonedDateTime exchangeZonedDateTime = exchangeLocalDateTime.atZone(exchangeTimezone);

        ZonedDateTime timeInMyZone = exchangeZonedDateTime.withZoneSameInstant(OffsetDateTime.now(myTimezone).getOffset()).withZoneSameInstant(myTimezone);
        return timeInMyZone;
    }

    public static ZonedDateTime getTimeInExchangeZone(Long timestamp, String exchangeTimezoneStr) {
        ZoneId exchangeTimezone = ZoneId.of(exchangeTimezoneStr);
        LocalDateTime exchangeLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), exchangeTimezone);
        ZonedDateTime exchangeZonedDateTime = exchangeLocalDateTime.atZone(exchangeTimezone);
        return exchangeZonedDateTime;
    }

    public static void main(String[] args) {
    }

}
