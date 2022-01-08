package com.kishlaly.ta.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static class Result {
        private boolean closed;
        private boolean profitable;
        private double profit;
        private double loss;
        private long timeDiff;

        public boolean isProfitable() {
            return this.profitable;
        }

        public void setProfitable(final boolean profitable) {
            this.profitable = profitable;
        }

        public boolean isClosed() {
            return this.closed;
        }

        public void setClosed(final boolean closed) {
            this.closed = closed;
        }

        public double getProfit() {
            return this.profit;
        }

        public void setProfit(final double profit) {
            this.profit = profit;
        }

        public double getLoss() {
            return this.loss;
        }

        public void setLoss(final double loss) {
            this.loss = loss;
        }

        public long getTimeDiff() {
            return this.timeDiff;
        }

        public void setTimeDiff(final long timeDiff) {
            this.timeDiff = timeDiff;
        }
    }

}
