package com.nextiva.nextivaapp.android.db.converters

import androidx.room.TypeConverter
import com.nextiva.nextivaapp.android.managers.FormatterManager
import org.threeten.bp.Instant

class RoomConverters {

    private val formatManager = FormatterManager.getInstance()

    @TypeConverter
    fun instantFromDateTime(dateTime: String?): Instant? {
        return if (dateTime == null) null else Instant.from(
            formatManager.dateFormatter_8601ExtendedDatetimeTimeZone.parse(
                dateTime
            )
        )
    }

    @TypeConverter
    fun dateTimeFromInstant(instant: Instant?): String? {
        return if (instant == null) null else formatManager.dateFormatter_8601ExtendedDatetimeTimeZone.format(instant)
    }

}
