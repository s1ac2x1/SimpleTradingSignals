package com.kishlaly.ta.utils

import com.kishlaly.ta.config.Context
import org.ktorm.database.Database

class DBUtils {

    companion object {
        fun initDB() {
            Context.useDBLogging = true
            Context.database =
                Database.connect("jdbc:postgresql://localhost:5432/test", user = "test", password = "test")
        }
    }

}