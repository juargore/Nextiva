///*
// * Copyright (c) 2017 Nextiva, Inc. to Present.
// * All rights reserved.
// */
//
package com.nextiva.nextivaapp.android.fragments
//
//import android.widget.ImageButton
//import android.widget.ImageView
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeDialerListenerActivity
//import com.nextiva.nextivaapp.android.models.CallInfo
//import com.nextiva.nextivaapp.android.viewmodels.DialerViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertTrue
//import org.junit.Test
//import org.robolectric.fakes.RoboMenuItem
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment
//import java.io.IOException
//import javax.inject.Inject
//
//class DialerFragmentTest : BaseRobolectricTest() {
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
//    private lateinit var fragment: DialerFragment
//
//    private val mockViewModel: DialerViewModel = mock()
//    private val mockProcessedCallInfoLiveData: LiveData<CallInfo> = mock()
//    private val mockErrorStateLiveData: LiveData<Boolean> = mock()
//    private val mockLastDialedPhoneNumberLiveData: LiveData<String> = mock()
//    private val mockVoicemailCountLiveData: LiveData<Int> = mock()
//    private val mockFragmentListener: DialerFragment.DialerFragmentListener = mock()
//
//    @Throws(IOException::class)
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(DialerViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.processedCallInfoLiveData).thenReturn(mockProcessedCallInfoLiveData)
//        whenever(mockViewModel.errorStateLiveData).thenReturn(mockErrorStateLiveData)
//        whenever(mockViewModel.lastDialedPhoneNumberLiveData).thenReturn(mockLastDialedPhoneNumberLiveData)
//        whenever(mockViewModel.voicemailCountLiveData).thenReturn(mockVoicemailCountLiveData)
//
//        fragment = DialerFragment.newInstance()
//        startFragment(fragment, FakeDialerListenerActivity::class.java)
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
//    fun onCreateView_observesViewModelLiveDatas() {
//        verify(mockProcessedCallInfoLiveData).observe(eq(fragment), any())
//        verify(mockErrorStateLiveData).observe(eq(fragment), any())
//        verify(mockLastDialedPhoneNumberLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun clickVoicemailButton_placeVoicemailCall() {
//        val voicemailImageView: ImageView = fragment.view!!.findViewById(R.id.dialer_voicemail_image_view)
//
//        if (voicemailImageView.isClickable) {
//            voicemailImageView.performClick()
//        }
//
//        verify(mockViewModel).placeVoicemailCall()
//    }
//
//    @Test
//    fun clickVideoCallButton_callsToAnalyticsManager() {
//        val placeVideoCallImageView: ImageView = fragment.view!!.findViewById(R.id.dialer_video_call_image_view)
//
//        if (placeVideoCallImageView.isClickable) {
//            placeVideoCallImageView.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.DIALER, Enums.Analytics.EventName.VIDEO_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun clickVideoCallButton_emptyPhoneNumber_callsToViewModel() {
//        val placeVideoCallImageView: ImageView = fragment.view!!.findViewById(R.id.dialer_video_call_image_view)
//
//        if (placeVideoCallImageView.isClickable) {
//            placeVideoCallImageView.performClick()
//        }
//
//        verify(mockViewModel).placeCall("", Enums.Sip.CallTypes.VIDEO)
//    }
//
//    @Test
//    fun clickVideoCallButton_populatedPhoneNumber_callsToViewModel() {
//        val placeVideoCallImageView: ImageView = fragment.view!!.findViewById(R.id.dialer_video_call_image_view)
//
//        fragment.mPhoneNumberEditText.setText("(222) 333-4444")
//
//        if (placeVideoCallImageView.isClickable) {
//            placeVideoCallImageView.performClick()
//        }
//
//        verify(mockViewModel).placeCall("(222) 333-4444", Enums.Sip.CallTypes.VIDEO)
//    }
//
//    @Test
//    fun onFabClicked_callsToAnalyticsManager() {
//        val fab: com.nextiva.nextivaapp.android.fab.FloatingActionButton = com.nextiva.nextivaapp.android.fab.FloatingActionButton(fragment.requireContext())
//
//        fragment.onFabClicked(fab)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.DIALER, Enums.Analytics.EventName.VOICE_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onFabClicked_emptyPhoneNumber_callsToViewModel() {
//        val fab: com.nextiva.nextivaapp.android.fab.FloatingActionButton = com.nextiva.nextivaapp.android.fab.FloatingActionButton(fragment.requireContext())
//
//        fragment.onFabClicked(fab)
//
//        verify(mockViewModel).placeCall("", Enums.Sip.CallTypes.VOICE)
//    }
//
//    //TODO Move into NewCallDialerFragmentTest
//    @Test
//    fun onNewCallVoiceButtonClicked_callsToAnalyticsManager() {
//        val placeVoiceCallFab: FloatingActionButton = fragment.view!!.findViewById(R.id.dialer_voice_call_floating_action_button)
//
//        if (placeVoiceCallFab.isClickable) {
//            placeVoiceCallFab.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.DIALER, Enums.Analytics.EventName.VOICE_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onNewCallVoiceButtonClicked_emptyPhoneNumber_callsToViewModel() {
//        val placeVoiceCallFab: FloatingActionButton = fragment.view!!.findViewById(R.id.dialer_voice_call_floating_action_button)
//
//        if (placeVoiceCallFab.isClickable) {
//            placeVoiceCallFab.performClick()
//        }
//
//        verify(mockViewModel).placeCall("", Enums.Sip.CallTypes.VOICE)
//    }
//
//    @Test
//    fun onNewCallVoiceButtonClicked_populatedPhoneNumber_callsToViewModel() {
//        val placeVoiceCallFab: FloatingActionButton = fragment.view!!.findViewById(R.id.dialer_voice_call_floating_action_button)
//
//        fragment.mPhoneNumberEditText.setText("(222) 333-4444")
//
//        if (placeVoiceCallFab.isClickable) {
//            placeVoiceCallFab.performClick()
//        }
//
//        verify(mockViewModel).placeCall("(222) 333-4444", Enums.Sip.CallTypes.VOICE)
//    }
//    //TODO End Move into NewCallDialerFragmentTest
//
//    @Test
//    fun placeCall_populatedPhoneNumber_callsToViewModel() {
//        fragment.mPhoneNumberEditText.setText("(222) 333-4444")
//
//        fragment.placeCall()
//
//        verify(mockViewModel).placeCall("(222) 333-4444", Enums.Sip.CallTypes.VOICE)
//    }
//
//    @Test
//    fun clickVoiceCallButton_emptyPhoneNumber_callsToViewModel() {
//        val placeVoiceCallFloatingActionButton = fragment.view!!.findViewById<ImageButton>(R.id.dialer_voice_call_floating_action_button)
//
//        if (placeVoiceCallFloatingActionButton.isClickable) {
//            placeVoiceCallFloatingActionButton.performClick()
//        }
//
//        verify(mockViewModel).placeCall("", Enums.Sip.CallTypes.VOICE)
//    }
//
//    @Test
//    fun clickVoiceCallButton_populatedPhoneNumber_callsToViewModel() {
//        val placeVoiceCallFloatingActionButton = fragment.view!!.findViewById<ImageButton>(R.id.dialer_voice_call_floating_action_button)
//
//        fragment.mPhoneNumberEditText.setText("(222) 333-4444")
//
//        if (placeVoiceCallFloatingActionButton.isClickable) {
//            placeVoiceCallFloatingActionButton.performClick()
//        }
//
//        verify(mockViewModel).placeCall("(222) 333-4444", Enums.Sip.CallTypes.VOICE)
//    }
//
//
//    @Test
//    fun onKeyPressed_appendsKeyToPhoneNumberField() {
//        fragment.onKeyPressed("1")
//
//        assertEquals("1", fragment.mPhoneNumberEditText.text.toString())
//
//        fragment.onKeyPressed("2")
//
//        assertEquals("12", fragment.mPhoneNumberEditText.text.toString())
//
//        fragment.onKeyPressed("3")
//
//        assertEquals("123", fragment.mPhoneNumberEditText.text.toString())
//    }
//
//    @Test
//    fun onKeyPressed_selectionMidNumber_insertsKeyInPhoneNumberField() {
//        fragment.mPhoneNumberEditText.setText("222333")
//        fragment.mPhoneNumberEditText.setSelection(3)
//
//        fragment.onKeyPressed("1")
//
//        assertEquals("222-1333", fragment.mPhoneNumberEditText.text.toString())
//
//        fragment.onKeyPressed("2")
//
//        assertEquals("(222) 123-33", fragment.mPhoneNumberEditText.text.toString())
//
//        fragment.onKeyPressed("3")
//
//        assertEquals("(222) 123-333", fragment.mPhoneNumberEditText.text.toString())
//    }
//
//    @Test
//    fun onVoiceMailPressed_callsToViewModel() {
//        fragment.onVoiceMailPressed()
//
//        verify(mockViewModel).placeVoicemailCall()
//    }
//
//    @Test
//    fun pullCall_clickMenuItem_callsToViewModel() {
//        val menuItem = RoboMenuItem(R.id.dialer_pull_call)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(mockViewModel).pullCall()
//    }
//
//    @Test
//    fun pullCall_clickMenuItem_callsToAnalyticsManager() {
//        val menuItem = RoboMenuItem(R.id.dialer_pull_call)
//
//        assertTrue(fragment.onOptionsItemSelected(menuItem))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.DIALER, Enums.Analytics.EventName.PULL_CALL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun processedCallInfoObserverValueUpdated_successValue_callsToFragmentListener() {
//        val argumentCaptor: KArgumentCaptor<Observer<CallInfo>> = argumentCaptor()
//        verify(mockProcessedCallInfoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        val callInfo: CallInfo = CallInfo.Builder().build()
//
//        argumentCaptor.firstValue.onChanged(callInfo)
//
//        verify(mockFragmentListener).onProcessCall(callInfo)
//    }
//
//    @Test
//    fun processedCallInfoObserverValueUpdated_successValue_callsToViewModel() {
//        val argumentCaptor: KArgumentCaptor<Observer<CallInfo>> = argumentCaptor()
//        verify(mockProcessedCallInfoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        val callInfo: CallInfo = CallInfo.Builder().build()
//
//        argumentCaptor.firstValue.onChanged(callInfo)
//
//        verify(mockViewModel).clearProcessedCallInfo()
//    }
//
//    @Test
//    fun processedCallInfoObserverValueUpdated_successValue_clearsPhoneNumberText() {
//        val argumentCaptor: KArgumentCaptor<Observer<CallInfo>> = argumentCaptor()
//
//        fragment.mPhoneNumberEditText.setText("222-222-2222")
//
//        verify(mockProcessedCallInfoLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        val callInfo: CallInfo = CallInfo.Builder().build()
//
//        argumentCaptor.firstValue.onChanged(callInfo)
//
//        assertEquals("", fragment.mPhoneNumberEditText.text.toString())
//    }
//
//    @Test
//    fun errorStateObserverValueUpdated_successValue_callsToDialogManager() {
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockErrorStateLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(true)
//
//        verify(dialogManager).showErrorDialog(eq(fragment.activity!!), eq(Enums.Analytics.ScreenName.DIALER), any())
//    }
//
//    @Test
//    fun errorStateObserverValueUpdated_successValueDismissDialog_callsToViewModel() {
//        val errorStateArgumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//        verify(mockErrorStateLiveData).observe(eq(fragment), errorStateArgumentCaptor.capture())
//
//        errorStateArgumentCaptor.firstValue.onChanged(true)
//
//        val buttonCallbackArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//        verify(dialogManager).showErrorDialog(eq(fragment.activity!!), eq(Enums.Analytics.ScreenName.DIALER), buttonCallbackArgumentCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//
//        buttonCallbackArgumentCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//        verify(mockViewModel).clearErrorState()
//    }
//
//    @Test
//    fun lastDialedPhoneNumberObserverValueUpdated_successValue_populatesPhoneNumberField() {
//        val argumentCaptor: KArgumentCaptor<Observer<String>> = argumentCaptor()
//        verify(mockLastDialedPhoneNumberLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged("3334445555")
//
//        assertEquals("(333) 444-5555", fragment.mPhoneNumberEditText.text.toString())
//    }
//
//    @Test
//    fun lastDialedPhoneNumberObserverValueUpdated_successValue_gainsFocus() {
//        val argumentCaptor: KArgumentCaptor<Observer<String>> = argumentCaptor()
//        verify(mockLastDialedPhoneNumberLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged("3334445555")
//
//        assertTrue(fragment.mPhoneNumberEditText.hasFocus())
//    }
//
//    @Test
//    fun lastDialedPhoneNumberObserverValueUpdated_successValue_showsCursor() {
//        val argumentCaptor: KArgumentCaptor<Observer<String>> = argumentCaptor()
//        verify(mockLastDialedPhoneNumberLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged("3334445555")
//
//        assertTrue(fragment.mPhoneNumberEditText.isCursorVisible)
//    }
//}
