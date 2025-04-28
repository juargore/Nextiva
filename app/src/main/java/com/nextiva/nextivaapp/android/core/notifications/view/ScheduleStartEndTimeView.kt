package com.nextiva.nextivaapp.android.core.notifications.view

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.viewstate.ScheduleStartEndTimeViewState
import com.nextiva.nextivaapp.android.databinding.ViewScheduleStartEndTimeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ScheduleStartEndTimeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(
    ContextThemeWrapper(context, R.style.AppTheme), attrs, defStyleAttr
) {

    companion object {
        const val START_TIME_VIEW = "START_TIME_VIEW"
        const val END_TIME_VIEW = "END_TIME_VIEW"
    }

    private val binding: ViewScheduleStartEndTimeBinding =
        ViewScheduleStartEndTimeBinding.inflate(LayoutInflater.from(context), this, true)

    var viewState: ScheduleStartEndTimeViewState =
        ScheduleStartEndTimeViewState().also { applyNewViewState(it) }
        set(value) {
            if (field != value) {
                applyNewViewState(value)
                field = value
            }
        }

    private fun applyNewViewState(newViewState: ScheduleStartEndTimeViewState) {
        val typeface: Typeface? = ResourcesCompat.getFont(context, R.font.lato_bold)
        //Start time view
        binding.txtStartTimeLabel.text = newViewState.startTimeLabel
        binding.txtStartTimeLabel.typeface = typeface
        binding.txtStartTimeLabel.setTextColor(
            if (newViewState.shouldShowStartTimeError == true) ContextCompat.getColor(
                context, R.color.connectSecondaryRed
            ) else ContextCompat.getColor(context, R.color.connectGrey10)
        )

        binding.txtStartTimeView.removeTextChangedListener(newViewState.startTimeTextChangedListener)
        binding.txtStartTimeView.text = newViewState.startTime
        binding.txtStartTimeView.background =
            AppCompatResources.getDrawable(context, newViewState.starTimeViewBackground)

        //End time view
        binding.txtEndTimeLabel.text = newViewState.endTimeLabel
        binding.txtEndTimeLabel.typeface = typeface
        binding.txtEndTimeLabel.setTextColor(
            if (newViewState.shouldShowEndTimeError == true) ContextCompat.getColor(
                context, R.color.connectSecondaryRed
            ) else ContextCompat.getColor(context, R.color.connectGrey10)
        )

        binding.txtEndTimeView.removeTextChangedListener(newViewState.endTimeTextChangedListener)
        binding.txtEndTimeView.text = newViewState.endTime
        binding.txtEndTimeView.background =
            AppCompatResources.getDrawable(context, newViewState.endTimeViewBackground)

        //Icon View
        newViewState.iconRes?.let { iconRes ->
            binding.imgAddRemoveBreakTime.setImageResource(iconRes)
        }

        //Error view
        binding.txtValidationError.visibility = newViewState.shouldShowErrorText
        binding.txtValidationError.text = newViewState.validationErrorText


        //listeners
        binding.txtEndTimeView.setOnClickListener {
            showTimePicker(newViewState, END_TIME_VIEW)
        }

        binding.imgAddRemoveBreakTime.setOnClickListener(
            if (newViewState.iconRes == R.drawable.ic_add_circle) newViewState.addOnClickListener else newViewState.removeOnClickListener
        )

        binding.txtStartTimeView.setOnClickListener {
            showTimePicker(newViewState, START_TIME_VIEW)
        }
        newViewState.startTimeTextChangedListener?.let {
            binding.txtStartTimeView.addTextChangedListener(it)
        }

        newViewState.endTimeTextChangedListener?.let {
            binding.txtEndTimeView.addTextChangedListener(it)
        }

        //accessibility
        binding.txtStartTimeView.contentDescription = newViewState.startTimeAccessibilityId
        binding.txtEndTimeView.contentDescription = newViewState.endTimeAccessibilityId
        binding.imgAddRemoveBreakTime.contentDescription = newViewState.addRemoveBreakAccessibilityId
        
    }

    private fun showTimePicker(
        newViewState: ScheduleStartEndTimeViewState,
        selectedTimeView: String
    ) {
        val time: String? = if (selectedTimeView == START_TIME_VIEW) {
            newViewState.startTime
        } else newViewState.endTime
        val hourMinPair: Pair<Int, Int> = if (time.isNullOrEmpty()) {
            Pair(9, 0)
        } else {
            getHourMinuteOfDay(time)
        }
        val calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
            context, { _, hourOfDay, minuteOfDay ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minuteOfDay)
                val selectedTime12Hr =
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                val formattedTime = selectedTime12Hr.format(calendar.time)
                if (selectedTimeView == START_TIME_VIEW) {
                    binding.txtStartTimeView.text = formattedTime.lowercase()
                } else {
                    binding.txtEndTimeView.text = formattedTime.lowercase()
                }
            }, hourMinPair.first, hourMinPair.second, false
        )
        dialog.show()
    }

    private fun getHourMinuteOfDay(time: String?): Pair<Int, Int> {
        var hour = 9
        var minute = 0
        if (!time.isNullOrEmpty()) {
            val parts = time.split(":")
            hour = parts[0].toIntOrNull() ?: 9
            minute = parts.getOrNull(1)?.substring(0, 2)?.toIntOrNull() ?: 0
        }
        return Pair(hour, minute)
    }
}