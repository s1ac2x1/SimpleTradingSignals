package com.kishlaly.ta.model

import org.ktorm.schema.*

object TestingsDBO : Table<Nothing>("testings") {
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