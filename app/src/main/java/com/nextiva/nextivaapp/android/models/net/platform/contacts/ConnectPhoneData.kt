package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import java.io.Serializable

data class ConnectPhoneData(@SerializedName("number") var number: String?,
                            @SerializedName("extension") var extension: String?): Serializable {
    fun mapToPhoneNumber(type: Int): PhoneNumber? {
        number?.let { return PhoneNumber(type, number) }
        extension?.let { return PhoneNumber(type, extension) }
        return null
    }
}