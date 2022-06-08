package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.Timeframe;

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

    // on the input may be the date of the form yyyyy-MM-dd for the day timeframe, so I shift the clock to the beginning of the exchange
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

    // on the input may be the date of the form yyyyy-MM-dd for the day timeframe, so I shift the clock to the beginning of the exchange
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

    // At the entrance of the exact time, so there is no need to reset the clock at the time of the opening of the exchange
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

    public static String getDuration(Timeframe timeframe, long start, long end) {
        ZoneId timezone = ZoneId.of(Context.myTimezone);
        LocalDateTime startDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(start), timezone);
        LocalDateTime endDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(end), timezone);
        switch (timeframe) {
            case WEEK:
                return ChronoUnit.WEEKS.between(startDate, endDate) + " weeks";
            case DAY:
                return ChronoUnit.DAYS.between(startDate, endDate) + " days";
            case HOUR:
                return ChronoUnit.HOURS.between(startDate, endDate) + " hours";
            default:
                return "";
        }
    }

    public static ZonedDateTime shortDateToZoned(String datePart) {
        String[] split = datePart.split("\\.");
        String date = split[2] + "-" + split[1] + "-" + split[0] + "T09:30-04:00[US/Eastern]";
        ZonedDateTime parsed = ZonedDateTime.parse(date);
        return parsed;
    }

    public static void main(String[] args) {
        System.out.println(getDuration(Timeframe.DAY, 1632749400, 1633008600));
    }

}
