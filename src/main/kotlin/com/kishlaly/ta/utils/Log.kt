package com.kishlaly.ta.utils

import com.kishlaly.ta.model.BlockResultCode
import com.kishlaly.ta.model.BlockResultCodeJava
import com.kishlaly.ta.model.SymbolData
import com.kishlaly.ta.model.SymbolDataJava

class Log {

    companion object {

        private val log = StringBuilder()
        private val debug = StringBuilder()
        private val codes = mutableMapOf<BlockResultCode, List<SymbolData>>()
        private val summary = mutableMapOf<Key, Set<String>>()

    }

    class Key(val taskName: String, val) {

    }

}