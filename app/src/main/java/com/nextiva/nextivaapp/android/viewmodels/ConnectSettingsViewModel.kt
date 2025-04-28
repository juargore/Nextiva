package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.androidNextivaAuth.domain.repository.AuthImplementation
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.navigationDrawer.NavigationItemModel
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformNotificationOrchestrationServiceRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.managers.interfaces.WebSocketManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.leolin.shortcutbadger.ShortcutBadgeException
import me.leolin.shortcutbadger.ShortcutBadger
import javax.inject.Inject

@HiltViewModel
class ConnectSettingsViewModel @Inject constructor(
    application: Application,
    val nextivaApplication: Application,
    val configManager: ConfigManager,
    val sessionManager: SessionManager,
    val dbManager: DbManager,
    val dbManagerKt: DbManagerKt,
    val logManager: LogManager,
    val pushNotificationManager: PushNotificationManager,
    val netManager: NetManager,
    val schedulerProvider: SchedulerProvider,
    val webSocketManager: WebSocketManager,
    private val platformNOSRepository: PlatformNotificationOrchestrationServiceRepository,
    val userRepository: UserRepository,
    val platformRepository: PlatformRepository,
    val roomsDbManager: RoomsDbManager,
    val contactsRepository: PlatformContactsRepository,
    val mediaPlayer: NextivaMediaPlayer,
    val smsManagementRepository: SmsManagementRepository,
    val sharedPreferencesManager: SharedPreferencesManager,
    val notificationManager: NotificationManager,
    val sipManager: PJSipManager,
    private val authRepository: AuthImplementation
) : BaseViewModel(application) {

    companion object {
        private const val TAG = "ConnectSettingsViewModel"
    }
    private var userAvatarLiveData: MutableLiveData<AvatarInfo> = MutableLiveData()
    val userContactDetailsLiveData: MutableLiveData<NextivaContact?> = MutableLiveData()
    var finishSignOutLiveData: MutableLiveData<Void?> = MutableLiveData()
    val activeCallLiveData = sipManager.activeCallLiveData

    init {
        dbManager.getContactFromContactTypeIdLiveData(sessionManager.userInfo?.comNextivaUseruuid)
                .observeForever {
                    userContactDetailsLiveData.value = it
                }
        dbManager.ownConnectPresenceLiveData.observeForever { getUsersAvatar() }
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

    private fun getUsersAvatarWithFullName(): AvatarInfo {
        return AvatarInfo.Builder()
                .setDisplayName(getUsersFullName())
                .setPresence(sessionManager.connectUserPresence)
                .setFontAwesomeIconResId(R.string.fa_user)
                .isConnect(true)
                .build()
    }

    fun getUsersFullName(): String? {
        return sessionManager.userDetails?.fullName
    }

    fun getNavigationItemModels(): ArrayList<NavigationItemModel> {
        val navigationItemsList: ArrayList<NavigationItemModel> = ArrayList()

        navigationItemsList.add(NavigationItemModel(R.drawable.icon_user_details, nextivaApplication.getString(R.string.main_nav_user_details)))
        navigationItemsList.add(NavigationItemModel(R.drawable.ic_nav_call_settings_icon, nextivaApplication.getString(R.string.main_nav_call_settings)))
        navigationItemsList.add(NavigationItemModel(R.drawable.ic_nav_preferences_icon, nextivaApplication.getString(R.string.main_nav_preferences)))

        if (sessionManager.featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.PERSONAL_SCHEDULES)) {
            navigationItemsList.add(NavigationItemModel(R.drawable.ic_nav_notifications_icon, nextivaApplication.getString(R.string.main_nav_notifications)))
        }

        navigationItemsList.add(NavigationItemModel(R.drawable.ic_nav_about_icon, nextivaApplication.getString(R.string.main_nav_about)))
        navigationItemsList.add(NavigationItemModel(R.drawable.ic_nav_help_icon, nextivaApplication.getString(R.string.main_nav_help)))
        navigationItemsList.add(NavigationItemModel(R.drawable.ic_nav_sign_out_icon, nextivaApplication.getString(R.string.main_nav_sign_out)))

        return navigationItemsList
    }

    fun getUsersAvatarLiveData(): LiveData<AvatarInfo> {
        return userAvatarLiveData
    }

    fun signOut() {
        viewModelScope.launch(context = Dispatchers.IO) {
            LogUtil.postLogsToKibana(mCompositeDisposable, platformRepository, dbManager)

            try {
                val deleteResponse = async { platformNOSRepository.deleteDevice(null).blockingGet() }
                val unregisterResponse = async { userRepository.unregisterForPushNotifications().blockingGet() }
                val logout = sessionManager.sessionId?.let {
                    async { authRepository.syncLogout(it) }
                }
                Log.d(TAG, "Signing Out : DeleteDevice [${deleteResponse.await().orFalse()}] : UnregisterPushNotifications [${unregisterResponse.await()?.isSuccessful}] : logout [${logout?.await()?.getOrNull().orFalse()}]")
            } catch(e: Exception) {
                Log.d(TAG, "Signing out error: ${e.message}")
            }

            mCompositeDisposable.add(
                Completable
                    .fromAction {
                        nextivaApplication.cacheDir.deleteRecursively()
                        nextivaApplication.filesDir.deleteRecursively()
                        pushNotificationManager.disableFCM()
                        webSocketManager.stopConnection()
                        dbManager.expireContactCache()
                        dbManager.clearAndResetAllTables()
                        configManager.mobileConfig = null
                        roomsDbManager.clearAndResetAllTables()
                        contactsRepository.resetLastRefreshTimestamp()
                        sessionManager.userDetails = null
                        sessionManager.token = ""
                        notificationManager.cancelAllNotifications()
                        try {
                            ShortcutBadger.removeCountOrThrow(nextivaApplication)
                        } catch (e: ShortcutBadgeException) {
                            logManager.logToFile(
                                Enums.Logging.STATE_ERROR,
                                "Shortcut Badger Exception: $e"
                            )
                        }
                    }
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .onErrorComplete { throwable ->
                        logManager.logToFile(
                            Enums.Logging.STATE_ERROR,
                            "Error signing out: " + throwable.localizedMessage
                        )
                        FirebaseCrashlytics.getInstance().recordException(throwable)
                        true
                    }.subscribe {
                        sharedPreferencesManager.setInt(
                            SharedPreferencesManager.PENDO_CALL_TRACKING_COUNTER,
                            0
                        )
                        sharedPreferencesManager.removeKey(SharedPreferencesManager.PENDO_FIRST_VISIT_ANDROID)
                        netManager.clearBroadsoftUserApiManager()
                        mediaPlayer.reset()
                        smsManagementRepository.setIsFetchingMessages(false)
                        finishSignOutLiveData.value = null
                    })
        }
    }

    suspend fun getContactFromPhoneNumber(phoneNumber: String): NextivaContact? {
        return dbManagerKt.getContactFromPhoneNumberInThread(phoneNumber).value
    }
}