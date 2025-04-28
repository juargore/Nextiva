package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsStatus
import java.io.Serializable

data class BroadsoftContactStorageResponse(@SerializedName("status") var responseStatus: BroadsoftUmsStatus?,
                                           @SerializedName("contactstorage-ts") var contactStorageTimestamp: String?,
                                           @SerializedName("contactstorage") var contactStorage: String?) : Serializable