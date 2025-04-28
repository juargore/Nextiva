///*
// * Copyright (c) 2017 Nextiva, Inc. to Present.
// * All rights reserved.
// */
//
package com.nextiva.nextivaapp.android.fragments
//
//import android.app.Activity
//import android.content.Intent
//import android.view.View
//import android.view.WindowManager
//import android.widget.*
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.NewCallActivity
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.constants.Constants
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.constants.FragmentTags
//import com.nextiva.nextivaapp.android.constants.RequestCodes
//import com.nextiva.nextivaapp.android.constants.RequestCodes.NewCall.NEW_CALL_REQUEST_CODE
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeActiveCallListenerActivity
//import com.nextiva.nextivaapp.android.models.CallInfo
//import com.nextiva.nextivaapp.android.models.NextivaContact
//import com.nextiva.nextivaapp.android.models.SingleEvent
//import com.nextiva.nextivaapp.android.sip.CallSession
//import com.nextiva.nextivaapp.android.view.ActiveCallButtonView
//import com.nextiva.nextivaapp.android.view.CallerInformationView
//import com.nextiva.nextivaapp.android.viewmodels.ActiveCallViewModel
//import com.nextiva.nextivaapp.android.viewmodels.DialerKeypadDialogViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.hamcrest.CoreMatchers.instanceOf
//import org.junit.Assert.*
//import org.junit.Test
//import org.robolectric.Shadows
//import org.robolectric.shadows.ShadowApplication
//import org.robolectric.shadows.ShadowDrawable
//import org.robolectric.shadows.ShadowToast
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import javax.inject.Inject
//
//class ActiveCallFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    @Inject
//    lateinit var dialogManager: DialogManager
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    private lateinit var fragment: ActiveCallFragment
//
//    private val mockFragmentListener: ActiveCallFragment.ActiveCallFragmentListener = mock()
//    private val mockViewModel: ActiveCallViewModel = mock()
//    private val mockDialpadViewModel: DialerKeypadDialogViewModel = mock()
//    private val mockIsSpeakerSelectedLiveData: LiveData<Boolean> = mock()
//    private val mockIsMuteSelectedLiveData: LiveData<Boolean> = mock()
//    private val mockIsVideoEnabledLiveData: LiveData<Boolean> = mock()
//    private val mockActiveCallSessionLiveData: LiveData<CallSession> = mock()
//    private val mockInactiveCallSessionLiveData: LiveData<CallSession> = mock()
//    private val mockActiveCallStatusLiveData: LiveData<String> = mock()
//    private val mockActiveCallDurationLiveData: LiveData<String> = mock()
//    private val mockCameraStateLiveData: LiveData<Int> = mock()
//    private val mockStartNewCallLiveData: LiveData<SingleEvent<Boolean>> = mock()
//    private val mockInviteFailureLiveData: LiveData<SingleEvent<Long>> = mock()
//    private val mockRequestToAddVideoLiveData: LiveData<SingleEvent<CallInfo>> = mock()
//    private val mockIsRemoteHoldLiveData: LiveData<Boolean> = mock()
//
//    private val callInfo: CallInfo = CallInfo.Builder().setNumberToCall("2223334444").build()
//
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(ActiveCallViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(viewModelFactory.create(DialerKeypadDialogViewModel::class.java)).thenReturn(mockDialpadViewModel)
//        whenever(mockViewModel.isSpeakerEnabledLiveData).thenReturn(mockIsSpeakerSelectedLiveData)
//        whenever(mockViewModel.isMuteEnabledLiveData).thenReturn(mockIsMuteSelectedLiveData)
//        whenever(mockViewModel.isVideoEnabledLiveData).thenReturn(mockIsVideoEnabledLiveData)
//        whenever(mockViewModel.activeCallSessionLiveData).thenReturn(mockActiveCallSessionLiveData)
//        whenever(mockViewModel.passiveCallSessionLiveData).thenReturn(mockInactiveCallSessionLiveData)
//        whenever(mockViewModel.activeCallStatusLiveData).thenReturn(mockActiveCallStatusLiveData)
//        whenever(mockViewModel.activeCallDurationLiveData).thenReturn(mockActiveCallDurationLiveData)
//        whenever(mockViewModel.cameraStateLiveData).thenReturn(mockCameraStateLiveData)
//        whenever(mockViewModel.startNewCallLiveData).thenReturn(mockStartNewCallLiveData)
//        whenever(mockViewModel.inviteFailureLiveData).thenReturn(mockInviteFailureLiveData)
//        whenever(mockViewModel.requestToAddVideoLiveData).thenReturn(mockRequestToAddVideoLiveData)
//        whenever(mockViewModel.isRemoteHoldLiveData).thenReturn(mockIsRemoteHoldLiveData)
//
//        whenever(mockDialpadViewModel.heightMetric).thenReturn(240)
//        whenever(mockDialpadViewModel.widthMetric).thenReturn(120)
//
//        fragment = ActiveCallFragment.newInstance(callInfo)
//        SupportFragmentTestUtil.startFragment(fragment, FakeActiveCallListenerActivity::class.java)
//        fragment.setFragmentListener(mockFragmentListener)
//    }
//
//    override fun after() {
//        super.after()
//        fragment.onPause()
//        fragment.onStop()
//        fragment.onDestroy()
//    }
//
//    @Test
//    fun newInstance_correctlySetsExtras() {
//        val fragment = ActiveCallFragment.newInstance(callInfo)
//
//        assertEquals(callInfo, fragment.arguments!![Constants.Calls.PARAMS_CALL_INFO])
//    }
//
//    @Test
//    fun onCreateView_noNumber_callsToViewModel() {
//        fragment = ActiveCallFragment.newInstance(CallInfo.Builder().build())
//        SupportFragmentTestUtil.startFragment(fragment, FakeActiveCallListenerActivity::class.java)
//
//        verify(mockViewModel).endCall()
//    }
//
//    @Test
//    fun onCreateView_hasVoiceCallCallInfo_callsToViewModel() {
//        val callInfo = CallInfo.Builder().setCallType(Enums.Sip.CallTypes.VOICE).setNumberToCall("1112223333").build()
//
//        fragment = ActiveCallFragment.newInstance(callInfo)
//        SupportFragmentTestUtil.startFragment(fragment, FakeActiveCallListenerActivity::class.java)
//
//    }
//
//    @Test
//    fun onCreateView_observesViewModelLiveDatas() {
//        verify(mockIsVideoEnabledLiveData).observe(eq(fragment), any())
//        verify(mockIsSpeakerSelectedLiveData).observe(eq(fragment), any())
//        verify(mockActiveCallStatusLiveData).observe(eq(fragment), any())
//        verify(mockCameraStateLiveData).observe(eq(fragment), any())
//        verify(mockStartNewCallLiveData).observe(eq(fragment), any())
//        verify(mockActiveCallDurationLiveData).observe(eq(fragment), any())
//        verify(mockRequestToAddVideoLiveData).observe(eq(fragment), any())
//        verify(mockInviteFailureLiveData).observe(eq(fragment), any())
//        verify(mockActiveCallSessionLiveData).observe(eq(fragment), any())
//        verify(mockInactiveCallSessionLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun onActivityResult_callsToViewModel() {
//        val newCallInfo: CallInfo = CallInfo.Builder().build()
//        val data = Intent()
//        data.putExtra(Constants.EXTRA_CALL_INFO, newCallInfo)
//
//        fragment.onActivityResult(RequestCodes.NewCall.NEW_CALL_REQUEST_CODE, Activity.RESULT_OK, data)
//
//        verify(mockViewModel).startCall(newCallInfo, null, fragment.mLocalVideoLayout, fragment.mRemoteVideoLayout)
//    }
//
//    @Test
//    fun onActivityResult_voiceCallCallInfo_callsToViewModel() {
//        reset(mockViewModel)
//
//        val newCallInfo: CallInfo = CallInfo.Builder().setCallType(Enums.Sip.CallTypes.VOICE).build()
//        val data = Intent()
//        data.putExtra(Constants.EXTRA_CALL_INFO, newCallInfo)
//
//        fragment.onActivityResult(RequestCodes.NewCall.NEW_CALL_REQUEST_CODE, Activity.RESULT_OK, data)
//
//        verify(mockViewModel).setVideo(fragment.activity!!, Enums.Analytics.ScreenName.ACTIVE_CALL, false, fragment.mLocalVideoLayout, fragment.mRemoteVideoLayout)
//    }
//
//    @Test
//    fun onActivityResult_videoCallCallInfo_callsToViewModel() {
//        val newCallInfo: CallInfo = CallInfo.Builder().setCallType(Enums.Sip.CallTypes.VIDEO).build()
//        val data = Intent()
//        data.putExtra(Constants.EXTRA_CALL_INFO, newCallInfo)
//
//        fragment.onActivityResult(RequestCodes.NewCall.NEW_CALL_REQUEST_CODE, Activity.RESULT_OK, data)
//
//        verify(mockViewModel).setVideo(fragment.activity!!, Enums.Analytics.ScreenName.ACTIVE_CALL, true, fragment.mLocalVideoLayout, fragment.mRemoteVideoLayout)
//    }
//
//    @Test
//    fun onMuteCallButtonClicked_callsToViewModel() {
//        val muteCallButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_mute_button)
//
//        if (muteCallButton.isClickable) {
//            muteCallButton.performClick()
//        }
//
//        verify(mockViewModel).toggleMute()
//    }
//
//    @Test
//    fun onMuteCallButtonClicked_selectingMute_callsToAnalyticsManager() {
//        val muteCallButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_mute_button)
//
//        if (muteCallButton.isClickable) {
//            muteCallButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.MUTE_BUTTON_SELECTED)
//    }
//
//    @Test
//    fun onMuteCallButtonClicked_deselectingMute_callsToAnalyticsManager() {
//        val muteCallButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_mute_button)
//        muteCallButton.isSelected = true
//
//        if (muteCallButton.isClickable) {
//            muteCallButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.MUTE_BUTTON_DESELECTED)
//    }
//
//    @Test
//    fun onHoldButtonClicked_callsToViewModel() {
//        val holdButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_hold_button)
//
//        if (holdButton.isClickable) {
//            holdButton.performClick()
//        }
//
//        verify(mockViewModel).toggleHold(fragment.mLocalVideoLayout, fragment.mRemoteVideoLayout)
//    }
//
//    @Test
//    fun onHoldCallButtonClicked_selectingHold_callsToAnalyticsManager() {
//        val holdButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_hold_button)
//
//        if (holdButton.isClickable) {
//            holdButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.HOLD_BUTTON_SELECTED)
//    }
//
//    @Test
//    fun onHoldCallButtonClicked_deselectingHold_callsToAnalyticsManager() {
//        val holdButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_hold_button)
//        holdButton.isSelected = true
//
//        if (holdButton.isClickable) {
//            holdButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.HOLD_BUTTON_DESELECTED)
//    }
//
//    @Test
//    fun onSpeakerButtonClicked_callsToViewModel() {
//        val speakerButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_speaker_button)
//
//        if (speakerButton.isClickable) {
//            speakerButton.performClick()
//        }
//
//        verify(mockViewModel).toggleSpeaker()
//    }
//
//    @Test
//    fun onSpeakerButtonClicked_selectingSpeaker_callsToAnalyticsManager() {
//        val speakerButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_speaker_button)
//
//        if (speakerButton.isClickable) {
//            speakerButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.SPEAKER_BUTTON_SELECTED)
//    }
//
//    @Test
//    fun onSpeakerButtonClicked_deselectingSpeaker_callsToAnalyticsManager() {
//        val speakerButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_speaker_button)
//        speakerButton.isSelected = true
//
//        if (speakerButton.isClickable) {
//            speakerButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.SPEAKER_BUTTON_DESELECTED)
//    }
//
//    @Test
//    fun onKeypadButtonClicked_startsFragment() {
//        val keypadButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_keypad_button)
//
//        if (keypadButton.isClickable) {
//            keypadButton.performClick()
//        }
//
//        val keypadFragment: Fragment? = fragment.childFragmentManager.findFragmentByTag(FragmentTags.DIALER_KEYPAD_DIALOG)
//
//        assertNotNull(keypadFragment)
//        assertThat(keypadFragment, instanceOf(DialerKeypadDialogFragment::class.java))
//    }
//
//    @Test
//    fun onKeypadButtonClicked_callsToAnalyticsManager() {
//        val keypadButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_keypad_button)
//
//        if (keypadButton.isClickable) {
//            keypadButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.KEYPAD_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onEndCallButtonClicked_callsToViewModel() {
//        val endCallButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_end_call_button)
//
//        if (endCallButton.isClickable) {
//            endCallButton.performClick()
//        }
//
//        verify(mockViewModel).endCall()
//    }
//
//    @Test
//    fun onEndCallButtonClicked_callsToAnalyticsManager() {
//        val endCallButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_end_call_button)
//
//        if (endCallButton.isClickable) {
//            endCallButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.END_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onVideoButtonClicked_callsToViewModel() {
//        val videoButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_video_add_participant_button)
//
//        if (videoButton.isClickable) {
//            videoButton.performClick()
//        }
//
//        verify(mockViewModel).toggleVideo(fragment.activity!!, Enums.Analytics.ScreenName.ACTIVE_CALL, fragment.mLocalVideoLayout, fragment.mRemoteVideoLayout)
//    }
//
//    @Test
//    fun onVideoButtonClicked_selectingVideo_callsToAnalyticsManager() {
//        val videoButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_video_add_participant_button)
//
//        if (videoButton.isClickable) {
//            videoButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.VIDEO_BUTTON_SELECTED)
//    }
//
//    @Test
//    fun onVideoButtonClicked_deselectingVideo_callsToAnalyticsManager() {
//        val videoButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_video_add_participant_button)
//        videoButton.isSelected = true
//
//        if (videoButton.isClickable) {
//            videoButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.VIDEO_BUTTON_DESELECTED)
//    }
//
//    @Test
//    fun onSwitchCameraButtonClicked_callsToAnalyticsManager() {
//        val switchCameraButton: ImageButton = fragment.view!!.findViewById(R.id.active_call_switch_camera_button)
//
//        if (switchCameraButton.isClickable) {
//            switchCameraButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.SWITCH_CAMERA_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onSwitchCameraButtonClicked_callsToViewModel() {
//        val switchCameraButton: ImageButton = fragment.view!!.findViewById(R.id.active_call_switch_camera_button)
//
//        if (switchCameraButton.isClickable) {
//            switchCameraButton.performClick()
//        }
//
//        verify(mockViewModel).toggleCamera()
//    }
//
//    @Test
//    fun onNewCallSwapButtonClicked_callsToViewModel() {
//        val newCallSwapButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_new_call_swap_button)
//
//        if (newCallSwapButton.isClickable) {
//            newCallSwapButton.performClick()
//        }
//
//        verify(mockViewModel).toggleNewCallSwap(any(), any())
//    }
//
//    @Test
//    fun onHoldCallerInformationClicked_disablesNewCallSwapButton() {
//        val holdCallerInformationView: CallerInformationView = fragment.view!!.findViewById(R.id.active_call_hold_caller_information_view)
//        val newCallSwapButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_new_call_swap_button)
//
//        if (holdCallerInformationView.isClickable) {
//            holdCallerInformationView.performClick()
//        }
//
//        assertFalse(newCallSwapButton.isEnabled)
//    }
//
//    @Test
//    fun onHoldCallerInformationClicked_callsToViewModel() {
//        val holdCallerInformationView: CallerInformationView = fragment.view!!.findViewById(R.id.active_call_hold_caller_information_view)
//
//        if (holdCallerInformationView.isClickable) {
//            holdCallerInformationView.performClick()
//        }
//
//        verify(mockViewModel).swapCalls(any(), any())
//    }
//
//    @Test
//    fun onHoldCallerInformationClicked_callsToAnalyticsManager() {
//        val holdCallerInformationView: CallerInformationView = fragment.view!!.findViewById(R.id.active_call_hold_caller_information_view)
//
//        if (holdCallerInformationView.isClickable) {
//            holdCallerInformationView.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.ON_HOLD_CALL_HEADER_PRESSED)
//    }
//
//    @Test
//    fun onKeyPressed_callsToViewModel() {
//        fragment.onKeyPressed("3")
//
//        verify(mockViewModel).playDialerKeyPress("3")
//    }
//
//    @Test
//    fun activeCallSessionObserverValueUpdated_nullValue_callsToFragmentListener() {
//        val argumentCaptor: KArgumentCaptor<Observer<CallSession>> = argumentCaptor()
//        verify(mockActiveCallSessionLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        verify(mockFragmentListener).onLastCallEnded()
//    }
//
////    @Test
////    fun activeCallObserverValueUpdated_nullValue_setsNewCallSwapButtonEnabled() {
////        val newCallSwapButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_new_call_swap_button)
////        newCallSwapButton.isEnabled = false
////
////        val argumentCaptor: KArgumentCaptor<Observer<CallInfo>> = argumentCaptor()
////        verify(mockActiveCallSessionLiveData).observe(eq(fragment), argumentCaptor.capture())
////
////        argumentCaptor.firstValue.onChanged(null)
////
////        assertTrue(newCallSwapButton.isEnabled)
////    }
//
//    @Test
//    fun activeCallSessionObserverValueUpdated_validValue_setsActiveCallerInfoValues() {
//        val callerNameTextView: TextView = fragment.mActiveCallerInformationView.findViewById(R.id.caller_information_caller_name_text_view)
//        val callerNumberTextView: TextView = fragment.mActiveCallerInformationView.findViewById(R.id.caller_information_caller_number_text_view)
//
//        val nextivaContact = NextivaContact("1")
//        nextivaContact.firstName = "Jim"
//        nextivaContact.lastName = "Smith"
//
//        val callSession1 = CallSession()
//        val callSession2 = CallSession()
//        val callSession3 = CallSession()
//
//        val newCallInfo1: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .build()
//        val newCallInfo2: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("Display Name")
//                .build()
//        val newCallInfo3: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("Display Name3")
//                .setNextivaContact(nextivaContact)
//                .build()
//
//        callSession1.callInfoArrayList.add(newCallInfo1)
//        callSession2.callInfoArrayList.add(newCallInfo2)
//        callSession3.callInfoArrayList.add(newCallInfo3)
//        callSession1.callState = Enums.Sip.CallStateFlag.CONNECTED
//        callSession2.callState = Enums.Sip.CallStateFlag.CONNECTED
//        callSession3.callState = Enums.Sip.CallStateFlag.CONNECTED
//
//        val argumentCaptor: KArgumentCaptor<Observer<CallSession>> = argumentCaptor()
//        verify(mockActiveCallSessionLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(callSession1)
//
//        assertEquals("(222) 333-4444", callerNameTextView.text.toString())
//        assertEquals("", callerNumberTextView.text.toString())
//
//        argumentCaptor.firstValue.onChanged(callSession2)
//
//        assertEquals("Display Name", callerNameTextView.text.toString())
//        assertEquals("(222) 333-4444", callerNumberTextView.text.toString())
//
//        argumentCaptor.firstValue.onChanged(callSession3)
//
//        assertEquals("Display Name3", callerNameTextView.text.toString())
//        assertEquals("(222) 333-4444", callerNumberTextView.text.toString())
//    }
//
//    @Test
//    fun activeCallSessionObserverValueUpdated_validValue_setsNewCallSwapButtonEnabled() {
//        val callSession1 = CallSession()
//        val newCallInfo: CallInfo = CallInfo.Builder().build()
//        callSession1.callInfoArrayList.add(newCallInfo)
//        callSession1.callState = Enums.Sip.CallStateFlag.CONNECTED
//
//        val newCallSwapButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_new_call_swap_button)
//        newCallSwapButton.isEnabled = false
//
//        val argumentCaptor: KArgumentCaptor<Observer<CallSession>> = argumentCaptor()
//        verify(mockActiveCallSessionLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(callSession1)
//
//        assertTrue(newCallSwapButton.isEnabled)
//    }
//
//    @Test
//    fun activeCallStatusObserverValueUpdated_nullValue_setsActiveCallStatusValue() {
//        val callStatusTextView: TextView = fragment.mActiveCallerInformationView.findViewById(R.id.caller_information_status_text_view)
//
//        val argumentCaptor: KArgumentCaptor<Observer<String>> = argumentCaptor()
//        verify(mockActiveCallStatusLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        assertEquals("", callStatusTextView.text.toString())
//    }
//
//
//    @Test
//    fun inactiveCallObserverValueUpdated_nullValue_setsInactiveCallerInfoValues() {
//        val newCallImageSwitcher: ImageSwitcher = fragment.mNewCallSwapButton.findViewById(R.id.active_call_image_switcher) as ImageSwitcher
//        val newCallTextSwitcher: TextSwitcher = fragment.mNewCallSwapButton.findViewById(R.id.active_call_button_text_switcher) as TextSwitcher
//
//        val argumentCaptor: KArgumentCaptor<Observer<CallSession>> = argumentCaptor()
//        verify(mockInactiveCallSessionLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        val shadowDrawable: ShadowDrawable = Shadows.shadowOf((newCallImageSwitcher.currentView as ImageView).drawable)
//        assertEquals(R.drawable.ic_call_new, shadowDrawable.createdFromResId)
//        assertEquals("New Call", (newCallTextSwitcher.currentView as TextView).text.toString())
//        assertEquals(View.GONE, fragment.mHoldCallerInformationView.visibility)
//    }
//
//    @Test
//    fun inactiveCallObserverValueUpdated_nullValue_setsNewCallSwapButtonEnabled() {
//        val newCallSwapButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_new_call_swap_button)
//        newCallSwapButton.isEnabled = false
//
//        val argumentCaptor: KArgumentCaptor<Observer<CallSession>> = argumentCaptor()
//        verify(mockInactiveCallSessionLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//    }
//
//    @Test
//    fun inactiveCallObserverValueUpdated_validValue_setsInactiveCallerInfoValues() {
//        val newCallImageSwitcher: ImageSwitcher = fragment.mNewCallSwapButton.findViewById(R.id.active_call_image_switcher) as ImageSwitcher
//        val newCallTextSwitcher: TextSwitcher = fragment.mNewCallSwapButton.findViewById(R.id.active_call_button_text_switcher) as TextSwitcher
//        val callStatusTextView: TextView = fragment.mHoldCallerInformationView.findViewById(R.id.caller_information_status_text_view)
//        val callerNameTextView: TextView = fragment.mHoldCallerInformationView.findViewById(R.id.caller_information_caller_name_text_view)
//        val callerNumberTextView: TextView = fragment.mHoldCallerInformationView.findViewById(R.id.caller_information_caller_number_text_view)
//
//        val nextivaContact = NextivaContact("1")
//        nextivaContact.firstName = "Jim"
//        nextivaContact.lastName = "Smith"
//
//        val callSession1 = CallSession()
//        val callSession2 = CallSession()
//        val callSession3 = CallSession()
//
//        val newCallInfo1: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .build()
//        val newCallInfo2: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("Display Name")
//                .build()
//        val newCallInfo3: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("Display Name 3")
//                .setNextivaContact(nextivaContact)
//                .build()
//
//        callSession1.callInfoArrayList.add(newCallInfo1)
//        callSession2.callInfoArrayList.add(newCallInfo2)
//        callSession3.callInfoArrayList.add(newCallInfo3)
//        callSession1.callState = Enums.Sip.CallStateFlag.CONNECTED
//        callSession2.callState = Enums.Sip.CallStateFlag.CONNECTED
//        callSession3.callState = Enums.Sip.CallStateFlag.CONNECTED
//
//        val argumentCaptor: KArgumentCaptor<Observer<CallSession>> = argumentCaptor()
//        verify(mockInactiveCallSessionLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(callSession1)
//
//        assertEquals("(222) 333-4444", callerNameTextView.text.toString())
//        assertEquals("", callerNumberTextView.text.toString())
//
//        argumentCaptor.firstValue.onChanged(callSession2)
//
//        assertEquals("Display Name", callerNameTextView.text.toString())
//        assertEquals("(222) 333-4444", callerNumberTextView.text.toString())
//
//        argumentCaptor.firstValue.onChanged(callSession3)
//
//        assertEquals("Display Name 3", callerNameTextView.text.toString())
//        assertEquals("(222) 333-4444", callerNumberTextView.text.toString())
//
//        val shadowDrawable: ShadowDrawable = Shadows.shadowOf((newCallImageSwitcher.currentView as ImageView).drawable)
//        assertEquals(R.drawable.ic_swap_calls, shadowDrawable.createdFromResId)
//        assertEquals("Swap", (newCallTextSwitcher.currentView as TextView).text.toString())
//        assertEquals(View.VISIBLE, fragment.mHoldCallerInformationView.visibility)
//        assertEquals("ON HOLD", callStatusTextView.text.toString())
//    }
//
//    @Test
//    fun inactiveCallObserverValueUpdated_validValue_setsNewCallSwapButtonEnabled() {
//        val callSession1 = CallSession()
//        val newCallInfo: CallInfo = CallInfo.Builder().build()
//
//        val newCallSwapButton: ActiveCallButtonView = fragment.view!!.findViewById(R.id.active_call_new_call_swap_button)
//        newCallSwapButton.isEnabled = false
//
//        val argumentCaptor: KArgumentCaptor<Observer<CallSession>> = argumentCaptor()
//        verify(mockInactiveCallSessionLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        callSession1.callInfoArrayList.add(newCallInfo)
//        argumentCaptor.firstValue.onChanged(callSession1)
//
//    }
//
//    @Test
//    fun startNewCallObserverValueUpdated_nullValue_startsActivity() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockStartNewCallLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        val expectedIntent = NewCallActivity.newIntent(ApplicationProvider.getApplicationContext(), NEW_CALL_REQUEST_CODE)
//        val actualIntent = ShadowApplication.getInstance().nextStartedActivity
//        assertEquals(expectedIntent.component, actualIntent.component)
//    }
//
//    @Test
//    fun inviteFailedObserverValueUpdated_isFromPullCall_callToViewModel() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Long>>> = argumentCaptor()
//        whenever(mockViewModel.isCallPullCall).thenReturn(true)
//
//        verify(mockInviteFailureLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(1))
//
//        verify(mockViewModel).isCallPullCall
//        assertEquals(Toast.LENGTH_SHORT, ShadowToast.getLatestToast().duration)
//        assertEquals("No call available to pull", ShadowToast.getTextOfLatestToast())
//    }
//
//    @Test
//    fun requestToAddVideoObserverValueUpdated_callsToAnalyticsManager() {
//        val callInfo: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("My Name")
//                .build()
//
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<CallInfo>>> = argumentCaptor()
//
//        verify(mockRequestToAddVideoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(callInfo))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.ADD_VIDEO_TO_CALL_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun requestToAddVideoObserverValueUpdated_callsToDialogManager() {
//        val callInfo: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("My Name")
//                .build()
//
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<CallInfo>>> = argumentCaptor()
//
//        verify(mockRequestToAddVideoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(callInfo))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq("Upgrade to Video"),
//                eq("My Name wants to upgrade the call to a video call."),
//                eq("Accept"),
//                any(),
//                eq("Decline"),
//                any())
//    }
//
//    @Test
//    fun requestToAddVideoObserverValueUpdated_acceptsDialog_callsToAnalyticsManager() {
//        val callInfo: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("My Name")
//                .build()
//
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<CallInfo>>> = argumentCaptor()
//
//        verify(mockRequestToAddVideoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(callInfo))
//
//        val buttonArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq("Upgrade to Video"),
//                eq("My Name wants to upgrade the call to a video call."),
//                eq("Accept"),
//                buttonArgumentCaptor.capture(),
//                eq("Decline"),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonArgumentCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.ADD_VIDEO_TO_CALL_DIALOG_ACCEPT_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun requestToAddVideoObserverValueUpdated_acceptsDialog_callsToViewModelToAcceptAddVideo() {
//        val callInfo: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("My Name")
//                .build()
//
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<CallInfo>>> = argumentCaptor()
//
//        verify(mockRequestToAddVideoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(callInfo))
//
//        val buttonArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq("Upgrade to Video"),
//                eq("My Name wants to upgrade the call to a video call."),
//                eq("Accept"),
//                buttonArgumentCaptor.capture(),
//                eq("Decline"),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonArgumentCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(mockViewModel).acceptAddVideo(fragment.activity!!, Enums.Analytics.ScreenName.ACTIVE_CALL, fragment.mLocalVideoLayout, fragment.mRemoteVideoLayout)
//    }
//
//    @Test
//    fun requestToAddVideoObserverValueUpdated_declinesDialog_callsToAnalyticsManager() {
//        val nextivaContact = NextivaContact("1")
//        nextivaContact.firstName = "Jim"
//        nextivaContact.lastName = "Smith"
//
//        val callInfo: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("My Name")
//                .setNextivaContact(nextivaContact)
//                .build()
//
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<CallInfo>>> = argumentCaptor()
//
//        verify(mockRequestToAddVideoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(callInfo))
//
//        val buttonArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq("Upgrade to Video"),
//                eq("Jim Smith wants to upgrade the call to a video call."),
//                eq("Accept"),
//                any(),
//                eq("Decline"),
//                buttonArgumentCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonArgumentCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ACTIVE_CALL, Enums.Analytics.EventName.ADD_VIDEO_TO_CALL_DIALOG_DECLINE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun requestToAddVideoObserverValueUpdated_declinesDialog_callsToViewModelToDeclineAddVideo() {
//        val nextivaContact = NextivaContact("1")
//        nextivaContact.firstName = "Jim"
//        nextivaContact.lastName = "Smith"
//
//        val callInfo: CallInfo = CallInfo.Builder()
//                .setNumberToCall("2223334444")
//                .setDisplayName("My Name")
//                .setNextivaContact(nextivaContact)
//                .build()
//
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<CallInfo>>> = argumentCaptor()
//
//        verify(mockRequestToAddVideoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(callInfo))
//
//        val buttonArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq("Upgrade to Video"),
//                eq("Jim Smith wants to upgrade the call to a video call."),
//                eq("Accept"),
//                any(),
//                eq("Decline"),
//                buttonArgumentCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonArgumentCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(mockViewModel).declineAddVideo()
//    }
////
////    @Test
////    fun isMuteSelectedObserverValueUpdated_nullValue_setsMuteButtonSelected() {
////        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
////        verify(mockIsMuteSelectedLiveData).observe(eq(fragment), argumentCaptor.capture())
////
////        argumentCaptor.firstValue.onChanged(null)
////
////        assertFalse(fragment.mMuteButton.isSelected)
////    }
////
////    @Test
////    fun isMuteSelectedObserverValueUpdated_validValue_setsMuteButtonSelected() {
////        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
////        verify(mockIsMuteSelectedLiveData).observe(eq(fragment), argumentCaptor.capture())
////
////        argumentCaptor.firstValue.onChanged(true)
////
////        assertTrue(fragment.mMuteButton.isSelected)
////
////        argumentCaptor.firstValue.onChanged(false)
////
////        assertFalse(fragment.mMuteButton.isSelected)
////    }
////
////    @Test
////    fun isHoldSelectedObserverValueUpdated_nullValue_setsHoldButtonValues() {
////        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
////        verify(mockIsHoldSelectedLiveData).observe(eq(fragment), argumentCaptor.capture())
////
////        argumentCaptor.firstValue.onChanged(null)
////
////        val imageSwitcher: ImageSwitcher = fragment.mHoldButton.findViewById(R.id.active_call_image_switcher) as ImageSwitcher
////        val shadowDrawable: ShadowDrawable = Shadows.shadowOf((imageSwitcher.currentView as ImageView).drawable)
////        assertEquals(R.drawable.ic_pause, shadowDrawable.createdFromResId)
////    }
////
////    @Test
////    fun isHoldSelectedObserverValueUpdated_validValue_setsHoldButtonValues() {
////        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
////        verify(mockIsHoldSelectedLiveData).observe(eq(fragment), argumentCaptor.capture())
////
////        argumentCaptor.firstValue.onChanged(true)
////
////        val imageSwitcher: ImageSwitcher = fragment.mHoldButton.findViewById(R.id.active_call_image_switcher) as ImageSwitcher
////        var shadowDrawable: ShadowDrawable = Shadows.shadowOf((imageSwitcher.currentView as ImageView).drawable)
////        assertEquals(R.drawable.ic_play_arrow, shadowDrawable.createdFromResId)
////
////        val textSwitcher: TextSwitcher = fragment.mHoldButton.findViewById(R.id.active_call_button_text_switcher) as TextSwitcher
////        assertEquals("Resume", (textSwitcher.currentView as TextView).text.toString())
////
////        argumentCaptor.firstValue.onChanged(false)
////
////        shadowDrawable = Shadows.shadowOf((imageSwitcher.currentView as ImageView).drawable)
////        assertEquals(R.drawable.ic_pause, shadowDrawable.createdFromResId)
////
////        assertEquals("Hold", (textSwitcher.currentView as TextView).text.toString())
////    }
//
//    @Test
//    fun isSpeakerSelectedObserverValueUpdated_nullValue_setsSpeakerButtonSelected() {
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockIsSpeakerSelectedLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        assertFalse(fragment.mSpeakerButton.isSelected)
//    }
//
//    @Test
//    fun isSpeakerSelectedObserverValueUpdated_validValue_setsSpeakerButtonSelected() {
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockIsSpeakerSelectedLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        assertTrue(fragment.mSpeakerButton.isSelected)
//
//        argumentCaptor.firstValue.onChanged(false)
//
//        assertFalse(fragment.mSpeakerButton.isSelected)
//    }
//
//    @Test
//    fun isVideoEnabledObserverValueUpdated_nullValue_setsAddVideoButtonValues() {
//        val imageSwitcher: ImageSwitcher = fragment.mAddVideoAddParticipantButton.findViewById(R.id.active_call_image_switcher) as ImageSwitcher
//        val textSwitcher: TextSwitcher = fragment.mAddVideoAddParticipantButton.findViewById(R.id.active_call_button_text_switcher) as TextSwitcher
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockIsVideoEnabledLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        assertEquals(View.GONE, fragment.mVideoRemoteContainerLayout.visibility)
//        assertEquals(View.GONE, fragment.mVideoLocalContainerLayout.visibility)
//
//        val shadowDrawable: ShadowDrawable = Shadows.shadowOf((imageSwitcher.currentView as ImageView).drawable)
//        assertEquals(R.drawable.ic_video, shadowDrawable.createdFromResId)
//        assertEquals("Video", (textSwitcher.currentView as TextView).text.toString())
//        assertEquals(0, fragment.activity!!.window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//    }
//
//    @Test
//    fun isVideoEnabledObserverValueUpdated_validValue_setsAddVideoButtonValues() {
//        val imageSwitcher: ImageSwitcher = fragment.mAddVideoAddParticipantButton.findViewById(R.id.active_call_image_switcher) as ImageSwitcher
//        imageSwitcher.inAnimation = null
//        imageSwitcher.outAnimation = null
//
//        val textSwitcher: TextSwitcher = fragment.mAddVideoAddParticipantButton.findViewById(R.id.active_call_button_text_switcher) as TextSwitcher
//        textSwitcher.inAnimation = null
//        textSwitcher.outAnimation = null
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockIsVideoEnabledLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(false)
//
//        assertEquals(View.GONE, fragment.mVideoRemoteContainerLayout.visibility)
//        assertEquals(View.GONE, fragment.mVideoLocalContainerLayout.visibility)
//
//        var shadowDrawable: ShadowDrawable = Shadows.shadowOf((imageSwitcher.currentView as ImageView).drawable)
//        assertEquals(R.drawable.ic_video, shadowDrawable.createdFromResId)
//        assertEquals("Video", (textSwitcher.currentView as TextView).text.toString())
//        assertEquals(0, fragment.activity!!.window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        assertEquals(View.VISIBLE, fragment.mVideoRemoteContainerLayout.visibility)
//        assertEquals(View.VISIBLE, fragment.mVideoLocalContainerLayout.visibility)
//
//        assertEquals(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, fragment.activity!!.window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//    }
//
//    @Test
//    fun cameraStateObserverValueUpdated_nullValue_setsSwitchCameraImage() {
//        val argumentCaptor: KArgumentCaptor<Observer<Int>> = argumentCaptor()
//        verify(mockCameraStateLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        val shadowDrawable: ShadowDrawable = Shadows.shadowOf(fragment.mSwitchCameraButton.drawable)
//        assertEquals(R.drawable.ic_camera_front, shadowDrawable.createdFromResId)
//    }
//
//    @Test
//    fun cameraStateObserverValueUpdated_validValue_setsSwitchCameraImage() {
//        val argumentCaptor: KArgumentCaptor<Observer<Int>> = argumentCaptor()
//        verify(mockCameraStateLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(Enums.Sip.CameraTypes.FRONT_CAMERA)
//
//        var shadowDrawable: ShadowDrawable = Shadows.shadowOf(fragment.mSwitchCameraButton.drawable)
//        assertEquals(R.drawable.ic_camera_rear, shadowDrawable.createdFromResId)
//
//        argumentCaptor.firstValue.onChanged(Enums.Sip.CameraTypes.REAR_CAMERA)
//
//        shadowDrawable = Shadows.shadowOf(fragment.mSwitchCameraButton.drawable)
//        assertEquals(R.drawable.ic_camera_front, shadowDrawable.createdFromResId)
//    }
//}