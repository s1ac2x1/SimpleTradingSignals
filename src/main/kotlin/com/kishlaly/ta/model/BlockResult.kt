package com.kishlaly.ta.model

data class BlockResult(var lastChartQuote: Quote, val code: BlockResultCode) {

    fun isOk() = code == BlockResultCode.OK

    companion object {
        fun configurationError(lastQuote: Quote) = BlockResult(lastQuote, BlockResultCode.CONFIGURATION_ERROR)
    }

}