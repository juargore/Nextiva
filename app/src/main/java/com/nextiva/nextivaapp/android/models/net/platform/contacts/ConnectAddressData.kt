package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.db.model.Address
import java.io.Serializable

data class ConnectAddressData(@SerializedName("street") var street1: String?,
                              @SerializedName("streetLine2") var street2: String?,
                              @SerializedName("city") var city: String?,
                              @SerializedName("state") var state: String?,
                              @SerializedName("zip") var zip: String?,
                              @SerializedName("country") var country: String?): Serializable {
    fun hasData(): Boolean {
        return when {
            !street1.isNullOrBlank() ||
                    !street2.isNullOrBlank() ||
                    !city.isNullOrBlank() ||
                    !state.isNullOrBlank() ||
                    !zip.isNullOrBlank() ||
                    !country.isNullOrBlank() -> true
            else -> false
        }
    }

    companion object {
        fun fromAddress(address: Address?): ConnectAddressData? {
            address?.let {
                if (address.hasNonNullValue()) {
                    return ConnectAddressData(address.addressLineOne,
                            address.addressLineTwo,
                            address.city,
                            address.region,
                            address.postalCode,
                            address.country)
                }
            }

            return null
        }
    }
}