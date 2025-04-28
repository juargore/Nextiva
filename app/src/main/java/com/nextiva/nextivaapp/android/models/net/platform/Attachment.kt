package com.nextiva.nextivaapp.android.models.net.platform

import android.text.TextUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.db.model.DbAttachment
import com.nextiva.nextivaapp.android.managers.FormatterManager
import org.threeten.bp.Instant

data class Attachment(
        var link: String? = null,
        var contentType: String? = null,
        var filename: String?,
        var contentData: ByteArray?,
        val thumbnailLink: String?,
        val uploadedDate: String?,
        val audioDuration: Long?

) {
    fun getDbAttachment(smsId: Long): DbAttachment? {
        try {
            val formatManager = FormatterManager.getInstance()
            if (TextUtils.isEmpty(uploadedDate)) {
                return DbAttachment(null, link, contentType, smsId, contentData, null, thumbnailLink, audioDuration, filename)
            } else {
                return DbAttachment(null, link, contentType, smsId, contentData, Instant.from(formatManager.getSentFormatterDateTimeManager(uploadedDate)), thumbnailLink, audioDuration, filename)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
             return  null
        }
    }
}

