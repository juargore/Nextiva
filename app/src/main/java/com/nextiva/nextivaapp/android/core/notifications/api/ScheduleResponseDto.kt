package com.nextiva.nextivaapp.android.core.notifications.api

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.db.model.DbHoliday
import com.nextiva.nextivaapp.android.db.model.DbSchedule
import com.nextiva.nextivaapp.android.db.model.DbWorkingHourBreak
import com.nextiva.nextivaapp.android.db.model.DbWorkingHours
import java.io.Serializable

data class ScheduleResponseDto(
    @SerializedName("hasNext") var hasNext: Boolean,
    @SerializedName("pageSize") var pageSize: Int,
    @SerializedName("schedules") var schedules: ArrayList<UserScheduleResponse>,
)

data class UserScheduleResponse(
    @SerializedName("days") var days: Days,
    @SerializedName("holidays") var holidays: ArrayList<HolidayV2>?,
    @SerializedName("hours") var hours: HourOfWeek?,
    @SerializedName("id") var id: String?,
    @SerializedName("location") var location: Location?,
    @SerializedName("name") var name: String?,
    @SerializedName("ownerId") var ownerId: String?,
    @SerializedName("ownerType") var ownerType: String?,
    @SerializedName("scheduleLevel") var scheduleLevel: ScheduleLevel?,
    @SerializedName("scheduleType") var scheduleType: ScheduleType?
) {

    constructor(): this(Days(), null, null, null, null, null, null, null, null, null)

    var isDndSchedule: Boolean = false

    fun toDbSchedule(isDbDndSchedule: Boolean, pageNumber: Int?): DbSchedule {
        return DbSchedule(null,
            id,
            is24x7 = scheduleType == ScheduleType.ALLDAY,
            monToFri = scheduleType == ScheduleType.WEEKLY,
            name,
            null,
            scheduleLevel?.name,
            isDndSchedule || isDbDndSchedule,
            pageNumber)
    }

    fun getDaysList(): ArrayList<DbWorkingHours> {
        val daysList: ArrayList<DbWorkingHours> = ArrayList()

        id?.let { scheduleId ->
            days.SUNDAY?.let { daysList.add(it.toDbWorkingHour(scheduleId, DayOfWeek.SUNDAY.name)) }
            days.MONDAY?.let { daysList.add(it.toDbWorkingHour(scheduleId, DayOfWeek.MONDAY.name)) }
            days.TUESDAY?.let { daysList.add(it.toDbWorkingHour(scheduleId, DayOfWeek.TUESDAY.name)) }
            days.WEDNESDAY?.let { daysList.add(it.toDbWorkingHour(scheduleId, DayOfWeek.WEDNESDAY.name)) }
            days.THURSDAY?.let { daysList.add(it.toDbWorkingHour(scheduleId, DayOfWeek.THURSDAY.name)) }
            days.FRIDAY?.let { daysList.add(it.toDbWorkingHour(scheduleId, DayOfWeek.FRIDAY.name)) }
            days.SATURDAY?.let { daysList.add(it.toDbWorkingHour(scheduleId, DayOfWeek.SATURDAY.name)) }
        }

        return daysList
    }

    fun getDayOfWeekOfString(context: Context, dayOfWeek: String): DayOfWeek
    {
        return when (dayOfWeek) {
            context.getString(R.string.general_SUNDAY) -> DayOfWeek.SUNDAY
            context.getString(R.string.general_MONDAY) -> DayOfWeek.MONDAY
            context.getString(R.string.general_TUESDAY) -> DayOfWeek.TUESDAY
            context.getString(R.string.general_WEDNESDAY) -> DayOfWeek.WEDNESDAY
            context.getString(R.string.general_THURSDAY) -> DayOfWeek.THURSDAY
            context.getString(R.string.general_FRIDAY) -> DayOfWeek.FRIDAY
            context.getString(R.string.general_SATURDAY) -> DayOfWeek.SATURDAY
            context.getString(R.string.general_Sunday) -> DayOfWeek.SUNDAY
            context.getString(R.string.general_Monday) -> DayOfWeek.MONDAY
            context.getString(R.string.general_Tuesday) -> DayOfWeek.TUESDAY
            context.getString(R.string.general_Wednesday) -> DayOfWeek.WEDNESDAY
            context.getString(R.string.general_Thursday) -> DayOfWeek.THURSDAY
            context.getString(R.string.general_Friday) -> DayOfWeek.FRIDAY
            context.getString(R.string.general_Saturday) -> DayOfWeek.SATURDAY
            else -> DayOfWeek.UNKNOWN
        }

    }

    fun getStringOfDayOfWeek(context: Context, dayOfWeek: DayOfWeek): String
    {
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> context.getString(R.string.general_SUNDAY)
            DayOfWeek.MONDAY -> context.getString(R.string.general_MONDAY)
            DayOfWeek.TUESDAY -> context.getString(R.string.general_TUESDAY)
            DayOfWeek.WEDNESDAY -> context.getString(R.string.general_WEDNESDAY)
            DayOfWeek.THURSDAY -> context.getString(R.string.general_THURSDAY)
            DayOfWeek.FRIDAY -> context.getString(R.string.general_FRIDAY)
            DayOfWeek.SATURDAY -> context.getString(R.string.general_SATURDAY)
            else -> ""
        }
    }


    fun getStringOfMonth(context: Context, month: Month): String
    {
        return when (month) {
            Month.JANUARY -> context.getString(R.string.general_january)
            Month.FEBRUARY -> context.getString(R.string.general_february)
            Month.MARCH -> context.getString(R.string.general_march)
            Month.APRIL -> context.getString(R.string.general_april)
            Month.MAY -> context.getString(R.string.general_may)
            Month.JUNE -> context.getString(R.string.general_june)
            Month.JULY -> context.getString(R.string.general_july)
            Month.AUGUST -> context.getString(R.string.general_august)
            Month.SEPTEMBER -> context.getString(R.string.general_september)
            Month.OCTOBER -> context.getString(R.string.general_october)
            Month.NOVEMBER -> context.getString(R.string.general_november)
            Month.DECEMBER -> context.getString(R.string.general_december)
            else -> ""
        }
    }
    fun getMonthOfString(context: Context, month: String): Month
    {
        return when (month) {
            context.getString(R.string.general_january) -> Month.JANUARY
            context.getString(R.string.general_february) -> Month.FEBRUARY
            context.getString(R.string.general_march) -> Month.MARCH
            context.getString(R.string.general_april) -> Month.APRIL
            context.getString(R.string.general_may) -> Month.MAY
            context.getString(R.string.general_june) -> Month.JUNE
            context.getString(R.string.general_july) -> Month.JULY
            context.getString(R.string.general_august) -> Month.AUGUST
            context.getString(R.string.general_september) -> Month.SEPTEMBER
            context.getString(R.string.general_october) -> Month.OCTOBER
            context.getString(R.string.general_november) -> Month.NOVEMBER
            context.getString(R.string.general_december) -> Month.DECEMBER
            else -> Month.UNKNOWN
        }
    }



    fun getDbHolidays(): ArrayList<DbHoliday> {
        val holidaysList: ArrayList<DbHoliday> = ArrayList()

        id?.let { scheduleId ->
            holidays?.forEach { holiday ->
                holidaysList.add(holiday.toDbHoliday(scheduleId))
            }
        }

        return holidaysList
    }
}

