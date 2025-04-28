package com.nextiva.nextivaapp.android.models.net.platform

import android.text.TextUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbMessageState

data class MessageState(
        var deleted: Boolean?,
        var id: String?,
        var messageId: String?,
        var priority: String?,
        var readStatus: String?
)
 {
    fun getDbMessageState(smsId: Long): DbMessageState? {
        try {
            return DbMessageState(smsId, messageId, priority, readStatus, deleted)

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            return null
        }
    }

     fun isRead(): Boolean {
         return TextUtils.equals(readStatus, Enums.SMSMessages.ReadStatus.READ)
     }
}