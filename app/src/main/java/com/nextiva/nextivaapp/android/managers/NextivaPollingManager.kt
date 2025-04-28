package com.nextiva.nextivaapp.android.managers

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.AuthenticationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.PollingManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NextivaPollingManager @Inject constructor(
    private val mLogManager: LogManager,
    private val mConnectionStateManager: ConnectionStateManager,
    private val mPlatformRepository: PlatformRepository,
    val sessionManager: SessionManager,
    val mSmsRespository: SmsManagementRepository,
    private val mAuthenticationRepository: AuthenticationRepository
) : PollingManager {

    private val mHandler = Handler(Looper.getMainLooper())
    private val mRunnable = Runnable {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start)
        refreshSettings()
    }

    private var lastCacheTimestampMillis: Long = 0
    private var job : Job? = null
    private var scope: CoroutineScope? = null

    private fun refreshSettings(force: Boolean = false) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start)

        val nowInMillis = System.currentTimeMillis()
        val lastCacheExpirationMillis = nowInMillis - CACHE_EXPIRY_MILLIS
        val timeHasExpired = lastCacheTimestampMillis < lastCacheExpirationMillis
        val mandatory = mConnectionStateManager.isInternetConnected
                    && !TextUtils.isEmpty(sessionManager.accessDeviceTypeUrl)
                    && !mConnectionStateManager.isCallActive
        Log.d(
            "NextivaPollingManager",
            "[POLLING] : $mandatory && ($force || ${timeHasExpired})"
        )

        if (mandatory && (force || timeHasExpired)) {

            job?.cancel()
            job = scope?.launch(Dispatchers.IO) {

                try {
                    awaitAll(
                        async {
                            mAuthenticationRepository.getPollingDeviceSettings(
                                sessionManager.username
                            ).blockingGet()
                        },
                        async { mPlatformRepository.getFeatureFlags().blockingGet() },
                        // async { mSmsRespository.getUsersTeams().blockingGet() } // Note: uncomment to refresh user's teams, since is not very common teams change
                    )
                    lastCacheTimestampMillis = nowInMillis
                    mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start)
                    mHandler.postDelayed(mRunnable, CACHE_EXPIRY_MILLIS + 1)
                } catch (e: Exception) {
                    LogUtil.d("NextivaPollingManager", "[Setting Refresh Polling]: ${e.message}")
                }
            }
        } else {
            mHandler.postDelayed(mRunnable, CACHE_EXPIRY_MILLIS + 1)
        }
    }

    override fun stopPolling() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start)
        job?.cancel()
        mHandler.removeCallbacks(mRunnable)
    }

    override fun startPolling(scope: CoroutineScope) {
        this.scope = scope
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start)

        lastCacheTimestampMillis = System.currentTimeMillis()
        mHandler.removeCallbacks(mRunnable)
        refreshSettings(true)
    }

    companion object {
        private const val CACHE_EXPIRY_MILLIS = Constants.ONE_MINUTE_IN_MILLIS * 15
    }
}