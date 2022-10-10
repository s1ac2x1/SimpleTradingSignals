package com.kishlaly.ta.analyze.tasks.groups

abstract class AbstractBlocksGroup : BlocksGroup {

    constructor() {
        init()
    }

    override fun comments(): String {
        return this.javaClass.simpleName + ": "
    }

    override fun init() {
        // empty
    }

}