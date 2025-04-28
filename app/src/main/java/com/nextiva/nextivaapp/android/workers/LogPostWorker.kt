package com.nextiva.nextivaapp.android.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.LogUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

@HiltWorker
class LogPostWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val mDbManager: DbManager,
    private val mPlatformRepository: PlatformRepository,
    private val mSipManager: PJSipManager,
    private val mConnectionStateManager: ConnectionStateManager
) : Worker(appContext, workerParams) {

    companion object {
        private const val WORKER_INTERVAL: Long = 24
        private const val BACKOFF_INTERVAL: Long = 1

        fun getRequest(): PeriodicWorkRequest {
            val constraints = Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

            return PeriodicWorkRequest
                .Builder(LogPostWorker::class.java, WORKER_INTERVAL, TimeUnit.HOURS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_INTERVAL, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        }
    }

    private val mCompositeDisposable = CompositeDisposable()

    override fun doWork(): Result {
        return if (!mSipManager.isCallActive() && mConnectionStateManager.internetConnectionType == Enums.InternetConnectTypes.WIFI) {
            LogUtil.postLogsToKibana(mCompositeDisposable, mPlatformRepository, mDbManager)
            Result.success()
        } else {
            Result.retry()
        }
    }
}
