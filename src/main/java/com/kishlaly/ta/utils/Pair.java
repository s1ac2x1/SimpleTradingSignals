package com.kishlaly.ta.utils;

public class Pair<LEFT, RIGHT> {
    private LEFT left;
    private RIGHT right;

    public Pair(final LEFT left, final RIGHT right) {
        this.left = left;
        this.right = right;
    }

    public LEFT getLeft() {
        return this.left;
    }

    public RIGHT getRight() {
        return this.right;
    }
}
