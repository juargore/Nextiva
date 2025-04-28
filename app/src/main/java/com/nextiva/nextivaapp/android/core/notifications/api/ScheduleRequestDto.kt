package com.nextiva.nextivaapp.android.core.notifications.api

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

data class UserScheduleRequest(
    @SerializedName("days") var days: Days,
    @SerializedName("holidays") var holidays: ArrayList<Holiday>?,
    // @SerializedName("locationId") var location: Location?,
    @SerializedName("name") var name: String?,
    @SerializedName("ownerId") var ownerId: String?,
    @SerializedName("ownerType") var ownerType: String?,
    @SerializedName("scheduleLevel") var scheduleLevel: ScheduleLevel?,
    @SerializedName("scheduleType") var scheduleType: ScheduleType?,
    @SerializedName("status") var status: Boolean?
)

data class UiStartEndTimes(
    @SerializedName("startTime") var startTime: String,
    @SerializedName("endTime") var endTime: String,
    @SerializedName("isStartTimeError") var isStartTimeError: Boolean? = null,
    @SerializedName("isEndTimeError") var isEndTimeError: Boolean? = null,
    @SerializedName("validationErrorText") var validationErrorText: String? = null,
    @SerializedName("validationErrorTypeList") var validationErrorTypeList: MutableSet<String> = mutableSetOf(),

    ) {
    fun getFormattedStartTime() = startTime.formatScheduleTimeTo12hr()


    fun getFormattedEndTime() = endTime.formatScheduleTimeTo12hr()

    private fun String.formatScheduleTimeTo12hr(): Date? {
        val sdf = SimpleDateFormat("hh:mm a")
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return sdf.parse(this)
            ?.let { date ->
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.add(Calendar.HOUR_OF_DAY, 12)
                calendar.time
            }

    }
}

data class UiDaySchedule(
    @SerializedName("breaks") var breaks: ArrayList<UiStartEndTimes>,
    @SerializedName("hours") var hours: UiStartEndTimes
)
