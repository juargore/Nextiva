package com.nextiva.nextivaapp.android.core.notifications.viewmodel

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.nextiva.nextivaapp.android.NextivaApplication
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.notifications.api.Break
import com.nextiva.nextivaapp.android.core.notifications.api.Day
import com.nextiva.nextivaapp.android.core.notifications.api.DayOfWeek
import com.nextiva.nextivaapp.android.core.notifications.api.Days
import com.nextiva.nextivaapp.android.core.notifications.api.Holiday
import com.nextiva.nextivaapp.android.core.notifications.api.HolidayType
import com.nextiva.nextivaapp.android.core.notifications.api.HourOfWeek
import com.nextiva.nextivaapp.android.core.notifications.api.Interval
import com.nextiva.nextivaapp.android.core.notifications.api.Repeat
import com.nextiva.nextivaapp.android.core.notifications.api.ScheduleLevel
import com.nextiva.nextivaapp.android.core.notifications.api.ScheduleType
import com.nextiva.nextivaapp.android.core.notifications.api.SchedulesRepository
import com.nextiva.nextivaapp.android.core.notifications.api.UiDaySchedule
import com.nextiva.nextivaapp.android.core.notifications.api.UiStartEndTimes
import com.nextiva.nextivaapp.android.core.notifications.api.UserScheduleRequest
import com.nextiva.nextivaapp.android.core.notifications.api.UserScheduleResponse
import com.nextiva.nextivaapp.android.core.notifications.models.HolidaySelection
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.core.notifications.utils.ScheduleValidator
import com.nextiva.nextivaapp.android.core.notifications.utils.Utils
import com.nextiva.nextivaapp.android.core.notifications.view.CustomHourScheduleView.Companion.CUSTOM_HOURS_DAY
import com.nextiva.nextivaapp.android.core.notifications.view.CustomHourScheduleView.Companion.SAME_HOURS_MONDAY_FRIDAY
import com.nextiva.nextivaapp.android.core.notifications.viewstate.DailyHoursScheduleViewState
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateScheduleViewModel @Inject constructor(
    private val nextivaApplication: Application,
    val presenceRepository: PresenceRepository,
    private val scheduleRepository: SchedulesRepository,
    val sessionManager: SessionManager,
    var sharedPreferencesManager: SharedPreferencesManager
) : BaseViewModel(nextivaApplication) {
    var selectedSchedule: Schedule? = null
    var scheduleDeletedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var dailyHourSelectedOption: Int = 0
    val dailyHoursScheduleViewStateList = MutableLiveData(getCustomHoursScheduleViewStateList())
    var onSaveSuccess: MutableLiveData<String?> = MutableLiveData()

    var hasValidScheduleTimes: Boolean = true
    val scheduleNameValidationResult = MutableLiveData<String>()

    var isAllDay: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    var isEventRepeatsForever: MutableLiveData<Boolean> = MutableLiveData(true)
    var customOccurrence: MutableLiveData<String> = MutableLiveData()
    var customDay: MutableLiveData<String> = MutableLiveData()
    var customMonth: MutableLiveData<String> = MutableLiveData()
    var occurrences: MutableLiveData<String> = MutableLiveData()
    var customStartTime: MutableLiveData<LocalDateTime> = MutableLiveData()
    var customEndTime: MutableLiveData<LocalDateTime> = MutableLiveData()

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var schedulesRepository: SchedulesRepository

    private val scheduleValidator = ScheduleValidator(nextivaApplication as NextivaApplication)

    val observedHolidaySelectionList = MutableStateFlow(emptyList<HolidaySelection>())
    val holidays = MutableStateFlow(emptyList<HolidaySelection>())

    private fun validateBreaksWithHours(daySchedule: UiDaySchedule?, dayIndex: Int): Boolean {
        val isValidated = scheduleValidator.validateBreaksWithHours(daySchedule)
        dailyHoursScheduleViewStateList.value?.get(dayIndex)?.copy(
            day = daySchedule, isChecked = true
        )?.let { dailyHoursScheduleViewStateList.value?.set(dayIndex, it) }

        return isValidated
    }

    private fun validateBreaksOverlapping(daySchedule: UiDaySchedule?, dayIndex: Int): Boolean {
        val isValidated = scheduleValidator.validateBreaksOverlapping(daySchedule)
        dailyHoursScheduleViewStateList.value?.get(dayIndex)?.copy(
            day = daySchedule, isChecked = true
        )?.let { dailyHoursScheduleViewStateList.value?.set(dayIndex, it) }

        return isValidated
    }

    private fun validateEndHours(daySchedule: UiDaySchedule?, dayIndex: Int): Boolean {
        val isValidated = scheduleValidator.validateHours(daySchedule)
        dailyHoursScheduleViewStateList.value?.get(dayIndex)?.copy(
            day = daySchedule, isChecked = true
        )?.let { dailyHoursScheduleViewStateList.value?.set(dayIndex, it) }
        return isValidated
    }

    private fun validateSameHours(daySchedule: UiDaySchedule?, dayIndex: Int): Boolean {
        val isValidated = scheduleValidator.validateSameHours(daySchedule)
        dailyHoursScheduleViewStateList.value?.get(dayIndex)?.copy(
            day = daySchedule, isChecked = true
        )?.let { dailyHoursScheduleViewStateList.value?.set(dayIndex, it) }
        return isValidated
    }

    private fun validateBreaks(daySchedule: UiDaySchedule?, dayIndex: Int): Boolean {
        val isValidated = scheduleValidator.validateBreaks(daySchedule)
        dailyHoursScheduleViewStateList.value?.get(dayIndex)?.copy(
            day = daySchedule, isChecked = true
        )?.let { dailyHoursScheduleViewStateList.value?.set(dayIndex, it) }
        return isValidated
    }

    fun validateScheduleName(scheduleName: String) {
        val scheduleValidationResult =
            scheduleValidator.validateScheduleName(scheduleName, selectedSchedule)
        scheduleNameValidationResult.value = scheduleValidationResult
    }


    fun getSameHourScheduleViewStateList(): ArrayList<DailyHoursScheduleViewState> {
        val viewStateList = ArrayList<DailyHoursScheduleViewState>()
        val daysOfWeekList = arrayListOf(
            nextivaApplication.getString(R.string.notification_create_schedule_txt_monday_friday)
        )

        val scheduleDay = selectedSchedule?.workingHours?.firstOrNull()
        val daySchedule =
            if (selectedSchedule?.monToFri == true && scheduleDay != null) {
                val breaks: ArrayList<UiStartEndTimes> = ArrayList()

                scheduleDay.breaks?.forEach { dayBreak ->
                    dayBreak.getStartTimeDisplay().let { start ->
                        dayBreak.getEndTimeDisplay().let { end ->
                            breaks.add(UiStartEndTimes(start, end))
                        }
                    }
                }

                UiDaySchedule(
                    breaks = breaks,
                    hours = UiStartEndTimes(
                        startTime = scheduleDay.getStartTimeDisplay(),
                        endTime = scheduleDay.getEndTimeDisplay()
                    )
                )

            } else {
                UiDaySchedule(
                    breaks = ArrayList(),
                    hours = UiStartEndTimes(
                        startTime = nextivaApplication.getString(R.string.notification_create_schedule_hours_default_start_time),
                        endTime = nextivaApplication.getString(R.string.notification_create_schedule_hours_default_end_time)
                    )
                )
            }
        daysOfWeekList.forEachIndexed { dayIndex, day ->
            viewStateList.add(getScheduleViewStateItem(dayName = day.lowercase()
                .replaceFirstChar { it.uppercase() },
                dayIndex = dayIndex,
                SAME_HOURS_MONDAY_FRIDAY,
                daySchedule,
                isChecked = selectedSchedule?.workingHours?.any { it.day == day } == true))
        }
        return viewStateList
    }

    fun getCustomHoursScheduleViewStateList(): ArrayList<DailyHoursScheduleViewState> {
        val viewStateList = ArrayList<DailyHoursScheduleViewState>()
        val daysOfWeekList = arrayListOf(
            DayOfWeek.MONDAY.name,
            DayOfWeek.TUESDAY.name,
            DayOfWeek.WEDNESDAY.name,
            DayOfWeek.THURSDAY.name,
            DayOfWeek.FRIDAY.name,
            DayOfWeek.SATURDAY.name,
            DayOfWeek.SUNDAY.name
        )
        daysOfWeekList.forEachIndexed { dayIndex, day ->
            var defaultDay: UiDaySchedule? = null
            var isChecked = false

            if (selectedSchedule != null && selectedSchedule?.monToFri != true) {
                selectedSchedule?.workingHours?.find { it.day == day }?.let { scheduleDay ->
                    val breaks: ArrayList<UiStartEndTimes> = ArrayList()

                    scheduleDay.breaks?.forEach { dayBreak ->
                        dayBreak.getStartTimeDisplay().let { start ->
                            dayBreak.getEndTimeDisplay().let { end ->
                                breaks.add(UiStartEndTimes(start, end))
                            }
                        }
                    }

                    defaultDay = UiDaySchedule(
                        breaks, UiStartEndTimes(
                            scheduleDay.getStartTimeDisplay(), scheduleDay.getEndTimeDisplay()
                        )
                    )
                    isChecked = true
                }
            } else {
                if (day != DayOfWeek.SUNDAY.name && day != DayOfWeek.SATURDAY.name) {
                    isChecked = true
                    defaultDay = UiDaySchedule(
                        breaks = ArrayList(),
                        hours = UiStartEndTimes(
                            startTime = nextivaApplication.getString(R.string.notification_create_schedule_hours_default_start_time),
                            endTime = nextivaApplication.getString(R.string.notification_create_schedule_hours_default_end_time)
                        )
                    )
                }
            }

            viewStateList.add(
                getScheduleViewStateItem(
                    dayName = day.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    dayIndex = dayIndex,
                    CUSTOM_HOURS_DAY,
                    daySchedule = defaultDay,
                    isChecked = isChecked
                )
            )
        }
        return viewStateList
    }

    private fun getScheduleViewStateItem(
        dayName: String,
        dayIndex: Int,
        dailyHourSelectedOption: Int,
        daySchedule: UiDaySchedule?,
        isChecked: Boolean
    ): DailyHoursScheduleViewState {
        return DailyHoursScheduleViewState(isChecked = isChecked,
            day = daySchedule,
            dayName = dayName,
            dailyHourSelectedOption = dailyHourSelectedOption,
            timeChangedListener = { uiDaySchedule, _ ->
                validateUpdatedSchedule(uiDaySchedule, dayIndex)
            },
            checkedChangeListener = { isChecked ->
                updateCheckedListener(isChecked, dayIndex)
            },
            onBreakItemRemovedListener = { uiDaySchedule ->
                onBreakItemRemoved(uiDaySchedule, dayIndex)
                validateUpdatedSchedule(uiDaySchedule, dayIndex)
            },
            onBreakItemAddedListener = { uiDaySchedule ->
                validateUpdatedSchedule(uiDaySchedule, dayIndex)
            })
    }

    private fun onBreakItemRemoved(uiDaySchedule: UiDaySchedule?, dayIndex: Int) {
        dailyHoursScheduleViewStateList.value?.get(dayIndex)?.copy(
            day = uiDaySchedule
        )?.let {
            dailyHoursScheduleViewStateList.value?.set(dayIndex, it)
        }
        dailyHoursScheduleViewStateList.postValue(
            dailyHoursScheduleViewStateList.value
        )
    }

    fun initializeViewStateList(selectedOption: Int) {
        dailyHourSelectedOption = selectedOption
        hasValidScheduleTimes = true
        when (selectedOption) {
            SAME_HOURS_MONDAY_FRIDAY -> {
                dailyHoursScheduleViewStateList.postValue(getSameHourScheduleViewStateList())
            }
            CUSTOM_HOURS_DAY -> {
                dailyHoursScheduleViewStateList.postValue(getCustomHoursScheduleViewStateList())
            }
        }
    }

    private fun updateCheckedListener(isChecked: Boolean, dayIndex: Int) {
        if (isChecked) {
            dailyHoursScheduleViewStateList.value?.get(dayIndex)?.copy(
                isChecked = isChecked,
                day = dailyHoursScheduleViewStateList.value?.get(dayIndex)?.day ?: UiDaySchedule(
                    breaks = ArrayList(), hours = UiStartEndTimes(
                        startTime = nextivaApplication.getString(R.string.notification_create_schedule_hours_default_start_time),
                        endTime = nextivaApplication.getString(R.string.notification_create_schedule_hours_default_end_time)
                    )
                )
            )?.let {
                dailyHoursScheduleViewStateList.value?.set(dayIndex, it)
            }
        } else {

            dailyHoursScheduleViewStateList.value?.get(dayIndex)
                ?.copy(day = null, isChecked = false)?.let { dailyHourScheduleViewState ->
                    dailyHoursScheduleViewStateList.value?.set(dayIndex, dailyHourScheduleViewState)
                }
        }
        dailyHoursScheduleViewStateList.postValue(
            dailyHoursScheduleViewStateList.value
        )
    }

    private fun validateUpdatedSchedule(
        daySchedule: UiDaySchedule?, dayIndex: Int
    ) {
        // Validations
        val isHoursValid = validateSameHours(daySchedule, dayIndex)
        val isEndHoursValid = if (isHoursValid) validateEndHours(daySchedule, dayIndex) else false
        val isBreaksOverlappingValid = validateBreaksOverlapping(daySchedule, dayIndex)
        val isBreakWithHoursValid = validateBreaksWithHours(daySchedule, dayIndex)
        val isBreaksValid = validateBreaks(daySchedule, dayIndex)

        updateUIState()

        hasValidScheduleTimes =
            isEndHoursValid && isHoursValid && isBreaksValid && isBreakWithHoursValid && isBreaksOverlappingValid

        dailyHoursScheduleViewStateList.postValue(
            dailyHoursScheduleViewStateList.value
        )
    }

    private fun updateUIState() {
        dailyHoursScheduleViewStateList.value?.forEach { dailyHoursScheduleViewState ->
            dailyHoursScheduleViewState.day?.breaks?.forEach { uiStartEndTimes ->
                if (uiStartEndTimes.validationErrorTypeList.isEmpty()) {
                    uiStartEndTimes.isEndTimeError = false
                    uiStartEndTimes.isStartTimeError = false
                    uiStartEndTimes.validationErrorText = ""
                }
                uiStartEndTimes.validationErrorTypeList.forEach { errorType ->
                    when (errorType) {
                        Enums.ScheduleValidationErrorType.END_TIME_BEFORE_START_TIME -> {
                            uiStartEndTimes.isEndTimeError = true
                            uiStartEndTimes.isStartTimeError = false
                            uiStartEndTimes.validationErrorText =
                                nextivaApplication.getString(R.string.notification_create_schedule_validation_error_end_time_before_start)
                        }
                        Enums.ScheduleValidationErrorType.BREAK_TIMES_END_TIME_NOT_IN_RANGE -> {
                            uiStartEndTimes.isEndTimeError = true
                            uiStartEndTimes.isStartTimeError = false
                            uiStartEndTimes.validationErrorText =
                                nextivaApplication.getString(R.string.notification_create_schedule_validation_error_break_times_range)
                        }
                        Enums.ScheduleValidationErrorType.BREAK_TIMES_START_TIME_NOT_IN_RANGE -> {
                            uiStartEndTimes.isStartTimeError = true
                            uiStartEndTimes.isEndTimeError = false
                            uiStartEndTimes.validationErrorText =
                                nextivaApplication.getString(R.string.notification_create_schedule_validation_error_break_times_range)

                        }
                        Enums.ScheduleValidationErrorType.BREAK_TIMES_OVERLAPPING -> {
                            uiStartEndTimes.isStartTimeError = true
                            uiStartEndTimes.isEndTimeError = true
                            uiStartEndTimes.validationErrorText =
                                nextivaApplication.getString(R.string.notification_create_schedule_validation_error_break_times_overlapping)

                        }

                        Enums.ScheduleValidationErrorType.BREAK_TIMES_START_END_TIME_NOT_IN_RANGE -> {
                            uiStartEndTimes.isStartTimeError = true
                            uiStartEndTimes.isEndTimeError = true
                            uiStartEndTimes.validationErrorText =
                                nextivaApplication.getString(R.string.notification_create_schedule_validation_error_break_times_range)
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }


    fun makeScheduleRequest(scheduleName: String) {
        val dayHashMap = getDayHashMap(dailyHoursScheduleViewStateList.value)
        if (dayHashMap.isNotEmpty()) {

            val userScheduleRequest = transformToApiRequest(dayHashMap, scheduleName)

            selectedSchedule?.scheduleId?.let { scheduleId ->
                schedulesRepository.updateUserSchedule(userScheduleRequest, scheduleId)
                    .subscribe(object : DisposableSingleObserver<UserScheduleResponse?>() {
                        override fun onSuccess(userScheduleResponse: UserScheduleResponse) {
                            onSaveSuccess.postValue(userScheduleResponse.id)
                        }

                        override fun onError(e: Throwable) {
                            onSaveSuccess.postValue(null)
                        }
                    })

            } ?: run {
                schedulesRepository.saveUserSchedule(userScheduleRequest)
                    .flatMap { dbManager.insertSchedule(it) }
                    .subscribe(object : DisposableSingleObserver<String>() {
                        override fun onSuccess(scheduleId: String) {
                            onSaveSuccess.postValue(scheduleId)
                        }

                        override fun onError(e: Throwable) {
                            onSaveSuccess.postValue(null)
                        }
                    })
            }
        }
    }

    private fun getDayHashMap(dailyHoursScheduleViewStates: ArrayList<DailyHoursScheduleViewState>?): HashMap<String, Day> {
        val dayHashMap = HashMap<String, Day>()
        dailyHoursScheduleViewStates?.forEach { dailyHoursScheduleViewState ->
            val day = dailyHoursScheduleViewState.day
            val startTime = day?.hours?.startTime?.let { Utils.formatScheduleTimeTo24hr(it) }
            val endTime = day?.hours?.endTime?.let { Utils.formatScheduleTimeTo24hr(it) }
            if (!startTime.isNullOrEmpty() && !endTime.isNullOrEmpty()) {
                dailyHoursScheduleViewState.dayName?.let { dayName ->
                    dayHashMap[dayName] = Day(
                        hours = HourOfWeek(startTime, endTime),
                        breaks = transformUiBreaks(day.breaks)
                    )
                }
            }
        }
        return dayHashMap
    }

    private fun transformToApiRequest(
        dayHashMap: HashMap<String, Day>, scheduleName: String
    ): UserScheduleRequest {

        val days: Days
        if (dailyHourSelectedOption == SAME_HOURS_MONDAY_FRIDAY) {
            val mondayToFriday =
                nextivaApplication.getString(R.string.notification_create_schedule_txt_monday_friday)
            days = Days(
                SUNDAY = null,
                MONDAY = dayHashMap[mondayToFriday],
                TUESDAY = dayHashMap[mondayToFriday],
                WEDNESDAY = dayHashMap[mondayToFriday],
                THURSDAY = dayHashMap[mondayToFriday],
                FRIDAY = dayHashMap[mondayToFriday],
                SATURDAY = null
            )
        } else {
            days = Days(
                SUNDAY = dayHashMap["Sunday"],
                MONDAY = dayHashMap["Monday"],
                TUESDAY = dayHashMap["Tuesday"],
                WEDNESDAY = dayHashMap["Wednesday"],
                THURSDAY = dayHashMap["Thursday"],
                FRIDAY = dayHashMap["Friday"],
                SATURDAY = dayHashMap["Saturday"]
            )
        }

        return UserScheduleRequest(
            days = days,
            holidays = getHolidaysList(),
            name = scheduleName,
            ownerType = "User",
            scheduleLevel = ScheduleLevel.User,
            scheduleType = if (dailyHourSelectedOption == SAME_HOURS_MONDAY_FRIDAY) {
                ScheduleType.WEEKLY
            } else {
                ScheduleType.DAILY
            },
            status = true,
            ownerId = sessionManager.userInfo?.comNextivaUseruuid
        )
    }

    private fun getHolidaysList(): ArrayList<Holiday> {
        val holidays = holidays.value.map { it.holiday }.toCollection(ArrayList())
        holidays.addAll(observedHolidaySelectionList.value.filter { it.isSelected }
            .map {
                it.holiday.copy(
                    occurrence = it.holiday.occurrence?.uppercase(Locale.ROOT),
                    holidayType = HolidayType.OBSERVED,
                    interval = Interval(true, null)
                )
            }.toCollection(ArrayList()))

        return holidays
    }

    private fun transformUiBreaks(uiBreaks: ArrayList<UiStartEndTimes>?): ArrayList<Break> {
        val breaks = ArrayList<Break>()
        uiBreaks?.filter { uiBreaksItem -> uiBreaksItem.startTime.isNotEmpty() && uiBreaksItem.endTime.isNotEmpty() }
            ?.map { uiStartEndTimes ->
                Utils.formatScheduleTimeTo24hr(uiStartEndTimes.startTime)?.let { startTime ->
                    Utils.formatScheduleTimeTo24hr(
                        uiStartEndTimes.endTime
                    )?.let { endTime -> Break(name = "", start = startTime, end = endTime) }
                }?.let { breaks.add(it) }
            }
        return breaks
    }

    fun addHoliday(holiday: Holiday) {
        val holidaysList = holidays.value.toCollection(ArrayList())
        holidaysList.add(HolidaySelection(holiday, true))
        holidays.value = holidaysList
    }

    fun removeHoliday(holiday: Holiday) {
        val holidaysList = holidays.value.filter { it.holiday.name != holiday.name }
        holidays.value = ArrayList(holidaysList)
    }

    fun deleteSchedule() {
        selectedSchedule?.scheduleId?.let { scheduleId ->
            scheduleRepository.deleteSchedule(scheduleId)
                .subscribe(object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(success: Boolean) {
                        presenceRepository.deletePresenceDndSchedule()
                        scheduleDeletedLiveData.postValue(success)
                    }

                    override fun onError(e: Throwable) {
                        scheduleDeletedLiveData.postValue(false)
                    }
                })
        }
    }

    fun getObservedHolidayList() {
        scheduleRepository.getObservedHolidayList()
            .subscribe(object : DisposableSingleObserver<ArrayList<Holiday>>() {
                override fun onSuccess(holidayList: ArrayList<Holiday>) {
                    val gson = Gson()
                    val holidayJson = gson.toJson(holidayList)
                    sharedPreferencesManager.setString(
                        SharedPreferencesManager.NOTIFICATION_SCHEDULE_OBSERVED_HOLIDAY_LIST,
                        holidayJson
                    )
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun updateHolidaySelectionList(holidayListJson: List<HolidaySelection>) {
        observedHolidaySelectionList.value = holidayListJson.toMutableStateList()
    }

    fun addHolidaySelectionItem(holidayItem: Holiday) {
        val holidaysList = observedHolidaySelectionList.value.toCollection(ArrayList())
        holidaysList.add(HolidaySelection(holidayItem, true))
        observedHolidaySelectionList.value = holidaysList
    }

    fun deleteHoliday(holidayName: String) {
        holidays.value = holidays.value.filterNot { it.holiday.toString() == holidayName }
        observedHolidaySelectionList.value =
            observedHolidaySelectionList.value.filterNot { it.holiday.toString() == holidayName }
    }

    fun createCustomHolidayEntry(): Holiday {
        val startTime =
            customStartTime.value?.let {
                Utils.formatScheduleTimeTo24hr(
                    it.format(
                        DateTimeFormatter.ofPattern(
                            nextivaApplication.getString(R.string.date_format_short_time_12_hour),
                            Locale.getDefault()
                        )
                    )
                )
            }
        val endTime =
            customEndTime.value?.let {
                Utils.formatScheduleTimeTo24hr(
                    it.format(
                        DateTimeFormatter.ofPattern(
                            nextivaApplication.getString(R.string.date_format_short_time_12_hour),
                            Locale.getDefault()
                        )
                    )
                )
            }

        val name = getCustomHolidayDisplayDate()
        val occurrencesInt = 1
        try
            {
                Integer.parseInt(occurrences.value.toString())
            }
        catch (exception: NumberFormatException){
            LogUtil.e(exception.toString())
        }

        return Holiday(
            null,
            customDay.value?.uppercase(Locale.ROOT),
            HolidayType.CUSTOM,
            customMonth.value?.uppercase(Locale.ROOT),
            name,
            customOccurrence.value?.uppercase(Locale.ROOT),
            Interval(
                isAllDay.value,
                if (isAllDay.value == true) null else HourOfWeek(
                    startTime ?: "",
                    endTime ?: ""
                )
            ),
            Repeat(isEventRepeatsForever.value, if(isEventRepeatsForever.value == true) null else occurrencesInt),
            SimpleDateFormat("yyyy-MM-dd").format(Date())
        )
    }

    private fun getCustomHolidayDisplayDate(): String {
        val occurences = getShortOccurencesString()
        val dayOfWeek = getShortDayOfTheWeekString()
        val monthYearTime = getMonthYearTimeString()

        return nextivaApplication.getString(
            R.string.work_schedule_custom_dates_occurrence_day_of_month,
            occurences,
            dayOfWeek,
            monthYearTime
        )
    }

    private fun getShortOccurencesString(): String {
        return when (customOccurrence.value.toString()) {
            nextivaApplication.getString(R.string.work_schedule_custom_dates_first) -> nextivaApplication.getString(
                R.string.work_schedule_custom_dates_1st
            )
            nextivaApplication.getString(R.string.work_schedule_custom_dates_second) -> nextivaApplication.getString(
                R.string.work_schedule_custom_dates_2nd
            )
            nextivaApplication.getString(R.string.work_schedule_custom_dates_third) -> nextivaApplication.getString(
                R.string.work_schedule_custom_dates_3rd
            )
            nextivaApplication.getString(R.string.work_schedule_custom_dates_fourth) -> nextivaApplication.getString(
                R.string.work_schedule_custom_dates_4th
            )
            nextivaApplication.getString(R.string.work_schedule_custom_dates_last) -> nextivaApplication.getString(
                R.string.work_schedule_custom_dates_last
            )
            else -> ""
        }
    }

    private fun getShortDayOfTheWeekString(): String {
        return when (customDay.value.toString()) {
            nextivaApplication.getString(R.string.general_SUNDAY), nextivaApplication.getString(R.string.general_Sunday) -> nextivaApplication.getString(
                R.string.general_Sun
            )
            nextivaApplication.getString(R.string.general_MONDAY), nextivaApplication.getString(R.string.general_Monday) -> nextivaApplication.getString(
                R.string.general_Mon
            )
            nextivaApplication.getString(R.string.general_TUESDAY), nextivaApplication.getString(R.string.general_Tuesday) -> nextivaApplication.getString(
                R.string.general_Tue
            )
            nextivaApplication.getString(R.string.general_WEDNESDAY), nextivaApplication.getString(R.string.general_Wednesday) -> nextivaApplication.getString(
                R.string.general_Wed
            )
            nextivaApplication.getString(R.string.general_THURSDAY), nextivaApplication.getString(R.string.general_Thursday) -> nextivaApplication.getString(
                R.string.general_Thu
            )
            nextivaApplication.getString(R.string.general_FRIDAY), nextivaApplication.getString(R.string.general_Friday) -> nextivaApplication.getString(
                R.string.general_Fri
            )
            nextivaApplication.getString(R.string.general_SATURDAY), nextivaApplication.getString(R.string.general_Saturday) -> nextivaApplication.getString(
                R.string.general_Sat
            )
            else -> ""
        }
    }

    private fun getMonthYearTimeString(): String {
        var monthYearTime = getShortMonthString()
        if (isEventRepeatsForever.value == false) {
            var occurrenceCount =
                if (occurrences.value?.toInt() != null) occurrences.value!!.toInt() else 0
            if (occurrenceCount > 1) {
                val localDateTime = LocalDateTime.now()
                val selectedMonth = customMonth.value
                if (selectedMonth != null) {
                    val months =
                        nextivaApplication.resources.getStringArray(R.array.general_months).toList()
                    val monthPosition = months.indexOf(selectedMonth) + 1;
                    val startYear =
                        if (localDateTime.month.value >= monthPosition) localDateTime.year + 1 else localDateTime.year

                    val endYear =
                        if (localDateTime.month.value >= monthPosition) localDateTime.year + occurrenceCount else localDateTime.year + occurrenceCount - 1

                    monthYearTime += " " + nextivaApplication.getString(
                        R.string.work_schedule_custom_dates_occurrence_years,
                        startYear.toString(),
                        endYear.toString().substring(endYear.toString().length - 2)
                    )
                }
            }
        }

        if (isAllDay.value == false) {
            val timeString = nextivaApplication.getString(
                R.string.work_schedule_custom_dates_occurrence_time_hours,
                getTimeString(customStartTime),
                getTimeString(customEndTime)
            )

            monthYearTime += " $timeString"
        }

        return monthYearTime

    }

    private fun getTimeString(time: MutableLiveData<LocalDateTime>): String {
        val formatter =
            DateTimeFormatter.ofPattern(nextivaApplication.getString(R.string.date_format_short_time_12_hour))

        var timeString = time.value?.format(formatter) ?: ""
        timeString = timeString.lowercase(Locale.getDefault())
            .replace(
                " " + nextivaApplication.getString(R.string.work_schedule_custom_dates_occurrence_am),
                nextivaApplication.getString(R.string.work_schedule_custom_dates_occurrence_short_am)
            )
            .replace(
                " " + nextivaApplication.getString(R.string.work_schedule_custom_dates_occurrence_pm),
                nextivaApplication.getString(R.string.work_schedule_custom_dates_occurrence_short_pm)
            )
        return timeString
    }

    private fun getShortMonthString(): String {
        return when (customMonth.value.toString()) {
            nextivaApplication.getString(R.string.general_january) -> nextivaApplication.getString(R.string.general_jan_short)
            nextivaApplication.getString(R.string.general_february) -> nextivaApplication.getString(R.string.general_feb_short)
            nextivaApplication.getString(R.string.general_march) -> nextivaApplication.getString(R.string.general_mar_short)
            nextivaApplication.getString(R.string.general_april) -> nextivaApplication.getString(R.string.general_apr_short)
            nextivaApplication.getString(R.string.general_may) -> nextivaApplication.getString(R.string.general_may_short)
            nextivaApplication.getString(R.string.general_june) -> nextivaApplication.getString(R.string.general_jun_short)
            nextivaApplication.getString(R.string.general_july) -> nextivaApplication.getString(R.string.general_july_short)
            nextivaApplication.getString(R.string.general_august) -> nextivaApplication.getString(R.string.general_aug_short)
            nextivaApplication.getString(R.string.general_september) -> nextivaApplication.getString(R.string.general_sept_short)
            nextivaApplication.getString(R.string.general_october) -> nextivaApplication.getString(R.string.general_oct_short)
            nextivaApplication.getString(R.string.general_november) -> nextivaApplication.getString(R.string.general_nov_short)
            nextivaApplication.getString(R.string.general_december) -> nextivaApplication.getString(R.string.general_dec_short)
            else -> ""
        }

    }
}