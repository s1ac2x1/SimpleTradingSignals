package com.kishlaly.ta.utils

class Numbers {

    companion object {
        fun roi(initialCost: Double, currentCost: Double): Double {
            val result = (currentCost - initialCost) / initialCost * 100
            return result.round()
        }

        fun percent(n: Double, N: Double): Double {
            return (n * 100 / N).round()
        }

    }

}