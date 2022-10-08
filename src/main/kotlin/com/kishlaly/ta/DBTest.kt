package com.kishlaly.ta

import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.select

fun main() {
    val database = Database.connect("jdbc:postgresql://localhost:5432/test", user = "test", password = "test")

//    for (row in database.from(TestingsDBO).select()) {
//        println(row[TestingsDBO.id])
//    }

}