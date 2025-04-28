package com.nextiva.nextivaapp.android

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.databinding.ActivityActiveCallBinding
import com.nextiva.nextivaapp.android.fragments.OneActiveCallFragment
import com.nextiva.nextivaapp.android.fragments.OneActiveCallFragment.ActiveCallFragmentListener
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.models.PermissionRequest
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class OneActiveCallActivity : BaseActivity(), ActiveCallFragmentListener {
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var sipManager: PJSipManager

    private var participantInfo: ParticipantInfo? = null
    private var retrievalNumber: String? = null

    private val mActiveCallObserver = Observer { sipCall: SipCall? ->
        sipCall?.let {
            if (it.participantInfoList.isNotEmpty()) {
                participantInfo = sipCall.participantInfoList.firstOrNull()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindViews())

        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        if (!mConnectionStateManager.isInternetConnected) {
            onLastCallEnded()
        }

       // sipManager.clearCallTimer()

        val openedFromNotification = intent.getBooleanExtra(Constants.EXTRA_OPENED_FROM_NOTIFICATION, false)
        processIntent(intent)

        participantInfo = intent.getSerializableExtra(Constants.Calls.PARAMS_PARTICIPANT_INFO) as ParticipantInfo?

        if (!TextUtils.isEmpty(intent.getStringExtra(Constants.Calls.PARAMS_RETRIEVAL_NUMBER))) {
            retrievalNumber = intent.getStringExtra(Constants.Calls.PARAMS_RETRIEVAL_NUMBER)
        }

        if (participantInfo == null) {
            sipManager.activeCallLiveData.value?.let { sipCall ->
                if (sipCall.participantInfoList.isNotEmpty()) {
                    participantInfo = sipCall.participantInfoList.firstOrNull()
                }
            }
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(
            R.id.active_call_activity_master_layout,
            OneActiveCallFragment.newInstance(
                participantInfo,
                retrievalNumber,
                openedFromNotification
            )
        )
        transaction.commit()

        try {
            val data = Intent()
            data.putExtra(Constants.EXTRA_CALLED_NUMBER, participantInfo?.numberToCall)
            setResult(RESULT_OK, data)

        } catch (e: Exception) {
            LogUtil.d("Error adding Call Info: $e")
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        val permissionsObserver: Observer<PermissionRequest>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionsObserver =
                Observer { permissionRequest: PermissionRequest ->
                    requestPermissions(
                        Objects.requireNonNull(permissionRequest).permissions,
                        permissionRequest.resultsCode
                    )
                }

        //    sipManager.permissionsLiveData.observe(this, permissionsObserver)
        }

      //  sipManager.activeCallSessionLiveData.observe(this, mActiveCallObserver)
    }

    fun bindViews(): View {
        val binding = ActivityActiveCallBinding.inflate(layoutInflater)
        overrideEdgeToEdge(binding.root)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        analyticsManager.logScreenView(ScreenName.ACTIVE_CALL)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent)
        participantInfo = intent.getSerializableExtra(Constants.Calls.PARAMS_PARTICIPANT_INFO) as ParticipantInfo?

        if (!TextUtils.isEmpty(intent.getStringExtra(Constants.Calls.PARAMS_RETRIEVAL_NUMBER))) {
            retrievalNumber = intent.getStringExtra(Constants.Calls.PARAMS_RETRIEVAL_NUMBER)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.BACK_BUTTON_PRESSED)
    }

    private fun processIntent(intent: Intent) {
        if (intent.getBooleanExtra(Constants.EXTRA_OPENED_FROM_NOTIFICATION, false)) {
            analyticsManager.logEvent(ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.RETURN_TO_ACTIVE_CALL_PRESSED)
            intent.putExtra(Constants.EXTRA_OPENED_FROM_NOTIFICATION, false)
        }
    }

    override fun onLastCallEnded() {
        if (isFinishing) {
            return
        }

//        if (!sipManager.isCallQueued && !sipManager.isRegistered) {
//            if (isTaskRoot) {
//                finishAndRemoveTask()
//            }
//
//            if (sipManager.isCallQueued) {
//                sipManager.cancelQueuedCall()
//            }
//
//            sipManager.cancelOnCallNotification()
//        }

        if(isTaskRoot){
            startActivity(ConnectMainActivity.newIntent(this))
        } else{
            finish()
        }
    }

    companion object {

        fun newIntent(context: Context?, participantInfo: ParticipantInfo?, retrievalNumber: String?): Intent {
            val intent = Intent(context, OneActiveCallActivity::class.java)
            intent.putExtra(Constants.Calls.PARAMS_PARTICIPANT_INFO, participantInfo)
            if (!TextUtils.isEmpty(retrievalNumber)) {
                intent.putExtra(Constants.Calls.PARAMS_RETRIEVAL_NUMBER, retrievalNumber)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            return intent
        }
    }
}