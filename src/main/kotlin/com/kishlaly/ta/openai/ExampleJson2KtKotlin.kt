package com.kishlaly.ta.openai

import com.google.gson.annotations.SerializedName

data class ExampleJson2KtKotlin(

    @SerializedName("id") var id: String? = null,
    @SerializedName("object") var object: String? = null,
    @SerializedName("created") var created: Int? = null,
    @SerializedName("model") var model: String? = null,
    @SerializedName("choices") var choices: ArrayList<Choices> = arrayListOf(),
    @SerializedName("usage") var usage: Usage? = Usage()

)