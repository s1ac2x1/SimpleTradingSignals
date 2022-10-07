package com.kishlaly.ta.model

import org.ktorm.entity.Entity
import org.ktorm.schema.*

interface Testing : Entity<Testing> {
    val id: Int
    val symbol: String
    val task_blocks: String
    val sl_strategy: String
    val tp_strategy: String
    val balance: Double
    val successful_ratio: Double
    val loss_ratio: Double
    val all_positions_count: Int
    val profitable_positions_count: Int
    val loss_positions_count: Int
    val min_position_duration_seconds: Long
    val average_position_duration_seconds: Double
    val max_position_duration_seconds: Long
    val min_profit: Double
    val avg_profit: Double
    val max_profit: Double
    val min_loss: Double
    val avg_loss: Double
    val max_loss: Double
    val total_profit: Double
    val average_roi: Double
    val signal_stats: String
}

interface SignalResult : Entity<SignalResult> {
    val id: Int
    val testing: Testing
    val result: String
}

object Testings : Table<Testing>("testings") {
    val id = int("id").primaryKey()
    val symbol = varchar("symbol")
    val task_blocks = text("task_blocks")
    val sl_strategy = text("sl_strategy")
    val tp_strategy = text("tp_strategy")
    val balance = double("balance")
    val successful_ratio = double("successful_ratio")
    val loss_ratio = double("loss_ratio")
    val all_positions_count = int("all_positions_count")
    val profitable_positions_count = int("profitable_positions_count")
    val loss_positions_count = int("loss_positions_count")
    val min_position_duration_seconds = long("min_position_duration_seconds")
    val average_position_duration_seconds = double("average_position_duration_seconds")
    val max_position_duration_seconds = long("max_position_duration_seconds")
    val min_profit = double("min_profit")
    val avg_profit = double("avg_profit")
    val max_profit = double("max_profit")
    val min_loss = double("min_loss")
    val avg_loss = double("avg_loss")
    val max_loss = double("max_loss")
    val total_profit = double("total_profit")
    val average_roi = double("average_roi")
    val signal_stats = text("signal_stats")
}

object SignalResults : Table<SignalResult>("signal_results") {
    val id = int("id").primaryKey()
    val testings_id = int("testings_id").references(Testings) { it.testing }
    val result = text("result")
}

