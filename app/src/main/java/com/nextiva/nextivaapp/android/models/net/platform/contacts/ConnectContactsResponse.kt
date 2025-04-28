package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectContactsResponse(@SerializedName("totalCount") var totalCount: Int?,
                                   @SerializedName("first") var firstPage: String?,
                                   @SerializedName("prev") var previousPage: String?,
                                   @SerializedName("next") var nextPage: String?,
                                   @SerializedName("last") var lastPage: String?,
                                   @SerializedName("data") var contactItems: ArrayList<ConnectContact>?): Serializable