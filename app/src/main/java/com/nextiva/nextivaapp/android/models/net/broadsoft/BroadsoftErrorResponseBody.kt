package com.nextiva.nextivaapp.android.models.net.broadsoft

import android.content.Context
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "ErrorInfo", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
data class BroadsoftErrorResponseBody(@field:Element(name = "summary", required = false) var summary: String? = "",
                                      @field:Element(name = "summaryEnglish", required = false) var summaryEnglish: String? = "",
                                      @field:Element(name = "errorCode", required = false) var errorCode: String? = "") : Serializable {

    @Enums.Analytics.EventName.Event
    val eventName: String?
        get() {
            return when (errorCode) {
                Enums.CallSettings.ErrorCodes.DUPLICATE_NEXTIVA_ANYWHERE_LOCATION_EXISTS -> Enums.Analytics.EventName.DUPLICATE_NEXTIVA_ANYWHERE_LOCATION_EXISTS_DIALOG_SHOWN
                Enums.CallSettings.ErrorCodes.DUPLICATE_SIMULTANEOUS_RING_LOCATION_EXISTS -> Enums.Analytics.EventName.DUPLICATE_SIMULTANEOUS_RING_LOCATION_EXISTS_DIALOG_SHOWN
                else -> return null
            }
        }

    fun getDialogContent(context: Context): String? {
        return when (errorCode) {
            Enums.CallSettings.ErrorCodes.DUPLICATE_NEXTIVA_ANYWHERE_LOCATION_EXISTS -> context.getString(R.string.error_nextiva_anywhere_location_with_phone_number_exists_message)
            Enums.CallSettings.ErrorCodes.DUPLICATE_SIMULTANEOUS_RING_LOCATION_EXISTS -> context.getString(R.string.error_simultaneous_ring_location_with_phone_number_exists_message)
            else -> return null
        }
    }

    fun getDialogTitle(context: Context): String? {
        return when (errorCode) {
            Enums.CallSettings.ErrorCodes.DUPLICATE_NEXTIVA_ANYWHERE_LOCATION_EXISTS -> context.getString(R.string.error_location_with_phone_number_exists_title)
            Enums.CallSettings.ErrorCodes.DUPLICATE_SIMULTANEOUS_RING_LOCATION_EXISTS -> context.getString(R.string.error_location_with_phone_number_exists_title)
            else -> return null
        }
    }
}