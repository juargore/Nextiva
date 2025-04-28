package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.db.model.Address
import com.nextiva.nextivaapp.android.util.extensions.BiLet
import com.nextiva.nextivaapp.android.util.extensions.default
import java.io.Serializable

data class ConnectAddress(
    @SerializedName("street") var street: String?,
    @SerializedName("streetLine2") var streetLine2: String?,
    @SerializedName("city") var city: String?,
    @SerializedName("state") var state: String?,
    @SerializedName("zip") var zip: String?,
    @SerializedName("country") var country: String?,
    @SerializedName("type") var type: String?,
    @SerializedName("label") var label: String?
) : Serializable {

    fun toAddress(): Address? {
        return BiLet(street, city)?.let { _ ->
            Address(
                addressLineOne = street,
                addressLineTwo = streetLine2,
                postalCode = zip,
                city = city,
                region = state,
                country = country,
                location = null,
                type = ConnectAddressType.fromString(type).numericType,
                transactionId = null,
            )
        }
    }

    companion object {
        fun fromAddress(address: Address): ConnectAddress? =
            BiLet(address.addressLineOne, address.city)?.let { _ ->
                val addressType = ConnectAddressType.fromIntType(address.type.default(-1))
                ConnectAddress(
                    street = address.addressLineOne,
                    streetLine2 = address.addressLineTwo,
                    city = address.city,
                    state = address.region,
                    zip = address.postalCode,
                    country = address.country,
                    type = addressType.value,
                    label = addressType.name
                )
            }
    }
}