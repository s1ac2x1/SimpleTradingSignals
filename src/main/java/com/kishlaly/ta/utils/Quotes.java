package com.kishlaly.ta.utils;

import com.kishlaly.ta.model.Quote;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.Timeframe;
import com.kishlaly.ta.model.indicators.Bollinger;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Quotes {

    public static List<Quote> dayToWeek(List<Quote> dailyQuotes) {
        List<Quote> weeklyQuotes = new ArrayList<>();
        Set<Quote> week = new HashSet<>();
        List<Quote> weekSorted;
        for (int i = 0; i < dailyQuotes.size() - 1; i++) {
            try {
                Quote currentQuote = dailyQuotes.get(i);
                Quote nextQuote = dailyQuotes.get(i + 1);
                week.add(currentQuote);
                // сверять день недели у следующей котировки и программно вычислять следующий день для текущей
                ZonedDateTime currentQuoteDate = Dates.getTimeInExchangeZone(currentQuote.getTimestamp(), Quote.exchangeTimezome);
                ZonedDateTime currentQuotePlusOneDayDate = Dates.getTimeInExchangeZone(currentQuote.getTimestamp(), Quote.exchangeTimezome).plusDays(1);
                ZonedDateTime nextQuoteDate = Dates.getTimeInExchangeZone(nextQuote.getTimestamp(), Quote.exchangeTimezome);
                // если следующая котировка ровно на один день позже - мы рамках рабочей недели
                // если currentQuotePlusOneDayDate выходной день, то nextQuoteDate будет другой
                if (nextQuoteDate.getDayOfWeek() == currentQuotePlusOneDayDate.getDayOfWeek()) {
                    // исключаем ситуацию вида:
                    // неделя 1: пн вт ср чт _
                    // неделя 2: -  -  -  -  пт
                    // тут всегда будет разница в более чем пять дней
                    long daysBetweenCurrentAndNextQuote = ChronoUnit.DAYS.between(currentQuoteDate, nextQuoteDate);
                    if (daysBetweenCurrentAndNextQuote < 5) {
                        week.add(currentQuote);
                        week.add(nextQuote);
                    }
                } else {
                    weekSorted = new ArrayList<>(week);
                    Collections.sort(weekSorted, Comparator.comparing(Quote::getTimestamp));
                    long timestamp = weekSorted.get(0).getTimestamp();
                    double high = weekSorted.stream().mapToDouble(q -> q.getHigh()).max().getAsDouble();
                    double open = weekSorted.get(0).getOpen();
                    double close = weekSorted.get(week.size() - 1).getClose();
                    double low = weekSorted.stream().mapToDouble(q -> q.getLow()).min().getAsDouble();
                    double volume = weekSorted.stream().mapToDouble(q -> q.getVolume()).sum();
                    Quote weeklyQuote = new Quote(timestamp, Numbers.round(high), open, close, Numbers.round(low), volume);
                    weeklyQuotes.add(weeklyQuote);
                    week.clear();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Collections.sort(weeklyQuotes, Comparator.comparing(Quote::getTimestamp));
        return weeklyQuotes;
    }

    public static List<Quote> hourToDay(List<Quote> hourQuotes) {
        List<Quote> dayQuotes = new ArrayList<>();
        List<Quote> duringDay = new ArrayList<>();
        for (int i = 0; i < hourQuotes.size() - 1; i++) {
            Quote currentHour = hourQuotes.get(i);
            DayOfWeek currentHourDay = Dates.getTimeInExchangeZone(currentHour.getTimestamp(), Quote.exchangeTimezome).getDayOfWeek();
            Quote nextHour = hourQuotes.get(i + 1);
            DayOfWeek nextHourDay = Dates.getTimeInExchangeZone(nextHour.getTimestamp(), Quote.exchangeTimezome).getDayOfWeek();
            if (currentHourDay == nextHourDay) {
                duringDay.add(nextHour);
            } else {
                collectDayQuote(duringDay, dayQuotes);
                duringDay.add(nextHour);
            }
        }
        collectDayQuote(duringDay, dayQuotes);
        return dayQuotes;
    }

    public static int resolveMinBarsCount(Timeframe timeframe) {
        int min = 21; // меньше нельзя, иначе не сработает StopLossFixedPrice
        // если агрегация на основе часовых котировок (которых ~550), то дневных будет ~35, а недельных нет смысла рассматривать
        // если агрегация на основе дневных котировок (которых до 5500), то недельных будет до 1110
        return min;

//        int notSet = -1;
//        switch (timeframe) {
//            case WEEK:
//                if (Context.aggregationTimeframe == Timeframe.DAY) {
//                    if (Context.testMode) {
//                        return 4; // минимум для тестов
//                    } else {
//                        return 30; // 30 недель минимум, чтобы отлавливать в том числе молодые акции
//                    }
//                }
//                if (Context.aggregationTimeframe == Timeframe.HOUR) {
//                    throw new RuntimeException("Attemp to aggregate WEEK from HOUR - no sense");
//                }
//            case DAY:
//                if (Context.aggregationTimeframe == Timeframe.DAY) {
//                    if (Context.testMode) {
//                        return 4; // минимум для тестов
//                    } else {
//                        return 150; // полгода
//                    }
//                }
//                if (Context.aggregationTimeframe == Timeframe.HOUR) {
//                    if (Context.testMode) {
//                        return 11; // минимум для тестов
//                    } else {
//                        return 30; // по часовым котировкам получается до 35 дней собрать
//                    }
//                }
//            case HOUR:
//                if (Context.aggregationTimeframe == Timeframe.DAY) {
//                    throw new RuntimeException("Attemp to aggregate HOUR from DAY - impossible");
//                }
//                if (Context.aggregationTimeframe == Timeframe.HOUR) {
//                    if (Context.testMode) {
//                        return 4; // минимум для тестов
//                    } else {
//                        return 80; // хотя бы 2 недели
//                    }
//                }
//            default:
//                return notSet;
//        }
    }

    public static void trim(SymbolData screen) {
        if (screen.quotes != null && !screen.quotes.isEmpty()) {
            screen.quotes = screen.quotes.subList(screen.quotes.size() - resolveMinBarsCount(screen.timeframe), screen.quotes.size());
        } else {
            screen.quotes = new ArrayList<>();
        }
    }

    private static void collectDayQuote(List<Quote> hourQuotesInsideOneDay, List<Quote> dayQuotes) {
        List<Quote> dayQuotesSorted = new ArrayList<>(hourQuotesInsideOneDay);
        Collections.sort(dayQuotesSorted, Comparator.comparing(Quote::getTimestamp));
        if (dayQuotesSorted.isEmpty()) {
            System.out.println("Warning: hourQuotesInsideOneDay is empty");
            return;
        }
        long timestamp = dayQuotesSorted.get(0).getTimestamp();
        double high = dayQuotesSorted.stream().mapToDouble(q -> q.getHigh()).max().getAsDouble();
        double open = dayQuotesSorted.get(0).getOpen();
        double close = dayQuotesSorted.get(dayQuotesSorted.size() - 1).getClose();
        double low = dayQuotesSorted.stream().mapToDouble(q -> q.getLow()).min().getAsDouble();
        double volume = dayQuotesSorted.stream().mapToDouble(q -> q.getVolume()).sum();
        Quote dayQuote = new Quote(timestamp, Numbers.round(high), open, close, Numbers.round(low), volume);
        dayQuotes.add(dayQuote);
        hourQuotesInsideOneDay.clear();
    }

    public static boolean isQuoteCrossedEMA(Quote quote, double emaValue) {
        return quote.getLow() <= emaValue && quote.getHigh() >= emaValue;
    }

    public static boolean isQuoteBelowEMA(Quote quote, double emaValue) {
        return quote.getLow() < emaValue && quote.getHigh() < emaValue;
    }

    public static boolean isQuoteAboveEMA(Quote quote, double emaValue) {
        return quote.getLow() > emaValue && quote.getHigh() > emaValue;
    }

    public static boolean isQuoteCrossedBollingerBottom(Quote quote, Bollinger bollinger) {
        return quote.getLow() <= bollinger.getBottom() && quote.getHigh() >= bollinger.getBottom();
    }

    public static boolean isQuoteBelowBollingerBottom(Quote quote, Bollinger bollinger) {
        return quote.getLow() < bollinger.getBottom() && quote.getHigh() < bollinger.getBottom();
    }

}
