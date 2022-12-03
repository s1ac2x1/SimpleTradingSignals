package com.kishlaly.ta.costsparser

import com.kishlaly.ta.utils.roundDown
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


fun main() {
    val lines = File("20221202-1237417785-umsatz.CSV").readLines()
    val grouped = lines.groupBy { line ->
        line.split(";")[5]
    }
    var html = "<style>table, th, td { border: 1px solid; }</style><table>"
    html += """
        <script>
        function selectText(containerid) {
            if (document.selection) { // IE
                var range = document.body.createTextRange();
                range.moveToElementText(document.getElementById(containerid));
                range.select();
            } else if (window.getSelection) {
                var range = document.createRange();
                range.selectNode(document.getElementById(containerid));
                window.getSelection().removeAllRanges();
                window.getSelection().addRange(range);
            }
        }
        </script>
    """.trimIndent()
    val df = DecimalFormat("#.#", DecimalFormatSymbols(Locale.GERMANY))
    grouped.forEach { name, lines ->
        val id = "id-${System.nanoTime()}"
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
        val color = if (sum.roundDown() >= 0) "color:#006400" else "color:B22202"
        html += "<td><div style='${color}' id=\"${id}\" onclick=\"selectText('${id}')\">${df.format(Math.abs(sum.roundDown()))}</div></td></tr>"
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