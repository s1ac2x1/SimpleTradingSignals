package com.kishlaly.ta.cache

import com.google.gson.Gson
import com.kishlaly.ta.analyze.TaskTypeJava
import com.kishlaly.ta.model.Timeframe
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.Future

class CacheReader {

    companion object {
        var gson = Gson()
        var queueExecutor = Executors.newScheduledThreadPool(1)
        var apiExecutor = Executors.newCachedThreadPool()
        var requestPeriod = 0
        var requests = ConcurrentLinkedDeque<LoadRequestJava>()
        var callsInProgress = CopyOnWriteArrayList<Future<*>>()

        fun checkCache(timeframes: Array<Array<Timeframe>>, tasks: Array<TaskTypeJava>) {

        }
    }

}