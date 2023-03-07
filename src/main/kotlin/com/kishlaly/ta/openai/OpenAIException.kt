package com.kishlaly.ta.openai

class OpenAIException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
}