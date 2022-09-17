package com.kishlaly.ta.utils

class CollectionUtils {

    companion object {
        fun <T> getFromEnd(collection: List<T>, num: Int): T {
            return collection[collection.size - num]
        }
    }

}