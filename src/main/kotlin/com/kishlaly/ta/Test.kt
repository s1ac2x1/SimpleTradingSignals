package com.kishlaly.ta

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        repeat(100000) {
            launch { some() }
        }
    }
}

suspend fun some() {
    delay(300L)
    print(".")
}
