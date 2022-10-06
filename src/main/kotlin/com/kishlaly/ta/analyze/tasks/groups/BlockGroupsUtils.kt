package com.kishlaly.ta.analyze.tasks.groups

import com.kishlaly.ta.analyze.TaskType
import org.reflections.Reflections
import java.util.*

class BlockGroupsUtils {

    companion object {
        fun getAllGroups(taskType: TaskType): List<BlocksGroup> {
            return when (taskType) {
                TaskType.THREE_DISPLAYS_BUY -> findClasses(
                    "com.kishlaly.ta.analyze.tasks.blocks.groups.threedisplays",
                    "buy"
                )
                TaskType.THREE_DISPLAYS_SELL -> findClasses(
                    "com.kishlaly.ta.analyze.tasks.blocks.groups.threedisplays",
                    "sell"
                )
                TaskType.MACD_BULLISH_DIVERGENCE -> findClasses(
                    "com.kishlaly.ta.analyze.tasks.blocks.groups.divergencies",
                    ""
                )
                TaskType.FIRST_TRUST_MODEL -> findClasses(
                    "com.kishlaly.ta.analyze.tasks.blocks.groups.trustmodel",
                    ""
                )
                else -> listOf()
            }
        }

        private fun findClasses(pck: String, vararg traits: String): List<BlocksGroup> {
            val reflections = Reflections(pck)
            return reflections.getSubTypesOf(BlocksGroup::class.java)
                .filter { clazz -> traits.all { clazz.simpleName.lowercase().contains(it) } }
                .map { it.constructors.first().newInstance() as BlocksGroup }
                .toList()
        }

        fun allMatch(source: String, patterns: Array<String>): Boolean {
            return Arrays.stream(patterns).allMatch { s: CharSequence? -> source.contains(s!!) }
        }
    }

}

fun main() {
    BlockGroupsUtils.getAllGroups(TaskType.THREE_DISPLAYS_SELL).forEach { println(it.javaClass) }
}