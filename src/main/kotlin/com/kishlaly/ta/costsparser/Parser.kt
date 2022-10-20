package com.kishlaly.ta.costsparser

import com.kishlaly.ta.utils.roundDown
import java.io.File

fun main() {
    val lines = File("20221020-1237417785-umsatz.CSV").readLines()
    val grouped = lines.groupBy { line ->
        line.split(";")[5]
    }
    var html = "<style>table, th, td { border: 1px solid; }</style><table>"
    grouped.forEach { name, lines ->
        html += "<tr>"
        html += "<td>${name}</td>"
        val sum = lines.map { it.split(";")[8] }
            .map { it.replace(",", ".").replace("\"", "") }
            .map { price ->
                try {
                    price.toDouble()
                } catch (e: Exception) {
                    0
                }
            }.sumOf { it.toDouble() }
        html += "<td>${sum.roundDown()}</td></tr>"
    }
    html += "</table>"
    File("costs.html").writeText(html)
//    lines.forEach { line ->
//        val elements = line.split(";")
//        val cost = try {
//            elements[8].toDouble()
//        } catch (e: Exception) {
//        } finally {
//            0
//        }
//        println(elements[8])
//    }
}