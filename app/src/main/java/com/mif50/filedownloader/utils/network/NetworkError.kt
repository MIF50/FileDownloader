package com.mif50.filedownloader.utils.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkError(
    val code: Int = -1,

    @Json(name = "response")
    val response: Int = -1,

    @Json(name = "message")
    val message: String = "Something went wrong"
)