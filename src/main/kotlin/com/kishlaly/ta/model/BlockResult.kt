package com.kishlaly.ta.model

data class BlockResult(var lastChartQuote: Quote, val code: BlockResultCode) {

    fun isOk() = code == BlockResultCode.OK

}