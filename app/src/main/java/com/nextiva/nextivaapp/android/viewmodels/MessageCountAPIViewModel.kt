/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.util.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver
import javax.inject.Inject

@HiltViewModel
class MessageCountAPIViewModel @Inject constructor(nextivaApplication: Application, val conversationRepository: ConversationRepository, val sessionManager: SessionManager) : BaseViewModel(nextivaApplication) {

    val apiVoiceCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val apiVoicemailCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val apiSMSCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val apiChatCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val apiEmailCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val apiMeetingCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val apiSurveyCountLiveData: MutableLiveData<Int> = MutableLiveData()

    private fun getBadgeCounts() {
        val readMessagesSingleSms = conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.SMS, Enums.Messages.ReadStatus.READ)
        val unreadMessagesSingleSms = conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.SMS, Enums.Messages.ReadStatus.UNREAD)

        Single.zip(readMessagesSingleSms, unreadMessagesSingleSms) { readCount, unreadCount -> readCount + unreadCount }
                .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.SMS))

        // disabled until this feature is working again on app
        /*
        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.CHAT, Enums.Messages.ReadStatus.UNREAD)
            .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.CHAT))
        */

        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.VOICE, Enums.Messages.ReadStatus.UNREAD)
            .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.VOICE))

        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.VOICEMAIL, Enums.Messages.ReadStatus.UNREAD)
            .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.VOICEMAIL))

        // disabled until this feature is working again on app
        /*
        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.MEETING, Enums.Messages.ReadStatus.UNREAD)
            .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.MEETING))

        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.EMAIL, Enums.Messages.ReadStatus.UNREAD)
            .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.EMAIL))

        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.SURVEY, Enums.Messages.ReadStatus.UNREAD)
            .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.SURVEY))
        */
    }

    private fun channelMessagesCountSingleObserver(@Enums.Messages.Channels.Channel type: String) = object : DisposableSingleObserver<Int>() {
        override fun onSuccess(count: Int) {
            when(type)
            {
                Enums.Messages.Channels.VOICE ->
                    apiVoiceCountLiveData.postValue(count)
                Enums.Messages.Channels.VOICEMAIL ->
                    apiVoicemailCountLiveData.postValue(count)
                Enums.Messages.Channels.SMS ->
                    apiSMSCountLiveData.postValue(count)
                Enums.Messages.Channels.CHAT ->
                    apiChatCountLiveData.postValue(count)
                Enums.Messages.Channels.SURVEY ->
                    apiSurveyCountLiveData.postValue(count)
                Enums.Messages.Channels.MEETING ->
                    apiMeetingCountLiveData.postValue(count)
                Enums.Messages.Channels.EMAIL ->
                    apiEmailCountLiveData.postValue(count)
                else ->
                    LogUtil.e("Channel Messages Count Error type: $type")
            }

        }

        override fun onError(e: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun loadCount() {
        getBadgeCounts()
    }
}