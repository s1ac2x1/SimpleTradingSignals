package com.kishlaly.ta.model;

public class Screens {

    private final SymbolDataJava screen1;
    private final SymbolDataJava screen2;

    public Screens(SymbolDataJava screen1, SymbolDataJava screen2) {
        this.screen1 = screen1;
        this.screen2 = screen2;
    }

    public SymbolDataJava getScreen1() {
        return this.screen1;
    }

    public SymbolDataJava getScreen2() {
        return this.screen2;
    }
}