data class Days(
    @SerializedName("SUNDAY") var SUNDAY: Day?,
    @SerializedName("MONDAY") var MONDAY: Day?,
    @SerializedName("TUESDAY") var TUESDAY: Day?,
    @SerializedName("WEDNESDAY") var WEDNESDAY: Day?,
    @SerializedName("THURSDAY") var THURSDAY: Day?,
    @SerializedName("FRIDAY") var FRIDAY: Day?,
    @SerializedName("SATURDAY") var SATURDAY: Day?,
) {
    constructor(): this(null, null, null, null, null, null, null)
}

data class Day(
    @SerializedName("breaks") var breaks: ArrayList<Break>?,
    @SerializedName("hours") var hours: HourOfWeek,
) {
    fun toDbWorkingHour(scheduleId: String, dayName: String): DbWorkingHours {
        return DbWorkingHours(scheduleId,
            dayName,
            hours.end,
            hours.start,
            breaks?.map { it.toDbWorkingHourBreak("${dayName}${scheduleId}") })
    }
}

data class Break(
    @SerializedName("name") var name: String?,
    @SerializedName("start") var start: String,
    @SerializedName("end") var end: String,
) {
    fun toDbWorkingHourBreak(dayId: String): DbWorkingHourBreak {
        return DbWorkingHourBreak(null,
            dayId,
            name,
            start,
            end)
    }
}

data class HourOfWeek(
    @SerializedName("start") var start: String,
    @SerializedName("end") var end: String,
)

data class Interval(
    @SerializedName("allDayEvent") var allDayEvent: Boolean?,
    @SerializedName("hours") var hours: HourOfWeek?,
)

data class ObservedDay(@SerializedName("names") var names: ArrayList<String>?)

data class Repeat(
    @SerializedName("forever") var forever: Boolean?,
    @SerializedName("occurrences", alternate = ["occurences"]) var occurences: Int?,
)

