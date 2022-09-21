package com.kishlaly.ta.model.indicators

import com.kishlaly.ta.model.AbstractModel

enum class Indicator(val model: Class<out AbstractModel>) {
    EMA26(EMA::class.java), // close
    EMA13(EMA::class.java), // close
    MACD(com.kishlaly.ta.model.indicators.MACD::class.java), // 12 26 9 close
    STOCHASTIC(Stochastic::class.java), // 14 1 3 close
    KELTNER(Keltner::class.java), // 20 2 ATR 10
    BOLLINGER(Bollinger::class.java), // 20 2 0
    EFI(ElderForceIndex::class.java);

    companion object {
        fun findByClass(clazz: Class<out AbstractModel>): Indicator {
            return values().filter { it.model == clazz }.first()
        }
    }
}