package com.nextiva.nextivaapp.android.models.net

import com.google.gson.annotations.SerializedName

class PendoRequestBody(filter: String) {
    @SerializedName("response" ) var response : Response = Response()
    @SerializedName("request"  ) var request  : Request = Request(filter)

    data class Response(@SerializedName("mimeType") var mimeType: String = "application/json")
    data class Source(@SerializedName("source") val source: Visitors = Visitors()) {
        data class Visitors(@SerializedName("visitors") var visitors: BlackList = BlackList()) {
            data class BlackList(@SerializedName("blacklist") var blacklist: String = "ignore")
        }
    }
    data class Filter(@SerializedName("filter") val filter: String)
    data class Metadata(@SerializedName("select") val select: Select = Select()) {
        data class Select(
            @SerializedName("Metadata") val metadata: String = "metadata",
            @SerializedName("First Web Visit") val firstWebVisit: String = "metadata.auto__323232.firstvisit",
            @SerializedName("First Android Native Visit") val firstAndroidNativeVisit: String = "metadata.auto_4515977926410240.firstvisit",
            @SerializedName("First iOS Native Visit") val firstiOSNativeVisit: String = "metadata.auto_6137634902376448.firstvisit",
            @SerializedName("Visitor ID") val visitorId: String = "visitorId",
            @SerializedName("Account Id") val accountId: String = "metadata.auto.accountid",
        )
    }
    class Request(filter: String) {
        @SerializedName("name")
        val name: String = "Visitor First Per App"

        @SerializedName("pipeline")
        var pipeline: ArrayList<Any> = arrayListOf(Source(), Filter(filter = "visitorId == `$filter`"), Metadata())
    }
}

class PendoResponseBody {
    @SerializedName("results")
    val results: ArrayList<Result>? = null
    class Result {
        @SerializedName("First Android Native Visit") val firstAndroidNativeVisit: String? = null
        @SerializedName("Metadata") val metadata: Metadata? = null
        class Metadata {
            @SerializedName("auto") val auto: Auto? = null
            class Auto(@SerializedName("firstvisit") val firstvisit: String? = null)
        }
    }
}