data class DateCriteria(
    @SerializedName("firstOccurance", alternate = ["firstOccurrence"]) var firstOccurrence: String?,
    @SerializedName("interval") var interval: Interval?,
    @SerializedName("repeat") var repeat: Repeat?,
)

data class Location(
    @SerializedName("locationId") var locationId: String?,
    @SerializedName("name") var name: String?,
)

data class HolidayV2(
    @SerializedName("dates") var dates: ArrayList<DateCriteria>?,
    @SerializedName("day") var day: Int?,
    @SerializedName("dayOfWeek") var dayOfWeek: DayOfWeek?,
    @SerializedName("endDate") var endDate: String?,
    @SerializedName("holidayType") var holidayType: HolidayType?,
    @SerializedName("interval") var interval: Interval?,
    @SerializedName("month") var month: Month?,
    @SerializedName("name") var name: String?,
    @SerializedName("observedDay") var observedDay: ObservedDay?,
    @SerializedName("occurrence") var occurrence: Occurrence?,
    @SerializedName("repeats") var repeats: Repeat?,
    @SerializedName("startDate") var startDate: String?,
) {
    fun toDbHoliday(scheduleId: String): DbHoliday {
        return DbHoliday(null,
            scheduleId,
            day,
            dayOfWeek?.name,
            startDate,
            endDate,
            interval?.hours?.start,
            interval?.hours?.end,
            holidayType?.name,
            month?.name,
            name,
            occurrence?.name,
            null,
            repeats?.occurences?.toString())
    }
}

enum class DayOfWeek { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, UNKNOWN }

enum class Month { JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER, UNKNOWN }
enum class Occurrence { FIRST, SECOND, THIRD, FOURTH, LAST, UNKNOWN }
enum class ScheduleLevel { Account, Location, User }
enum class ScheduleType { ALLDAY, DAILY, WEEKLY }

data class Holiday(
    @SerializedName("day") var day: Int?,
    @SerializedName("dayOfWeek") var dayOfWeek: String?,
    @SerializedName("holidayType") var holidayType: HolidayType?,
    @SerializedName("month") var month: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("occurrence") var occurrence: String?,
    @SerializedName("interval") var interval: Interval?,
    @SerializedName("repeats") var repeats: Repeat?,
    @SerializedName("startDate") var startDate: String?,
): Serializable {
    constructor():this(null, null, null, null, null, null, null,  null, null)
}

enum class HolidayType { CUSTOM, OBSERVED, SPECIFIC }

data class TimeRange(
    @SerializedName("start") var start: String?,
    @SerializedName("end") var end: String?,
)

data class RecurrenceRange(
    @SerializedName("endDate") var endDate: String?,
    @SerializedName("numOfRecurrences") var numOfRecurrences: Int?,
)

data class WorkingHours(
    @SerializedName("MONDAY") var MONDAY: TimeRange?,
    @SerializedName("TUESDAY") var TUESDAY: TimeRange?,
    @SerializedName("WEDNESDAY") var WEDNESDAY: TimeRange?,
    @SerializedName("THURSDAY") var THURSDAY: TimeRange?,
    @SerializedName("FRIDAY") var FRIDAY: TimeRange?,
    @SerializedName("SATURDAY") var SATURDAY: TimeRange?,
    @SerializedName("SUNDAY") var SUNDAY: TimeRange?,
)


/*
[
  {
    "holidays": [
      {
        "allDayEvent": true,
        "day": 0,
        "dayOfWeek": "string",
        "endDate": "2023-03-06",
        "holidayHours": {
          "end": "string",
          "start": "string"
        },
        "holidayType": "CUSTOM",
        "month": "string",
        "name": "string",
        "occurrence": "string",
        "recurrenceRange": {
          "endDate": "2023-03-06",
          "numOfRecurrences": 0
        },
        "startDate": "2023-03-06"
      }
    ],
    "is24x7": true,
    "monToFri": true,
    "name": "string",
    "oldScheduleName": "string",
    "scheduleLevel": "Account",
    "workingHours": {
      "FRIDAY": [
        {
          "end": "string",
          "start": "string"
        }
      ],
      "MONDAY": [
        {
          "end": "string",
          "start": "string"
        }
      ],
      "SATURDAY": [
        {
          "end": "string",
          "start": "string"
        }
      ],
      "SUNDAY": [
        {
          "end": "string",
          "start": "string"
        }
      ],
      "THURSDAY": [
        {
          "end": "string",
          "start": "string"
        }
      ],
      "TUESDAY": [
        {
          "end": "string",
          "start": "string"
        }
      ],
      "WEDNESDAY": [
        {
          "end": "string",
          "start": "string"
        }
      ]
    }
  }
]
 */