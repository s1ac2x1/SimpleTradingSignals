package com.kishlaly.ta

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    GlobalScope.launch { // запуск новой сопрограммы в фоне
        delay(1000L) // неблокирующая задержка на 1 секунду
        println("World!") // вывод результата после задержки
    }
    println("Hello,") // пока сопрограмма проводит вычисления, основной поток продолжает свою работу
    Thread.sleep(2000L) // блокировка основного потока на 2 секунды, чтобы сопрограмма успела произвести вычисления
}