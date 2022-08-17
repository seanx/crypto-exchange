package com.structure.fi.cryptoexchange.model.response;

public class SymbolStats {
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getMostRecentPrice() {
        return mostRecentPrice;
    }

    public void setMostRecentPrice(double mostRecentPrice) {
        this.mostRecentPrice = mostRecentPrice;
    }

    private String symbol;
    private int frequency;
    private double median;
    private double mostRecentPrice;

}
