package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectContactPrimaryAccount(@SerializedName("id") var id: String?,
                            @SerializedName("name") var name: String?): Serializable