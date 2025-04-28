package com.nextiva.nextivaapp.android.core.notifications.utils

import java.text.SimpleDateFormat
import java.util.Locale

object Utils {

     fun formatScheduleTimeTo24hr(time: String): String? {
        val sdfOutPut = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdfInput = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val date = sdfInput.parse(time)
        return date?.let { sdfOutPut.format(it) }
    }
}