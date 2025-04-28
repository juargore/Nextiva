package com.nextiva.nextivaapp.android.core.notifications.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.viewstate.CustomDateScheduleStartEndTimeViewState
import com.nextiva.nextivaapp.android.databinding.ViewScheduleCustomDateTimeBinding

class TimeScheduleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(
    ContextThemeWrapper(context, com.google.android.material.R.style.Theme_MaterialComponents),
    attrs,
    defStyleAttr
) {
    companion object {
    }

    private val binding: ViewScheduleCustomDateTimeBinding =
        ViewScheduleCustomDateTimeBinding.inflate(LayoutInflater.from(context), this)

    var viewState: CustomDateScheduleStartEndTimeViewState =
        CustomDateScheduleStartEndTimeViewState().also { applyNewViewState(it) }
        set(value) {
            if (field != value) {
                applyNewViewState(value)
                field = value
            }
        }

    private fun applyNewViewState(newViewState: CustomDateScheduleStartEndTimeViewState) {
        checkedAction(newViewState)
    }

    private fun checkedAction(newViewState: CustomDateScheduleStartEndTimeViewState) {
        binding.linearScheduleStartEndTime.visibility = View.VISIBLE
        addStartEndTimeView(
            context.getString(R.string.notification_create_schedule_start_time_label),
            context.getString(R.string.notification_create_schedule_end_time_label),
            newViewState
        )
    }

    private fun createStartEndTimeView(
        startTimeLabel: String,
        endTimeLabel: String,
        dailyHourScheduleViewState: CustomDateScheduleStartEndTimeViewState,
    ): CustomDateScheduleStartEndTimeView {
        return CustomDateScheduleStartEndTimeView(context).apply {
            viewState = CustomDateScheduleStartEndTimeViewState(
                startTimeLabel = startTimeLabel,
                endTimeLabel = endTimeLabel,
                validationErrorText = dailyHourScheduleViewState.validationErrorText,
                startTime = dailyHourScheduleViewState.startTime,
                endTime = dailyHourScheduleViewState.endTime,
                shouldShowStartTimeError = dailyHourScheduleViewState.shouldShowStartTimeError,
                shouldShowEndTimeError = dailyHourScheduleViewState.shouldShowEndTimeError,


                startTimeTextChangedListener = createStartTimeTextWatcher(
                    dailyHourScheduleViewState
                ),
                endTimeTextChangedListener = createEndTimeTextWatcher(
                    dailyHourScheduleViewState
                )
            )
        }
    }

    private fun createEndTimeTextWatcher(
        dailyHourScheduleViewState: CustomDateScheduleStartEndTimeViewState,
    ) = object : TextWatcher {
        override fun beforeTextChanged(
            p0: CharSequence?,
            p1: Int,
            p2: Int,
            p3: Int,
        ) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }

    private fun createStartTimeTextWatcher(
        dailyHourScheduleViewState: CustomDateScheduleStartEndTimeViewState,
    ) =
        object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int,
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }

    private fun addStartEndTimeView(
        startTimeLabel: String,
        endTimeLabel: String,
        viewState: CustomDateScheduleStartEndTimeViewState,
    ) {
        binding.linearScheduleStartEndTime.addView(
            createStartEndTimeView(
                startTimeLabel,
                endTimeLabel,
                viewState
            )
        )
    }
}