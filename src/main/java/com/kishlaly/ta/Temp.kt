package com.kishlaly.ta

fun printSubsets(set: CharArray): ArrayList<ArrayList<Char>> {
    val n = set.size
    val result = ArrayList<ArrayList<Char>>()
    for (i in 0 until (1 shl n)) {
        result.add(ArrayList())
        for (j in 0 until n) {
            if (i and (1 shl j) > 0) {
                result.get(i).add(set[j])
            }
        }
    }
    return result
}

fun main() {
    val set = charArrayOf('1', '2', '3', '4', '5', '6', '7')
    val subsets = printSubsets(set)
    for (subset in subsets) {
        println(subset)
    }
}