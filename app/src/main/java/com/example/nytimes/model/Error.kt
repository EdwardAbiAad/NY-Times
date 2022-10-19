package com.example.nytimes.model
import com.google.gson.annotations.SerializedName


data class Error(
    @SerializedName("fault")
    var fault: Fault? = null
)

data class Fault(
    @SerializedName("detail")
    var detail: Detail? = null,
    @SerializedName("faultstring")
    var faultstring: String? = null
)

data class Detail(
    @SerializedName("errorcode")
    var errorcode: String? = null
)