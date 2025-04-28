package com.nextiva.nextivaapp.android.core.notifications.models

import com.nextiva.nextivaapp.android.core.notifications.api.Holiday
import java.io.Serializable

data class HolidaySelection(
    val holiday: Holiday,
    var isSelected: Boolean
): Serializable