package com.kishlaly.ta.openai.flow

fun main() {
    val step1 =
        Step("step_1", "eine Einleitung für einen Artikel zu einem Thema schreiben: Welche Katzen Haaren am wenigsten?")
    step1.run()
}