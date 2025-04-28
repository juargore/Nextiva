package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectCreatedBy(@SerializedName("id") var id: String?,
                            @SerializedName("firstName") var firstName: String?,
                            @SerializedName("lastName") var lastName: String?,
                            @SerializedName("email") var email: String?,
                            @SerializedName("loginId") var loginId: String?,
                            @SerializedName("telephoneNumber") var telephoneNumber: String?): Serializable