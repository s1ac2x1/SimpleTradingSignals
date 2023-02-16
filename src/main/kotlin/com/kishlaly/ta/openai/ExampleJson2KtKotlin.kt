package com.kishlaly.ta.openai

import com.google.gson.annotations.SerializedName

data class CompletionRespone(

    @SerializedName("id") var id: String? = null,
    @SerializedName("object") var obj: String? = null,
    @SerializedName("created") var created: Int? = null,
    @SerializedName("model") var model: String? = null,
    @SerializedName("choices") var choices: ArrayList<Choices> = arrayListOf(),
    @SerializedName("usage") var usage: Usage? = Usage()

)