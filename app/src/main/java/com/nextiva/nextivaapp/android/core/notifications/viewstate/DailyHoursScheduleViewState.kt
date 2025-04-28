package com.nextiva.nextivaapp.android.core.notifications.viewstate

import android.widget.CompoundButton.OnCheckedChangeListener
import com.nextiva.nextivaapp.android.core.notifications.api.UiDaySchedule

data class DailyHoursScheduleViewState constructor(
    val day:UiDaySchedule? = null,
    val dayName: String? = null,
    val isChecked: Boolean? = false,
    val dayCheckListener: OnCheckedChangeListener? = null,
    val timeChangedListener: ((UiDaySchedule?, Boolean) -> Unit)? = null,
    val checkedChangeListener: ((Boolean) -> Unit)? = null,
    val onBreakItemRemovedListener: ((UiDaySchedule?) -> Unit)? = null,
    val onBreakItemAddedListener: ((UiDaySchedule?) -> Unit)? = null,
    val dailyHourSelectedOption: Int = 1
)
