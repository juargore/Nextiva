package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import java.io.Serializable

data class ConnectPhoneNumber(
    @SerializedName("type") var type: String?,
    @SerializedName("label") var label: String?,
    @SerializedName("number") var number: String?,
    @SerializedName("extension") var extension: String?
) : Serializable {
    fun toPhoneNumber(): PhoneNumber? =
        ConnectPhoneType.fromString(this.type).let { phoneNumberType ->
            return when {
                number?.isNotEmpty().orFalse() && extension?.isNotEmpty().orFalse() -> {
                    val strippedNumber = CallUtil.getStrippedPhoneNumber(number.orEmpty())
                    PhoneNumber(
                        type = phoneNumberType.numericType,
                        number = number + "x" + extension,
                        strippedNumber = strippedNumber,
                        label = label,
                        pinOne = null,
                        pinTwo = null
                    )
                }

                extension?.isNotEmpty().orFalse() -> PhoneNumber(
                    type = Enums.Contacts.PhoneTypes.WORK_EXTENSION,
                    number = extension,
                    strippedNumber = extension,
                    pinOne = null,
                    pinTwo = null,
                    label = label
                )

                else -> PhoneNumber(
                    type = phoneNumberType.numericType,
                    number = number,
                    label = label
                )
            }
        }

    companion object {
        fun fromPhoneNumber(phoneNumber: PhoneNumber): ConnectPhoneNumber? =
            phoneNumber.number?.let { number ->
                ConnectPhoneNumber(
                    type = ConnectPhoneType.fromIntType(phoneNumber.type).value,
                    label = phoneNumber.label,
                    number = number,
                    extension = null
                )
            }
    }
}