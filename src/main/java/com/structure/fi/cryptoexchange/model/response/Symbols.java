package com.structure.fi.cryptoexchange.model.response;

import java.util.List;

public class Symbols {
    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }

    private List<String> symbols;
}
