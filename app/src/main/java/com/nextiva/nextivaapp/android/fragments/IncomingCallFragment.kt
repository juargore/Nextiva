package com.nextiva.nextivaapp.android.fragments

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.databinding.IncomingCallFragmentBinding
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager.PermissionGrantedCallback
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.IncomingCall
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.extensions.serializable
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher
import com.nextiva.nextivaapp.android.viewmodels.IncomingCallViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class IncomingCallFragment : BaseFragment() {
    private var incomingCall: IncomingCall? = null
    private var displayName: String? = null
    private var phoneNumber: String? = null

    @Enums.Sip.CallTypes.Type
    private var callType = Enums.Sip.CallTypes.VOICE
    private var participantInfo: ParticipantInfo? = null
    private var callCheckHandler = Handler(Looper.getMainLooper())
    private var callCheckRunnable: Runnable? = null
    private var callCheckDelay = 1000
    private var callerAvatarView: AvatarView? = null
    private var incomingCallNameTextView: TextView? = null
    private var incomingCallNumberTextView: TextView? = null
    private var incomingCallAnswerVoiceCallButton: AppCompatButton? = null
    private var incomingCallAnswerVideoCallButton: AppCompatButton? = null
    private var incomingCallDeclineButton: AppCompatButton? = null

    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var permissionManager: PermissionManager

    private val recordAudioRequestCode = 1
    private var fragmentListener: IncomingCallFragmentListener? = null
    private lateinit var viewModel: IncomingCallViewModel
    private var answerAction = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity == null) {
            return
        }

        displayName = requireArguments().getString(Constants.Calls.PARAMS_DISPLAY_NAME, "")
        phoneNumber = requireArguments().getString(Constants.Calls.PARAMS_PHONE_NUMBER, "")
        callType = requireArguments().getInt(Constants.Calls.PARAMS_CALL_TYPE)
        participantInfo = requireArguments().serializable(Constants.Calls.PARAMS_PARTICIPANT_INFO)
        incomingCall = requireArguments().serializable(Constants.Calls.PARAMS_INCOMING_CALL)
        answerAction = requireArguments().getInt(Constants.Calls.PARAMS_ANSWER_ACTION, Enums.Sip.CallTypes.NONE)

        checkCallIsActiveStartTimer()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = try {
            context as IncomingCallFragmentListener
        } catch (e: ClassCastException) {
            throw UnsupportedOperationException(context.javaClass.getSimpleName() + " must implement ContactsListFragmentListener.")
        }
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        //mViewModel = ViewModelProviders.of(this).get(IncomingCallViewModel.class);
//        // TODO: Use the ViewModel
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        LogUtil.d("Incoming Call Fragment onCreateView")
        val view = bindViews(inflater, container)
        if (activity == null) {
            return view
        }

        requireActivity().window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            requireActivity().setShowWhenLocked(true)
            requireActivity().setTurnScreenOn(true)
        }

        wakeUpScreen()

        viewModel = ViewModelProvider(this)[IncomingCallViewModel::class.java]

        if (answerAction != Enums.Sip.CallTypes.NONE) {
            answerVoiceCall()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val contact = async { viewModel.getContactFromPhoneNumber(phoneNumber) }.await()

            withContext(Dispatchers.Main) {
                setUpViews(contact, displayName ?: "")
            }
        }

        return view
    }

    override fun onResume() {
        LogUtil.d("Incoming Call Fragment Resume")
        super.onResume()
        checkCallIsActiveStartTimer()
        checkIncomingCallIsActive()
        analyticsManager.logScreenView(ScreenName.INCOMING_CALL)
    }

    override fun onStop() {
        LogUtil.d("Incoming Call Fragment Stop")
        super.onStop()
        mDialogManager.dismissAllDialogs()
    }

    override fun onDestroyView() {
        LogUtil.d("Incoming Call Fragment Destroy View")
        super.onDestroyView()
    }

    private fun bindViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding = IncomingCallFragmentBinding.inflate(inflater, container, false)
        callerAvatarView = binding.incomingCallCallerAvatarView
        incomingCallNameTextView = binding.incomingCallNameTextView
        incomingCallNumberTextView = binding.incomingCallNumberTextView
        incomingCallAnswerVoiceCallButton = binding.incomingCallAnswerVoiceCallButton
        incomingCallAnswerVideoCallButton = binding.incomingCallAnswerVideoCallButton
        incomingCallDeclineButton = binding.incomingCallDeclineButton

        incomingCallAnswerVoiceCallButton?.setOnClickListener { onAnswerVoiceCallButtonClicked() }
        incomingCallDeclineButton?.setOnClickListener { onDeclineButtonClicked() }
        return binding.getRoot()
    }

    private fun onAnswerVoiceCallButtonClicked() {
        answerVoiceCall()
    }

    private fun onDeclineButtonClicked() {
        LogUtil.d("Incoming Call Fragment onDeclineButtonClicked")
        viewModel.declineCall()

        if (activity != null && incomingCall?.pushNotificationCallInfo != null) {
            requireActivity().finishAndRemoveTask()

        } else {
            analyticsManager.logEvent(ScreenName.INCOMING_CALL, Enums.Analytics.EventName.DECLINE_CALL_BUTTON_PRESSED)
            finishActivity()
        }
    }

    private fun setUpViews(nextivaContact: NextivaContact?, rawDisplayName: String?) {
        LogUtil.d("Incoming Call Fragment setUpViews")
        val displayName = rawDisplayName ?: nextivaContact?.displayName

        participantInfo = nextivaContact?.getParticipantInfo(phoneNumber) ?: ParticipantInfo()

        participantInfo?.displayName = displayName

        if (displayName != null && !TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(displayName.trim { it <= ' ' })) {
            incomingCallNameTextView?.text = displayName

            when {
                !TextUtils.isEmpty(phoneNumber) && TextUtils.isDigitsOnly(phoneNumber) && incomingCallNumberTextView != null -> incomingCallNumberTextView?.text = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().country)
                !TextUtils.isEmpty(phoneNumber) -> incomingCallNumberTextView?.text = phoneNumber
                else -> incomingCallNumberTextView?.text = ""
            }

            incomingCallNumberTextView?.addTextChangedListener(ExtensionEnabledPhoneNumberFormattingTextWatcher())

        } else {
            when {
                !TextUtils.isEmpty(phoneNumber) && TextUtils.isDigitsOnly(phoneNumber) -> incomingCallNameTextView?.text = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().country)
                !TextUtils.isEmpty(phoneNumber) -> incomingCallNumberTextView?.text = phoneNumber
                else -> incomingCallNumberTextView?.text = ""
            }

            incomingCallNameTextView?.addTextChangedListener(ExtensionEnabledPhoneNumberFormattingTextWatcher())
        }

        callerAvatarView?.setAvatar(AvatarInfo.Builder()
            .setSize(AvatarInfo.SIZE_LARGE)
            .setDisplayName(participantInfo?.displayName)
            .build())

        if (activity != null) {
            if (callType == Enums.Sip.CallTypes.VIDEO) {
                incomingCallAnswerVideoCallButton?.visibility = View.VISIBLE
                permissionManager.requestVideoCallPermission(
                    requireActivity(),
                    ScreenName.INCOMING_CALL,
                    null,
                    null
                )
            } else {
                //TODO: This should be handled by Dexter but it breaks in SAMSUNG Android 11
                //mPermissionManager.requestVoiceCallPermission(getActivity(), INCOMING_CALL, getPermissionGrantedVoiceCallCallback(), getPermissionDeniedVoiceCallCallback());
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), recordAudioRequestCode)
            }
        }
    }

    private fun wakeUpScreen() {
        //make sure screen is on and unlocked?
        if (activity != null) {
            val km = requireActivity().applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val locked = Objects.requireNonNull(km).inKeyguardRestrictedInputMode()

            if (locked) {
                //mSipManager.wakeUpDueToIncomingVoip();
            }
        }
    }

    private fun checkCallIsActiveStartTimer() {
        callCheckHandler.postDelayed(Runnable {
            callCheckHandler.postDelayed(callCheckRunnable!!, callCheckDelay.toLong()) //Post must be before checkIncomingCallIsActive or it will loop forever
            checkIncomingCallIsActive()
        }.also { callCheckRunnable = it }, callCheckDelay.toLong())
    }

    private fun finishActivity() {
        LogUtil.d("Incoming Call Fragment finishActivity")
        fragmentListener?.onFinished()
    }

    interface IncomingCallFragmentListener {
        fun onFinished()
    }

    private fun getPermissionGrantedAnswerCallCallback(): PermissionGrantedCallback {
        return PermissionGrantedCallback {
            if (viewModel.doesSipHaveIncomingCall()) {
                viewModel.answerCall()
                finishActivity()
            }
        }
    }

    /* TODO: This should be handled by Dexter but it breaks in SAMSUNG Android 11
    private PermissionManager.PermissionGrantedCallback getPermissionGrantedVoiceCallCallback() {
        return () -> {
            mIncomingTestTextView.setText("Record Permission is Granted");
        };
    }

    private PermissionManager.PermissionDeniedCallback getPermissionDeniedVoiceCallCallback() {
        return () -> {
            mIncomingTestTextView.setText("Record Permission is DENIED");
        };
    }
    */
    private fun answerVoiceCall() {
        participantInfo?.callType = Enums.Sip.CallTypes.VOICE

        if (incomingCall?.pushNotificationCallInfo?.participantInfo == null) {
            incomingCall?.pushNotificationCallInfo?.participantInfo = participantInfo
        }

        if (activity != null) {
            permissionManager.requestVoiceIncomingCallPermission(
                requireActivity(),
                ScreenName.INCOMING_CALL,
                getPermissionGrantedAnswerCallCallback(),
                view
            )
        }

        analyticsManager.logEvent(ScreenName.INCOMING_CALL, Enums.Analytics.EventName.ACCEPT_VOICE_CALL_BUTTON_PRESSED)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == recordAudioRequestCode) {
            if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(activity, R.string.permission_record_audio_contacts_denied_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIncomingCallIsActive() {
        if (!viewModel.doesSipHaveIncomingCall()) {
            callCheckHandler.removeCallbacks(callCheckRunnable!!)
            LogUtil.d("Incoming Call checkIncomingCallIsActive should finish")
            finishActivity()
        }
    }

    companion object {
        fun newInstance(): IncomingCallFragment {
            return IncomingCallFragment()
        }

        @JvmStatic
        fun newInstance(incomingCall: IncomingCall): IncomingCallFragment {
            val fragment = IncomingCallFragment()
            val args = Bundle()

            (incomingCall.pushNotificationCallInfo?.participantInfo ?: incomingCall.participantInfo)?.let {
                args.putString(Constants.Calls.PARAMS_PHONE_NUMBER, it.numberToCall)
                args.putString(Constants.Calls.PARAMS_DISPLAY_NAME, it.displayName)
                args.putInt(Constants.Calls.PARAMS_CALL_TYPE, Enums.Sip.CallTypes.VOICE)
                args.putSerializable(Constants.Calls.PARAMS_PARTICIPANT_INFO, it)
            }

            args.putSerializable(Constants.Calls.PARAMS_INCOMING_CALL, incomingCall)
            fragment.setArguments(args)
            return fragment
        }

        @JvmStatic
        fun newInstance(incomingCall: IncomingCall, @Enums.Sip.CallTypes.Type action: Int): IncomingCallFragment {
            val fragment = IncomingCallFragment()
            val args = Bundle()

            incomingCall.pushNotificationCallInfo?.participantInfo?.let {
                args.putString(Constants.Calls.PARAMS_PHONE_NUMBER, it.numberToCall)
                args.putString(Constants.Calls.PARAMS_DISPLAY_NAME, it.displayName)
                args.putInt(Constants.Calls.PARAMS_CALL_TYPE, Enums.Sip.CallTypes.VOICE)
                args.putSerializable(Constants.Calls.PARAMS_PARTICIPANT_INFO, it)
            }

            args.putSerializable(Constants.Calls.PARAMS_INCOMING_CALL, incomingCall)
            args.putInt(Constants.Calls.PARAMS_ANSWER_ACTION, action)
            fragment.setArguments(args)
            return fragment
        }
    }
}