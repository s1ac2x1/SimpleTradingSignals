package com.kishlaly.ta.utils

import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.utils.LogJava.KeyJava
import java.util.*

class Log {

    companion object {

        private val log = StringBuilder()
        private val debug = StringBuilder()
        private val codes = mutableMapOf<BlockResultCode, MutableList<SymbolData>>()
        private val summary = mutableMapOf<Key, MutableSet<String>>()

        fun addLine(line: String) = log.append(line).append(System.lineSeparator())

        fun addDebugLine(line: String) = debug.append(line).append(System.lineSeparator())

        fun recordCode(code: BlockResultCode, symbolData: SymbolData) {
            val existingRecord = codes.get(code) ?: mutableListOf()
            existingRecord.add(symbolData)
            codes.put(code, existingRecord)
        }

        fun saveDebug(filename: String) = FileUtils.appendToFile(filename, debug.toString())

        fun saveSummary(filename: String) {
            val builder = StringBuilder()
            val symbolToGroups = mutableMapOf<String, MutableSet<BlocksGroup>>()
            summary.forEach { key, symbols ->
                symbols.forEach {
                    var symbolGroups = symbolToGroups[it] ?: mutableSetOf()
                    symbolToGroups[it] = symbolGroups
                    symbolGroups.add(key.blocksGroup)
                }
            }

            builder.append("<table style=\"border: 1px solid;\">")

            symbolToGroups
                    .entries
                    .sortedBy { it.value.size }.reversed()
                    .forEach { entry ->
                        builder.append("<tr style=\"border: 1px solid;\">")
                        builder.append("<td style=\"border: 1px solid; vertical-align: top text-align: left;\">" + entry + "</td>")
                        builder.append("<td style=\"border: 1px solid; vertical-align: top; text-align: left;\">")
                        entry.value.forEach { group ->
                            builder.append(group.javaClass.simpleName + "<br>")
                            builder.append(group.comments() + "<br><br>")
                        }
                    }

            builder.append("</table>")
            FileUtils.appendToFile(filename, builder.toString())
        }

        fun saveSignal(filename: String) {
            val output = log.toString()
            if (!output.isEmpty()) {
                FileUtils.appendToFile(filename, output)
            }
        }

        fun saveCodes(folder: String) {
            codes.forEach { code, symbols ->
                val s = symbols
                        .map { it.symbol }
                        .joinToString { System.lineSeparator() }
                FileUtils.appendToFile(folder + "/" + code.name.lowercase(Locale.getDefault()) + ".txt", s)
            }
        }

        fun clear() {
            log.clear()
            debug.clear();
            codes.clear()
        }

        fun addSummary(name: String, blocksGroup: BlocksGroup, symbol: String) {
            val key = Key(name, blocksGroup)
            summary.putIfAbsent(key, mutableSetOf())
            summary[key]?.add(symbol)
        }

    }

    data class Key(val taskName: String, val blocksGroup: BlocksGroup) {
    }

}