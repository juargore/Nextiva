package com.nextiva.nextivaapp.android.managers.interfaces

import com.nextiva.nextivaapp.android.core.common.api.ContactManagementPolicyApi
import com.nextiva.nextivaapp.android.core.notifications.api.SchedulesApi
import com.nextiva.nextivaapp.android.features.rooms.api.RoomsApi
import com.nextiva.nextivaapp.android.net.AttachmentApi
import com.nextiva.nextivaapp.android.net.BroadsoftUserApi
import com.nextiva.nextivaapp.android.net.CalendarApi
import com.nextiva.nextivaapp.android.net.ContactsApi
import com.nextiva.nextivaapp.android.net.ConversationApi
import com.nextiva.nextivaapp.android.net.MediaCallApi
import com.nextiva.nextivaapp.android.net.PlatformAccessApi
import com.nextiva.nextivaapp.android.net.PlatformApi
import com.nextiva.nextivaapp.android.net.PlatformNotificationOrchestrationServiceApi
import com.nextiva.nextivaapp.android.net.PresenceApi
import com.nextiva.nextivaapp.android.net.SipApi
import com.nextiva.nextivaapp.android.net.SmsMessagesApi
import com.nextiva.nextivaapp.android.net.UsersApi
import com.nextiva.nextivaapp.android.net.VoicemailApi

interface NetManager {
    fun getPlatformAccessApi(): PlatformAccessApi?

    fun getPlatformApi(): PlatformApi?

    fun getBroadsoftUserApi(): BroadsoftUserApi?

    fun getVoicemailApi(): VoicemailApi

    fun getContactsApi(): ContactsApi?

    fun getRoomsApi(): RoomsApi?

    fun getUsersApi(): UsersApi?

    fun getPresenceApi(): PresenceApi

    fun getPlatformNotificationOrchestrationServiceApi(): PlatformNotificationOrchestrationServiceApi?

    fun getConversationApi(): ConversationApi

    fun getAttachmentApi(url: String): AttachmentApi

    fun getSchedulesApi(): SchedulesApi

    fun getContactManagementPolicyApi(): ContactManagementPolicyApi

    fun getSipApi(): SipApi

    fun setupBroadsoftUserApi(url: String?)

    fun clearBroadsoftUserApiManager()

    fun clearPlatformApiManager()

    fun clearPlatformApi()

    fun getMessagesApi(): SmsMessagesApi

    fun clearPlatformNotificationOrchestrationServiceApi()

    fun getMediaCallApi(): MediaCallApi?

    fun getCalendarApi(): CalendarApi?
}