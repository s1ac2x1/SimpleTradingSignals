package com.kishlaly.ta.model;

import com.kishlaly.ta.utils.Dates;
import com.kishlaly.ta.utils.Numbers;

import java.util.*;

public class HistoricalTesting {

    private SymbolData data;
    private List<Quote> signals = new ArrayList<>();
    private Map<Quote, Result> signalsResults = new HashMap<>();

    public HistoricalTesting(final SymbolData data, final List<Quote> signals) {
        this.data = data;
        this.signals = signals;
    }

    public SymbolData getData() {
        return this.data;
    }

    public void setData(final SymbolData data) {
        this.data = data;
    }

    public List<Quote> getSignals() {
        return this.signals;
    }

    public void setSignals(final List<Quote> signals) {
        this.signals = signals;
    }

    public void addSignalResult(Quote signal, Result result) {
        signalsResults.put(signal, result);
    }

    public Result getResult(Quote signal) {
        return signalsResults.get(signal);
    }

    public long getProfitablePositions() {
        return signalsResults.entrySet().stream().filter(entry -> entry.getValue().isProfitable()).count();
    }

    public long getLossPositions() {
        return signalsResults.entrySet().stream().filter(entry -> !entry.getValue().isProfitable()).count();
    }

    public long getAveragePositionDurationSeconds() {
        return (long) signalsResults.entrySet().stream().mapToLong(entry -> entry.getValue().getPositionDurationInSeconds(data.timeframe)).average().getAsDouble();
    }

    public long getMinPositionDurationSeconds() {
        return signalsResults.entrySet().stream().mapToLong(entry -> entry.getValue().getPositionDurationInSeconds(data.timeframe)).min().getAsLong();
    }

    public long getMaxPositionDurationSeconds() {
        return signalsResults.entrySet().stream().mapToLong(entry -> entry.getValue().getPositionDurationInSeconds(data.timeframe)).max().getAsLong();
    }

    public double getMaxProfit() {
        return Numbers.round(signalsResults.entrySet().stream().mapToDouble(entry -> entry.getValue().getProfit()).max().getAsDouble());
    }

    public double getAvgProfit() {
        return Numbers.round(signalsResults.entrySet().stream().mapToDouble(entry -> entry.getValue().getProfit()).average().getAsDouble());
    }

    public double getMaxLoss() {
        return Numbers.round(signalsResults.entrySet().stream().mapToDouble(entry -> entry.getValue().getLoss()).max().getAsDouble());
    }

    public double getAvgLoss() {
        return Numbers.round(signalsResults.entrySet().stream().mapToDouble(entry -> entry.getValue().getLoss()).average().getAsDouble());
    }

    public double getTotalProfit() {
        return Numbers.round(signalsResults.entrySet().stream().mapToDouble(entry -> entry.getValue().getProfit()).sum());
    }

    public double getTotalLoss() {
        return Numbers.round(signalsResults.entrySet().stream().mapToDouble(entry -> entry.getValue().getLoss()).sum());
    }

    public Result searchSignalByPositionDuration(long duration) {
        Optional<Map.Entry<Quote, Result>> first = signalsResults.entrySet().stream().filter(entrySet -> entrySet.getValue().getPositionDurationInSeconds(data.timeframe) == duration).findFirst();
        if (first.isPresent()) {
            return first.get().getValue();
        } else {
            return null;
        }
    }

    public Result searchSignalByMaxProfit() {
        Optional<Map.Entry<Quote, Result>> first = signalsResults.entrySet().stream().filter(entrySet -> entrySet.getValue().getProfit() == getMaxProfit()).findFirst();
        if (first.isPresent()) {
            return first.get().getValue();
        } else {
            return null;
        }
    }

    public Result searchSignalByMaxLoss() {
        Optional<Map.Entry<Quote, Result>> first = signalsResults.entrySet().stream().filter(entrySet -> entrySet.getValue().getLoss() == getMaxLoss()).findFirst();
        if (first.isPresent()) {
            return first.get().getValue();
        } else {
            return null;
        }
    }

    public static class Result {

        private long openedTimestamp;
        private long closedTimestamp;

        private boolean closed;

        private boolean profitable;
        private double profit;
        private double loss;
        private double roi;

        private boolean gapUp;
        private boolean gapDown;

        public boolean isClosed() {
            return this.closed;
        }

        public void setClosed(final boolean closed) {
            this.closed = closed;
        }

        public String getPositionDuration(Timeframe timeframe) {
            if (closed) {
                return Dates.getDuration(timeframe, openedTimestamp, closedTimestamp);
            }
            return "";
        }

        public long getPositionDurationInSeconds(Timeframe timeframe) {
            return closedTimestamp - openedTimestamp;
        }

        public boolean isProfitable() {
            return this.profitable;
        }

        public void setProfitable(final boolean profitable) {
            this.profitable = profitable;
        }

        public double getProfit() {
            return Numbers.round(this.profit);
        }

        public void setProfit(final double profit) {
            this.profit = profit;
        }

        public double getLoss() {
            return Numbers.round(this.loss);
        }

        public void setLoss(final double loss) {
            this.loss = loss;
        }

        public long getOpenedTimestamp() {
            return this.openedTimestamp;
        }

        public void setOpenedTimestamp(final long openedTimestamp) {
            this.openedTimestamp = openedTimestamp;
        }

        public long getClosedTimestamp() {
            return this.closedTimestamp;
        }

        public void setClosedTimestamp(final long closedTimestamp) {
            this.closedTimestamp = closedTimestamp;
        }

        public boolean isGapUp() {
            return this.gapUp;
        }

        public void setGapUp(final boolean gapUp) {
            this.gapUp = gapUp;
        }

        public boolean isGapDown() {
            return this.gapDown;
        }

        public void setGapDown(final boolean gapDown) {
            this.gapDown = gapDown;
        }
    }

}
