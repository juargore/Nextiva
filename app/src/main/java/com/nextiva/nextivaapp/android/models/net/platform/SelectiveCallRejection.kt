package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.models.net.platform.Schedule.Companion.getSchedule
import java.io.Serializable

data class SelectiveCallRejection(
    @SerializedName("active")
    var active: Boolean = true,

    @SerializedName("availableScheduleOptions")
    var availableScheduleOptions: ArrayList<Schedule>? = arrayListOf(getSchedule()),

    @SerializedName("currentSelectiveCallRejectionConditions")
    var currentSelectiveCallRejectionConditions: ArrayList<CurrentSelectiveCallRejectionConditions>?
)

data class CurrentSelectiveCallRejectionConditions(
    @SerializedName("active") var active: Boolean = true,
    @SerializedName("anyPrivateNumber") var anyPrivateNumber: Boolean = false,
    @SerializedName("anyUnavailableNumber") var anyUnavailableNumber: Boolean = false,
    @SerializedName("description") var description: String?,
    @SerializedName("fromAnyPhoneNumber") var fromAnyPhoneNumber: Boolean = false,
    @SerializedName("fromForwardedCalls") var fromForwardedCalls: Boolean = false,
    @SerializedName("fromSpecificNumbers") var fromSpecificNumbers: Boolean = true,
    @SerializedName("numbers") var numbers: ArrayList<String>?,
    @SerializedName("oldDescription") var oldDescription: String? = null,
    @SerializedName("rejectNumbers") var rejectNumbers: Boolean = true,
    @SerializedName("schedule") var schedule: Schedule? = getSchedule(),
) : Serializable

data class Schedule(
    @SerializedName("scheduleLevel") var scheduleLevel: String?,
    @SerializedName("scheduleName") var scheduleName: String?
) {
    companion object {
        fun getSchedule() = Schedule(
            scheduleLevel = "USER",
            scheduleName = "Every Day All Day"
        )
    }
}
