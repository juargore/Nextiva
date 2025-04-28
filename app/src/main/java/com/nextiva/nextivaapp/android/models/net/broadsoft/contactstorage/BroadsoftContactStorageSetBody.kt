package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BroadsoftContactStorageSetBody(@SerializedName("cs-ts-blob") var timestamp: String? = "",
                                          @SerializedName("contactstorage") var contactStorage: String? = "") : Serializable