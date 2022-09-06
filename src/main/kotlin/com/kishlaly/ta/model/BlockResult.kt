package com.kishlaly.ta.model

data class BlockResult(val lastChartQuote: Quote, val code: BlockResultCode) {

    fun isOk() = code == BlockResultCode.OK

}