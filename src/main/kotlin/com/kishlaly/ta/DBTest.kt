package com.kishlaly.ta

import com.kishlaly.ta.model.TestingsDBO
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.select

fun main() {
    val database = Database.connect("jdbc:postgresql://localhost:5432/test", user = "root", password = "***")

    for (row in database.from(TestingsDBO).select()) {
        println(row[TestingsDBO.id])
    }
}