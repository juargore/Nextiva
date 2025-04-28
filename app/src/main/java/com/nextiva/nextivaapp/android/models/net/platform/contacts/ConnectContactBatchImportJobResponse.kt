package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName

data class ConnectContactBatchImportJobResponse(@SerializedName("id") var id: String,
                                                @SerializedName("recordCount") var count: Int?,
                                                @SerializedName("jobType") var jobType: String?,
                                                @SerializedName("createdAt") var createdAt: String?)