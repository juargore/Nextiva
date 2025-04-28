package com.nextiva.nextivaapp.android.core.notifications.view

import android.content.Context
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.api.UiStartEndTimes
import com.nextiva.nextivaapp.android.core.notifications.viewstate.DailyHoursScheduleViewState
import com.nextiva.nextivaapp.android.core.notifications.viewstate.ScheduleStartEndTimeViewState
import com.nextiva.nextivaapp.android.databinding.ViewScheduleCustomCheckboxBinding
import java.util.Locale


class CustomHourScheduleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(
    ContextThemeWrapper(context, com.google.android.material.R.style.Theme_MaterialComponents),
    attrs,
    defStyleAttr
) {

    companion object {
        const val MAX_BREAK_TIME_VIEWS = 6
        const val SAME_HOURS_MONDAY_FRIDAY = 0
        const val CUSTOM_HOURS_DAY = 1
    }

    private val binding: ViewScheduleCustomCheckboxBinding =
        ViewScheduleCustomCheckboxBinding.inflate(LayoutInflater.from(context), this)

    var viewState: DailyHoursScheduleViewState =
        DailyHoursScheduleViewState().also { applyNewViewState(it) }
        set(value) {
            if (field != value) {
                applyNewViewState(value)
                field = value
            }
        }

    private fun applyNewViewState(newViewState: DailyHoursScheduleViewState) {

        if (newViewState.dailyHourSelectedOption == CUSTOM_HOURS_DAY) {
            binding.checkboxWeekSchedule.visibility = View.VISIBLE
            binding.checkboxWeekSchedule.text = newViewState.dayName
            binding.checkboxWeekSchedule.setOnCheckedChangeListener(null)
            binding.checkboxWeekSchedule.isChecked = newViewState.isChecked ?: false
            if (newViewState.isChecked == true) performCheckAction(
                newViewState.isChecked,
                newViewState
            )
            binding.checkboxWeekSchedule.setOnCheckedChangeListener { _, isChecked ->
                performCheckAction(isChecked, newViewState)
                newViewState.checkedChangeListener?.invoke(isChecked)
            }
        } else if (newViewState.dailyHourSelectedOption == SAME_HOURS_MONDAY_FRIDAY) {
            binding.checkboxWeekSchedule.visibility = View.GONE
            loadScheduleStartEndTimeView(newViewState)
        }
    }

    private fun performCheckAction(isChecked: Boolean?, newViewState: DailyHoursScheduleViewState) {
        if (isChecked == true) {
            checkedAction(newViewState)
        } else {
            unCheckedAction(newViewState)
        }
    }

    private fun unCheckedAction(newViewState: DailyHoursScheduleViewState) {
        binding.linearScheduleStartEndTime.visibility = View.GONE
        binding.linearScheduleStartEndTime.removeAllViews()
        newViewState.checkedChangeListener?.invoke(false)
    }

    private fun checkedAction(newViewState: DailyHoursScheduleViewState) {
        loadScheduleStartEndTimeView(newViewState)
    }

    private fun loadScheduleStartEndTimeView(newViewState: DailyHoursScheduleViewState) {
        binding.linearScheduleStartEndTime.visibility = View.VISIBLE

        addStartEndTimeView(
            context.getString(R.string.notification_create_schedule_start_time_label),
            context.getString(R.string.notification_create_schedule_end_time_label),
            true,
            newViewState
        )

        newViewState.day?.breaks?.forEachIndexed { index, _ ->
            addStartEndTimeView(
                "BREAK ${index + 1} START TIME",
                "BREAK ${index + 1} END TIME",
                false,
                newViewState,
            )
        }
    }

    private fun createStartEndTimeView(
        startTimeLabel: String,
        endTimeLabel: String,
        isFirstTime: Boolean,
        dailyHourScheduleViewState: DailyHoursScheduleViewState,
        breakItemPosition: Int
    ): ScheduleStartEndTimeView {
        val dailyHourSelectedOption = dailyHourScheduleViewState.dailyHourSelectedOption
        val dayName = dailyHourScheduleViewState.dayName
        val iconRes = if (isFirstTime) R.drawable.ic_add_circle else R.drawable.ic_remove_circle
        val uiStartEndTime = if (isFirstTime) {
            dailyHourScheduleViewState.day?.hours
        } else {
            dailyHourScheduleViewState.day?.breaks?.get(breakItemPosition)
        }
        return ScheduleStartEndTimeView(context).apply {
            viewState = ScheduleStartEndTimeViewState(
                startTimeLabel = startTimeLabel,
                endTimeLabel = endTimeLabel,
                validationErrorText = uiStartEndTime?.validationErrorText,
                startTime = uiStartEndTime?.startTime,
                endTime = uiStartEndTime?.endTime,
                shouldShowStartTimeError = uiStartEndTime?.isStartTimeError,
                shouldShowEndTimeError = uiStartEndTime?.isEndTimeError,
                iconRes = iconRes,
                index = binding.linearScheduleStartEndTime.childCount - 1,
                startTimeAccessibilityId = getStartTimeAccessibilityId(
                    dailyHourSelectedOption,
                    dayName,
                    startTimeLabel
                ),
                endTimeAccessibilityId = getEndTimeAccessibilityId(
                    dailyHourSelectedOption,
                    dayName,
                    endTimeLabel
                ),
                addRemoveBreakAccessibilityId = getAddRemoveBreakAccessibilityId(dailyHourSelectedOption,iconRes, dayName),
                addOnClickListener = {
                    val breakTime = UiStartEndTimes(
                        context.getString(R.string.notification_create_schedule_breaks_default_start_time),
                        context.getString(R.string.notification_create_schedule_breaks_default_end_time),
                    )
                    dailyHourScheduleViewState.day?.breaks?.add(
                        breakTime
                    )
                    addStartEndTimeView(
                        "BREAK ${binding.linearScheduleStartEndTime.childCount} START TIME",
                        "BREAK ${binding.linearScheduleStartEndTime.childCount} END TIME",
                        false,
                        dailyHourScheduleViewState
                    )
                    dailyHourScheduleViewState.onBreakItemAddedListener?.invoke(
                        dailyHourScheduleViewState.day
                    )
                },
                removeOnClickListener = {
                    dailyHourScheduleViewState.day?.breaks?.removeAt(breakItemPosition)
                    binding.linearScheduleStartEndTime.removeAllViews()
                    checkedAction(dailyHourScheduleViewState)
                    dailyHourScheduleViewState.onBreakItemRemovedListener?.invoke(
                        dailyHourScheduleViewState.day
                    )
                },
                startTimeTextChangedListener = createStartTimeTextWatcher(
                    isFirstTime,
                    dailyHourScheduleViewState,
                    if (isFirstTime) breakItemPosition - 1 else breakItemPosition
                ),
                endTimeTextChangedListener = createEndTimeTextWatcher(
                    isFirstTime,
                    dailyHourScheduleViewState,
                    if (isFirstTime) breakItemPosition - 1 else breakItemPosition
                )
            )
        }
    }

    private fun getAddRemoveBreakAccessibilityId(
        dailyHourSelectedOption: Int,
        iconRes: Int,
        dayName: String?
    ): String {
        val breakNumber = binding.linearScheduleStartEndTime.childCount.toString()
        if (dailyHourSelectedOption == CUSTOM_HOURS_DAY) {
            if (iconRes == R.drawable.ic_add_circle) {
                return context.getString(
                    R.string.create_schedule_custom_hours_add_break_accessibility_id,
                    dayName
                )
            } else {
                return context.getString(
                    R.string.create_schedule_custom_hours_remove_break_accessibility_id,
                    dayName,
                    breakNumber
                )
            }
        }
        return ""
    }

    private fun getEndTimeAccessibilityId(
        dailyHourSelectedOption: Int,
        dayName: String?,
        endTimeLabel: String
    ): String {
        val prefix = context.getString(R.string.notification_create_schedule_prefix_break)
        val breakNumber = binding.linearScheduleStartEndTime.childCount.toString()
        val isBreakTime = endTimeLabel.lowercase(Locale.ROOT).startsWith(prefix)
        return when (dailyHourSelectedOption) {
            SAME_HOURS_MONDAY_FRIDAY -> {
                if (isBreakTime) {
                    context.getString(R.string.create_schedule_same_hours_break_end_time_accessibility_id, breakNumber)
                } else {
                    context.getString(R.string.create_schedule_same_hours_end_time_accessibility_id)
                }
            }
            else -> {
                if (isBreakTime) {
                    context.getString(R.string.create_schedule_custom_hours_break_end_time_accessibility_id, dayName, breakNumber)
                } else {
                    context.getString(R.string.create_schedule_custom_hours_end_time_accessibility_id, dayName)
                }
            }
        }
    }

    private fun getStartTimeAccessibilityId(
        dailyHourSelectedOption: Int,
        dayName: String?,
        startTimeLabel: String
    ): String {
        val prefix = context.getString(R.string.notification_create_schedule_prefix_break)
        val breakNumber = binding.linearScheduleStartEndTime.childCount.toString()
        val isBreakTime = startTimeLabel.lowercase(Locale.ROOT).startsWith(prefix)
        return when (dailyHourSelectedOption) {
            SAME_HOURS_MONDAY_FRIDAY -> {
                if (isBreakTime) {
                    context.getString(R.string.create_schedule_same_hours_break_start_time_accessibility_id, breakNumber)
                } else {
                    context.getString(R.string.create_schedule_same_hours_start_time_accessibility_id)
                }
            }
            else -> {
                if (isBreakTime) {
                    context.getString(R.string.create_schedule_custom_hours_break_start_time_accessibility_id, dayName, breakNumber)
                } else {
                    context.getString(R.string.create_schedule_custom_hours_start_time_accessibility_id, dayName)
                }
            }
        }
    }

    private fun createEndTimeTextWatcher(
        isFirstTime: Boolean,
        dailyHourScheduleViewState: DailyHoursScheduleViewState,
        breakItemIndex: Int
    ) = object : TextWatcher {
        override fun beforeTextChanged(
            p0: CharSequence?,
            p1: Int,
            p2: Int,
            p3: Int
        ) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (isFirstTime) {
                dailyHourScheduleViewState.day?.hours?.endTime = p0.toString()
            } else {
                dailyHourScheduleViewState.day?.breaks?.get(breakItemIndex)?.endTime =
                    p0.toString()
            }
            dailyHourScheduleViewState.timeChangedListener?.invoke(
                dailyHourScheduleViewState.day, isFirstTime
            )
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }

    private fun createStartTimeTextWatcher(
        isFirstTime: Boolean,
        dailyHourScheduleViewState: DailyHoursScheduleViewState,
        breakItemIndex: Int
    ) =
        object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isFirstTime) {
                    dailyHourScheduleViewState.day?.hours?.startTime = p0.toString()
                } else {
                    dailyHourScheduleViewState.day?.breaks?.get(breakItemIndex)?.startTime =
                        p0.toString()
                }
                dailyHourScheduleViewState.timeChangedListener?.invoke(
                    dailyHourScheduleViewState.day, isFirstTime
                )
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }

    private fun addStartEndTimeView(
        startTimeLabel: String,
        endTimeLabel: String,
        isFirstTime: Boolean,
        viewState: DailyHoursScheduleViewState,
    ) {
        if (binding.linearScheduleStartEndTime.childCount < MAX_BREAK_TIME_VIEWS) {
            binding.linearScheduleStartEndTime.addView(
                createStartEndTimeView(
                    startTimeLabel,
                    endTimeLabel,
                    isFirstTime,
                    viewState,
                    binding.linearScheduleStartEndTime.childCount - 1
                )
            )
        }
        if (binding.linearScheduleStartEndTime.childCount == MAX_BREAK_TIME_VIEWS) {
            disableAddBreakTimeImage()
        }
    }

    private fun disableAddBreakTimeImage() {
        val color = ContextCompat.getColor(context, R.color.disabledTextGrey)
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_add_circle)
        drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.linearScheduleStartEndTime.getChildAt(0).findViewById<ImageView>(R.id.img_add_remove_break_time).apply {
            setImageDrawable(drawable)
            isEnabled = false
        }
    }
}