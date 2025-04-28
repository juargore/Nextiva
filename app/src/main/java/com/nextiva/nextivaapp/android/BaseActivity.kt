/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.NextivaNotificationManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ProductsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.PollingManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.managers.interfaces.WebSocketManager
import com.nextiva.nextivaapp.android.net.buses.RxEvents.PhoneInformationResponseEvent
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.ApplicationUtil.updateNightMode
import com.nextiva.nextivaapp.android.util.LogUtil
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import java.util.UUID
import javax.inject.Inject

@SuppressLint("Registered")
@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var mSipManager: PJSipManager
    @Inject
    lateinit var mConnectionStateManager: ConnectionStateManager
    @Inject
    lateinit var mSchedulerProvider: SchedulerProvider
    @Inject
    lateinit var mDialogManager: DialogManager
    @Inject
    lateinit var mDbManager: DbManager
    @Inject
    lateinit var mSharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var mSettingsManager: SettingsManager
    @Inject
    lateinit var mNotificationManager: NotificationManager
    @Inject
    lateinit var mIntentManager: IntentManager
    @Inject
    lateinit var mSessionManager: SessionManager
    @Inject
    lateinit var mSmsManagementRepository: SmsManagementRepository
    @Inject
    lateinit var mProductsRepository: ProductsRepository
    @Inject
    lateinit var mPlatformRepository: PlatformRepository
    @Inject
    lateinit var mLogManager: LogManager
    @Inject
    lateinit var mPollingManager: PollingManager
    @Inject
    lateinit var mWebSocketManager: WebSocketManager
    @Inject
    lateinit var mUmsRepository: UmsRepository
    @Inject
    lateinit var mPresenceRepository: PresenceRepository

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        LogUtil.log("onCreate: " + javaClass.simpleName)

        mLogManager.addDBManager(mDbManager)
        mLogManager.addConnectionStateManager(mConnectionStateManager)
        mLogManager.addCompositeDisposable(compositeDisposable)
        mLogManager.addPlatformRepository(mPlatformRepository)
        mLogManager.addSessionManager(mSessionManager)

        if (mSessionManager.userDetails == null && intent.hasExtra(NextivaNotificationManager.LAUNCHED_FROM_NOTIFICATION)) {
            startActivity(mIntentManager.getInitialIntent(this))
            finish()
            return
        }

        if (TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.XMPP_RESOURCE_UUID, ""))) {
            mSharedPreferencesManager.setString(SharedPreferencesManager.XMPP_RESOURCE_UUID, UUID.randomUUID().toString().replace("-", ""))
        }

        updateNightMode(mSettingsManager, this)

        // TODO: SIP: Setup SIP permissions here
        //mSipManager.setupSipPermissions(this);
        if (!mSipManager.isPhoneSpeakerOn()) {
            mSipManager.setProximityDetection(Enums.AppStates.ACTIVE_FORGROUND)
        }

        mNotificationManager.showSIPStateNotification(mSipManager)

        if (!mSipManager.isCallActive()) mNotificationManager.cancelNotification(Enums.Notification.TypeIDs.ON_CALL)
    }

    fun overrideEdgeToEdge(view: View) {
        ViewGroupCompat.installCompatInsetsDispatch(view)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v: View, insets: WindowInsetsCompat ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun onStart() {
        super.onStart()
        LogUtil.log("onStart: " + javaClass.simpleName)
        if (!mSipManager.isPhoneSpeakerOn()) {
            mSipManager.setProximityDetection(Enums.AppStates.ACTIVE_FORGROUND)
        }

        mNotificationManager.showSIPStateNotification(mSipManager)
    }

    override fun onResume() {
        super.onResume()
        LogUtil.log("onResume: " + javaClass.simpleName)

        setStatusBarColor(ContextCompat.getColor(this, R.color.connectGrey01))

        if (!mSipManager.isPhoneSpeakerOn()) {
            mSipManager.setProximityDetection(Enums.AppStates.ACTIVE_FORGROUND)
        }

        mNotificationManager.showSIPStateNotification(mSipManager)
    }

    override fun onPause() {
        super.onPause()
        LogUtil.log("onPause: " + javaClass.simpleName)
        if (!mSipManager.isPhoneSpeakerOn()) {
            mSipManager.setProximityDetection(Enums.AppStates.PAUSED)
        }

        mNotificationManager.showSIPStateNotification(mSipManager)

        compositeDisposable.clear()
    }

    override fun onStop() {
        super.onStop()
        LogUtil.log("onStop: " + javaClass.simpleName)
        if (!mSipManager.isCallActive()) {
            mSipManager.setProximityDetection(Enums.AppStates.STOPPED)
        }

        mNotificationManager.showSIPStateNotification(mSipManager)
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mSipManager.isCallActive()) {
            mSipManager.setProximityDetection(Enums.AppStates.DESTORYED)
        }

        LogUtil.log("onDestroy: " + javaClass.simpleName)
        mNotificationManager.showSIPStateNotification(mSipManager)
        compositeDisposable.clear()
    }

    override fun onRestart() {
        super.onRestart()
        if (!mSipManager.isPhoneSpeakerOn()) {
            mSipManager.setProximityDetection(Enums.AppStates.ACTIVE_FORGROUND)
        }

        LogUtil.log("onRestart: " + javaClass.simpleName)
        mNotificationManager.showSIPStateNotification(mSipManager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        LogUtil.log("onCreateOptionsMenu: " + javaClass.simpleName)
        return super.onCreateOptionsMenu(menu)
    }

    fun logError(throwable: Throwable) {
        mLogManager.logToFile(Enums.Logging.STATE_ERROR, throwable.localizedMessage)
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    val allSmsMessages: Unit
        get() {
            if (mSessionManager.userDetails != null && !mSessionManager.isNextivaConnectEnabled) {
                mSmsManagementRepository.getSmsConversations().subscribe()
            }
        }

    fun updateProducts() {
        if (mSessionManager.userDetails != null) {
            mProductsRepository.refreshLicenses()
                .subscribe(object : DisposableSingleObserver<PhoneInformationResponseEvent?>() {
                    override fun onSuccess(phoneInformationResponseEvent: PhoneInformationResponseEvent) {
                        mUmsRepository.unregisterDevice().subscribe()
                    }

                    override fun onError(e: Throwable) {}
                })
        }
    }

    fun sendInitialPresencePing() {
        mPresenceRepository.sendPresencePing()
    }

    fun startPolling() {
    }

    fun stopPolling() {
    }

    fun updateFeatureFlags() {
        if (mSessionManager.userInfo != null) {
            mPlatformRepository.getFeatureFlags().subscribe()
        }
    }

    fun startConnectWebSocketConnection() {
        if (!mConnectionStateManager.isConnectWebSocketConnected) {
            mWebSocketManager.setup()
        }
    }

    fun stopConnectWebSocketConnection() {
        if (mConnectionStateManager.isConnectWebSocketConnected) {
            mWebSocketManager.stopConnection()
        }
    }

    fun enableCallLogDataDogEvent() {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.ENABLE_CALL_LOG_DATADOG_EVENT, true)
    }

    fun setStatusBarColor(statusBarColor: Int) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            window.statusBarColor = statusBarColor

        } else {
            val isDisplayingActiveCallBanner = mSipManager.isCallActive() && this.javaClass != OneActiveCallActivity::class.java
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)

            if (isDisplayingActiveCallBanner) {
                insetsController.isAppearanceLightStatusBars = false
            } else {
                insetsController.isAppearanceLightStatusBars = !isNightModeEnabled(this, mSettingsManager)
            }

            if (isDisplayingActiveCallBanner) {
                window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.connectPrimaryGreen))
            } else {
                window.decorView.setBackgroundColor(statusBarColor)
            }

            window.insetsController?.hide(WindowInsets.Type.systemBars())
            window.insetsController?.show(WindowInsets.Type.systemBars())
        }
    }
}
