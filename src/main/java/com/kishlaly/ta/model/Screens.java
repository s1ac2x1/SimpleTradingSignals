package com.kishlaly.ta.model;

public class Screens {

    private final SymbolData screen1;
    private final SymbolData screen2;

    public Screens(SymbolData screen1, SymbolData screen2) {
        this.screen1 = screen1;
        this.screen2 = screen2;
    }

    public SymbolData getScreen1() {
        return this.screen1;
    }

    public SymbolData getScreen2() {
        return this.screen2;
    }
}