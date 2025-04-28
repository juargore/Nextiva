package com.nextiva.nextivaapp.android.core.notifications.viewstate

import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.DrawableRes
import com.nextiva.nextivaapp.android.R

data class ScheduleStartEndTimeViewState(
    val startTimeLabel: String? = null,
    val endTimeLabel: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val shouldShowStartTimeError: Boolean? = null,
    val shouldShowEndTimeError: Boolean? = null,
    val validationErrorText: String? = null,
    @DrawableRes val iconRes: Int? = null,
    val index: Int? = null,
    val addOnClickListenerWithIndex: ((Int, OnClickListener) -> Unit)? = null,
    val addOnClickListener: OnClickListener? = null,
    val removeOnClickListener: OnClickListener? = null,
    val startTimeTextChangedListener: TextWatcher? = null,
    val endTimeTextChangedListener: TextWatcher? = null,
    val startTimeAccessibilityId: String? = null,
    val endTimeAccessibilityId: String? = null,
    val addRemoveBreakAccessibilityId: String? = null,
) {
    val shouldShowErrorText =
        if (shouldShowStartTimeError == true || shouldShowEndTimeError == true) {
            View.VISIBLE
        } else {
            View.GONE
        }

    @DrawableRes
    val starTimeViewBackground = if (shouldShowStartTimeError == true) {
        R.drawable.connect_text_error_input_background
    } else {
        R.drawable.connect_text_input_background
    }

    @DrawableRes
    val endTimeViewBackground = if (shouldShowEndTimeError == true) {
        R.drawable.connect_text_error_input_background
    } else {
        R.drawable.connect_text_input_background
    }
}
