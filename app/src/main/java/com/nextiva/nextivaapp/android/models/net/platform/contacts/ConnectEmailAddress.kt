package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import java.io.Serializable

data class ConnectEmailAddress(
    @SerializedName("type") var type: String?,
    @SerializedName("label") var label: String?,
    @SerializedName("email") var email: String?,
) : Serializable {
    fun toEmailAddress(): EmailAddress? {
        return email?.let {
            EmailAddress(
                type = ConnectEmailType.fromString(this.type).numericType,
                address = this.email,
                label = this.label
            )
        }
    }

    companion object {
        fun fromEmailAddress(emailAddress: EmailAddress): ConnectEmailAddress? =
            emailAddress.address?.let { address ->
                ConnectEmailAddress(
                    type = ConnectEmailType.fromIntType(emailAddress.type).value,
                    label = emailAddress.label,
                    email = address
                )
            }
    }
}