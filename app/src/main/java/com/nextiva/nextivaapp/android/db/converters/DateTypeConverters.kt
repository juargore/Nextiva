package com.nextiva.nextivaapp.android.db.converters

import androidx.room.TypeConverter
import com.nextiva.nextivaapp.android.managers.FormatterManager
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class DateTypeConverters {
    private val formatManager = FormatterManager.getInstance()


    @TypeConverter
    fun fromDateTime(dateTime: String): LocalDateTime {

        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun dateTimeToOffsetDateTime(instant: LocalDateTime?): String? {
        return if (instant == null) null else DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(instant)
    }
}