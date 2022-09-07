package com.kishlaly.ta.utils

import com.kishlaly.ta.analyze.tasks.blocks.groups.BlocksGroup
import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.SymbolData

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
                        entry.value.forEach { group: BlocksGroup ->
                            builder.append(group.javaClass.simpleName + "<br>")
                            builder.append(group.comments() + "<br><br>")
                        }
                    }

            builder.append("</table>")
            FileUtils.appendToFile(filename, builder.toString())
        }
    }

    data class Key(val taskName: String, val blocksGroup: BlocksGroup) {
    }

}