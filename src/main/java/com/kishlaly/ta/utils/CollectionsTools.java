package com.kishlaly.ta.utils;

import java.util.List;

public class CollectionsTools {

    public static <T> T getFromEnd(List<T> collection, int num) {
        return collection.get(collection.size() - num);
    }

}
