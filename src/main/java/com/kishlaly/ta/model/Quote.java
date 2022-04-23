package com.kishlaly.ta.model;

import java.io.Serializable;
import java.util.Objects;

import static com.kishlaly.ta.utils.Dates.getBarTimeInMyZone;
import static com.kishlaly.ta.utils.Dates.getTimeInExchangeZone;

/**
 * @author Vladimir Kishlaly
 * @since 16.11.2021
 */
public class Quote extends EntityWithDate implements Serializable {

    private double high;
    private double open;
    private double close;
    private double low;
    private double volume;

    public Quote(final Long timestamp, final double high, final double open, final double close, final double low, final double volume) {
        super(timestamp);
        this.high = high;
        this.open = open;
        this.close = close;
        this.low = low;
        this.volume = volume;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
        this.nativeDate = getTimeInExchangeZone(timestamp, exchangeTimezome).toString();
        this.myDate = getBarTimeInMyZone(timestamp, exchangeTimezome).toString();
    }

    public static String getExchangeTimezome() {
        return Quote.exchangeTimezome;
    }

    public static void setExchangeTimezome(final String exchangeTimezome) {
        Quote.exchangeTimezome = exchangeTimezome;
    }

    public String getNativeDate() {
        return this.nativeDate;
    }

    public String getMyDate() {
        return this.myDate;
    }

    public double getHigh() {
        return this.high;
    }

    public void setHigh(final double high) {
        this.high = high;
    }

    public double getOpen() {
        return this.open;
    }

    public void setOpen(final double open) {
        this.open = open;
    }

    public double getClose() {
        return this.close;
    }

    public void setClose(final double close) {
        this.close = close;
    }

    public double getLow() {
        return this.low;
    }

    public void setLow(final double low) {
        this.low = low;
    }

    public double getVolume() {
        return this.volume;
    }

    public void setVolume(final double volume) {
        this.volume = volume;
    }

    public boolean valuesPesent() {
        return !Double.isNaN(open) && !Double.isNaN(close) && !Double.isNaN(low) && !Double.isNaN(high) && !Double.isNaN(volume);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Quote quote = (Quote) o;
        return Double.compare(quote.high, this.high) == 0 && Double.compare(quote.open, this.open) == 0 && Double.compare(quote.close, this.close) == 0 && Double.compare(quote.low, this.low) == 0 && Double.compare(quote.volume, this.volume) == 0 && this.timestamp.equals(quote.timestamp) && this.exchangeTimezome.equals(quote.exchangeTimezome) && this.nativeDate.equals(quote.nativeDate) && this.myDate.equals(quote.myDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.timestamp, this.exchangeTimezome, this.nativeDate, this.myDate, this.high, this.open, this.close, this.low, this.volume);
    }
}
