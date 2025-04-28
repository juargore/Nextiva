package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.androidNextivaAuth.data.datasource.network.dto.UserInfo
import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformNotificationOrchestrationServiceRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AppUpdateManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.DatadogManager
import com.nextiva.nextivaapp.android.managers.interfaces.LocalContactsManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.PendoManager
import com.nextiva.nextivaapp.android.managers.interfaces.PollingManager
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.managers.interfaces.WebSocketManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.BottomNavigationItem
import com.nextiva.nextivaapp.android.models.IdentityVoice
import com.nextiva.nextivaapp.android.models.net.platform.AccountInformation
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.view.ConnectBottomNavigationView
import com.nextiva.nextivaapp.android.workers.LogPostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectMainViewModel @Inject constructor(
    application: Application,
    var sessionManager: SessionManager,
    var dbManager: DbManager,
    val configManager: ConfigManager,
    val blockingNumberManager: BlockingNumberManager,
    var localContactsManager: LocalContactsManager,
    var presenceRepository: PresenceRepository,
    var platformContactsRepository: PlatformContactsRepository,
    var conversationRepository: ConversationRepository,
    private val appUpdateManager: AppUpdateManager,
    private val platformNOSRepository: PlatformNotificationOrchestrationServiceRepository,
    val userRepository: UserRepository,
    val schedulerProvider: SchedulerProvider,
    val sipManager: PJSipManager,
    val notificationManager: NotificationManager,
    val callManagementRepository: CallManagementRepository,
    val webSocketManager: WebSocketManager,
    val platformRepository: PlatformRepository,
    val sharedPreferencesManager: SharedPreferencesManager,
    val pushNotificationManager: PushNotificationManager,
    val datadogManager: DatadogManager,
    private val pendoManager: PendoManager,
    private val logManager: LogManager,
    private val pollingManager: PollingManager
) : BaseViewModel(application) {

    private val workManager =
        WorkManager.getInstance(application.applicationContext)

    val activeCallLiveData = sipManager.activeCallLiveData
    private val unreadSmsLiveData = dbManager.totalUnreadMessagesCount
    private val unreadVoiceCallVoicemailLiveData = sessionManager.voiceCallVoicemailCount
    private val unreadRoomsLiveData = sessionManager.getRoomsMessagesCountLiveData(listOf(
        RoomsEnums.ConnectRoomsTypes.MY_ROOM.value,
        RoomsEnums.ConnectRoomsTypes.PRIVATE_ROOM.value,
        RoomsEnums.ConnectRoomsTypes.PUBLIC_ROOM.value,
        RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value
    ))
    private val unreadChatsLiveData = sessionManager.getRoomsMessagesCountLiveData(listOf(
        RoomsEnums.ConnectRoomsTypes.INDIVIDUAL_CONVERSATION.value,
        RoomsEnums.ConnectRoomsTypes.MY_CONVERSATION.value,
        RoomsEnums.ConnectRoomsTypes.GROUP_CONVERSATION.value
    ))
    val unreadVoiceCallVoicemailMediatorLiveData = MediatorLiveData<Int>()
    val unreadChatSmsMediatorLiveData = MediatorLiveData<Int>()
    val unreadRoomsMediatorLiveData = MediatorLiveData<Int>()
    val unreadChatsMediatorLiveData = MediatorLiveData<Int>()
    private var userAvatarLiveData: MutableLiveData<AvatarInfo> = MutableLiveData()
    var onEditModeEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _pendoFirstVisit = MutableLiveData<String>()
    val pendoFirstVisit: LiveData<String> = _pendoFirstVisit

    init {

        unreadVoiceCallVoicemailMediatorLiveData.addSource(unreadVoiceCallVoicemailLiveData) {
            var count = 0

            if(!unreadVoiceCallVoicemailLiveData.value.isNullOrEmpty())
                for(unreadCount in unreadVoiceCallVoicemailLiveData.value!!) {
                    if(unreadCount.value != null)
                        count += unreadCount.value!!.toInt()
                }

            unreadVoiceCallVoicemailMediatorLiveData.value = count
        }

        unreadChatSmsMediatorLiveData.addSource(unreadSmsLiveData) {
            unreadChatSmsMediatorLiveData.value = unreadSmsLiveData.value?.toInt() ?: 0
        }

        unreadRoomsMediatorLiveData.addSource(unreadRoomsLiveData){
            unreadRoomsMediatorLiveData.value = unreadRoomsLiveData.value ?: 0
        }

        unreadChatsMediatorLiveData.addSource(unreadChatsLiveData){
            unreadChatsMediatorLiveData.value = unreadChatsLiveData.value ?: 0
        }

        dbManager.ownConnectPresenceLiveData.observeForever { getUsersAvatar() }
        presenceRepository.getPresences()

        pollingManager.startPolling(viewModelScope)

    }

    fun getUsersAvatar() {
        userAvatarLiveData.value = getUsersAvatarWithFullName()

        configManager.mobileConfig?.xmpp?.username?.let { username ->
            mCompositeDisposable.add(
                dbManager.getAvatarInfo(username)
                    .subscribe { avatarInfo ->
                        avatarInfo.presence = sessionManager.connectUserPresence
                        avatarInfo.fontAwesomeIconResId = R.string.fa_user
                        userAvatarLiveData.value = avatarInfo
                    }
            )
        }
    }

    fun getLocalContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            localContactsManager.localContacts.subscribe()
            platformContactsRepository.fetchContacts(false, { })
        }
    }

    fun getUsersAvatarLiveData(): LiveData<AvatarInfo> {
        return userAvatarLiveData
    }

    fun getUsersAvatarWithFullName(): AvatarInfo {
        return AvatarInfo.Builder()
            .setDisplayName(getUsersFullName())
            .setPresence(sessionManager.connectUserPresence)
            .setFontAwesomeIconResId(R.string.fa_user)
            .isConnect(true)
            .build()
    }

    private fun getUsersFullName(): String? {
        return sessionManager.userDetails?.fullName
    }

    fun getUsersPresence(): DbPresence? {
        return sessionManager.userPresence
    }

    fun getUserIsSuperAdmin() {
        sessionManager.sessionId?.let { sessionId ->
            platformRepository.isUserSuperAdmin(
                accountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                sessionId = sessionId
            ).subscribe(
                { response ->
                    if (response != null) {
                        sharedPreferencesManager.setBoolean(SharedPreferencesManager.IS_USER_SUPER_ADMIN, response)
                    }
                }, { error ->
                    if (error != null) FirebaseCrashlytics.getInstance().recordException(error)
                }
            ).let { mCompositeDisposable.add(it) }
        }

        getSMSCampaignStatus()
    }

    private fun getSMSCampaignStatus() {
        sessionManager.sessionId?.let { sessionId ->
            viewModelScope.launch(Dispatchers.IO) {
                platformRepository.getSMSCampaignStatus(
                    accountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                    sessionId = sessionId
                ).collectLatest { status ->
                    if (!status.isNullOrEmpty()) {
                        sharedPreferencesManager.setString(SharedPreferencesManager.SMS_CAMPAIGN_STATUS, status)
                    }
                }
            }

        }
    }

    fun getDevicePolicies() {
        sessionManager.sessionId?.let {
            sessionManager.userInfo?.comNextivaUseruuid?.let { it1 ->
                platformRepository.getDevicePolicies(
                    sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                    it,
                    it1
                )
                    .subscribe({ response ->
                        if (response != null) {
                            response.enableCallDecline?.let {
                                sharedPreferencesManager.setBoolean(
                                    SharedPreferencesManager.USER_SETTINGS_DEVICES_POLICIES_CALL_DECLINE_ENABLED,
                                    it
                                )
                            }
                        }

                    }, {
                        if(it != null) FirebaseCrashlytics.getInstance().recordException(it)
                    }
                    )?.let {
                        mCompositeDisposable.add(
                            it
                        )
                    }
            }
        }
    }

    fun createLogPostWorker() {
        workManager.enqueueUniquePeriodicWork(
            Enums.Workers.LOG_POST,
            ExistingPeriodicWorkPolicy.KEEP,
            LogPostWorker.getRequest()
        )
    }

    fun initialLoad() {
        datadogManager.setUserInfo()

        pushNotificationManager.enableFCMWithResponse { token ->
            if (!token.isNullOrEmpty()) {
                logManager.logToFile(Enums.Logging.STATE_INFO, "Generated Push Notification Token = $token")

                platformNOSRepository.registerForSmsPushNotifications(token)

                userRepository.registerForPushNotifications(token)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(object : DisposableSingleObserver<RxEvents.RegisterForPushNotificationsResponseEvent>() {
                        override fun onSuccess(isSuccessful: RxEvents.RegisterForPushNotificationsResponseEvent) {
                            userRepository.removeExpiredPushNotificationRegistrations(compositeDisposable)
                        }

                        override fun onError(e: Throwable) {
                            logManager.logToFile(Enums.Logging.STATE_FAILURE, "Error registering for push notifications $e")
                            LogUtil.e("PushNotificationManager", "Error registering for push notifications $e")
                        }
                    })
            } else {
                logManager.logToFile(Enums.Logging.STATE_FAILURE, "FCM token is null or empty")
                LogUtil.e("PushNotificationManager", "FCM token is null or empty")
            }
        }

        Completable
            .fromAction {
                webSocketManager.setup()
                processAppUpdates()
                updateAllSMSPendingStatusToFailed()
            }
            .subscribeOn(schedulerProvider.io())
            .subscribe()
    }

    private fun updateAllSMSPendingStatusToFailed() {
        dbManager.updateAllSmsSentStatus().subscribe(object : DisposableCompletableObserver() {
            override fun onComplete() {}
            override fun onError(e: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        })
    }

    fun cancelNotifications() {
        if (!sipManager.isRegistered() && !sipManager.isCallActive()) {
            notificationManager.cancelAllNotifications()
        }
    }

    private fun processAppUpdates() {
        appUpdateManager.processUpdates(BuildConfig.VERSION_NAME)
    }

    fun hasLocalContacts(): Boolean {
        return dbManager.localContactsCount > 0
    }

    fun getUserDetails() = sessionManager.userDetails

    fun getUserAccount(): AccountInformation? = sessionManager.accountInformation

    fun isSmsMessagesEnabled() = sessionManager.isShowSms

    fun isMeetingEnabled(context: Context) = sessionManager.isMeetingEnabled(context)

    fun isRoomsEnabled(context: Context) = sessionManager.isTeamchatEnabled(context)

    fun isTeamChatEnabled(context: Context) = sessionManager.isTeamchatEnabled(context)

    fun isSmsLicenseEnabled() = sessionManager.isSmsLicenseEnabled

    fun isSmsProvisioningEnabled() = sessionManager.isSmsProvisioningEnabled

    fun isTeamSmsEnabled() = sessionManager.isTeamSmsEnabled 

    fun setUnreadBadgeCounts(context: Context) = sessionManager.updateNotificationsCount(conversationRepository, context)

    fun getMoreMenuListItems(bottomNavigation: ConnectBottomNavigationView): ArrayList<BottomNavigationItem>
    {
        val listItems = ArrayList<BottomNavigationItem>(bottomNavigation.getBottomNavigationItems())

        for(x in 0 until bottomNavigation.menu.size() - 1) {
            if (listItems.size > 0)
                listItems.removeFirstOrNull()
            }

        return listItems
    }

    fun onEditModeClicked(isEnabled: Boolean){
        onEditModeEnabledLiveData.postValue(isEnabled)
    }

    fun clearSession(){
        setSessionId("")
        setVoiceIdentity("")
        setUserInfo("")
        setSessionTenant("")
    }
    private fun setSessionTenant(sessionTenant: String) {
        sessionManager.setSelectedTenant(sessionTenant)
    }
    private fun setSessionId(sessionId: String) {
        sessionManager.setSessionId(sessionId)
    }
    private fun setUserInfo(userInfo: String) {
        val mUserInfo = GsonUtil.getObject(
            UserInfo::class.java, userInfo
        )
        sessionManager.setUserInfo(mUserInfo)
    }
    private fun setVoiceIdentity(identityVoice: String) {
        val mIdentityVoice = GsonUtil.getObject(
            IdentityVoice::class.java, identityVoice
        )
        sessionManager.setIdentityVoice(mIdentityVoice)
    }

    fun fetchPendoFirstVisit(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            pendoManager.getPendoData(email)?.let { timestamp ->
                _pendoFirstVisit.postValue(timestamp)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingManager.stopPolling()
    }
}