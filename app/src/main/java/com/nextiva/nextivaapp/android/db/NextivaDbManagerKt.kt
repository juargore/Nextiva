package com.nextiva.nextivaapp.android.db

import android.app.Application
import com.nextiva.nextivaapp.android.db.AppDatabase.Companion.getAppDatabase
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager.SettingsKey
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.net.platform.Data
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import javax.inject.Inject

class NextivaDbManagerKt @Inject constructor(var application: Application,
                                             var schedulerProvider: SchedulerProvider,
                                             var sharedPreferencesManager: SharedPreferencesManager,
                                             var calendarManager: CalendarManager,
                                             var logManager: LogManager): DbManagerKt {

    private var appDatabase = getAppDatabase(application)
    private var presenceDao = appDatabase.presenceDao()
    private var contactDao = appDatabase.contactDao()
    private var contactRecentDao = appDatabase.contactRecentDao()
    private var messagesDao = appDatabase.messagesDao()
    private var callLogsDao = appDatabase.callLogEntriesDao()
    private var completeContactDao = appDatabase.completeContactDaoKt()
    private var sessionDao = appDatabase.sessionDao()
    private var voicemailDao = appDatabase.voicemailDao()
    private var smsMessagesDao = appDatabase.smsMessagesDao()
    private var messageStateDao = appDatabase.messageStateDao()
    private var attachmentsDao = appDatabase.attachmentsDao()
    private var loggingDao = appDatabase.loggingDao()
    private var meetingDao = appDatabase.meetingDao()
    private var schedulesDao = appDatabase.schedulesDao()
    private var participantsDao = appDatabase.participantDao()

    override suspend fun getContactFromPhoneNumberInThread(phoneNumber: String): DbResponse<NextivaContact> {
        return completeContactDao.getConnectContactInThread(phoneNumber)
    }

    override suspend fun getConnectContactFromUuidInThread(uuid: String): NextivaContact? {
        return completeContactDao.getConnectContactFromUuid(uuid)
    }

    override suspend fun saveSmsMessages(data: List<Data>?, phoneNumber: String, sentStatus: Int, userUuid: String?, allSavedTeams: List<SmsTeam>) {
        smsMessagesDao.insertSmsMessages(appDatabase,
            data?.let { ArrayList(it) } ?: arrayListOf(),
            phoneNumber,
            userUuid,
            sentStatus,
            allSavedTeams,
            null
        )
    }

    override suspend fun getSmsMessageByMessageId(messageId: String): SmsMessage? {
        return smsMessagesDao.getSmsMessageByMessageId(messageId)
    }

    override fun expireVoiceConversationMessagesCache() {
        sharedPreferencesManager.setLong(getLastCacheTimestampKey(SharedPreferencesManager.VOICE_CONVERSATION_MESSAGES), 0)
    }

    private fun getLastCacheTimestampKey(@SettingsKey key: String): String {
        return key + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX
    }
}