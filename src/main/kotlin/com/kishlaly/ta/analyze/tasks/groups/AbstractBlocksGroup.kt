package com.kishlaly.ta.analyze.tasks.groups

abstract class AbstractBlocksGroup : BlocksGroup {

    override fun comments(): String {
        return this.javaClass.simpleName + ": "
    }

}