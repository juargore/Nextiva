package com.nextiva.nextivaapp.android.fragments
//
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeDevicePhoneNumberFragmentListenerActivity
//import com.nextiva.nextivaapp.android.viewmodels.DevicePhoneNumberViewModel
//import com.nhaarman.mockito_kotlin.mock
//import com.nhaarman.mockito_kotlin.verify
//import com.nhaarman.mockito_kotlin.whenever
//import org.junit.Assert.*
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import javax.inject.Inject
//
//class DevicePhoneNumberFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var fragment: DevicePhoneNumberFragment
//
//    private val mockFragmentListener: DevicePhoneNumberFragment.DevicePhoneNumberFragmentListener = mock()
//    private val mockViewModel: DevicePhoneNumberViewModel = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(DevicePhoneNumberViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.getPhoneNumber()).thenReturn("5554443333")
//
//        fragment = DevicePhoneNumberFragment.newInstance()
//        SupportFragmentTestUtil.startFragment(fragment, FakeDevicePhoneNumberFragmentListenerActivity::class.java)
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
//    fun onCreate_setsDefaultPhoneNumber() {
//        assertEquals("(555) 444-3333", fragment.mPhoneNumberEditText.text.toString())
//    }
//
//    @Test
//    fun onPhoneNumberTextChanged_formatsCorrectly() {
//        fragment.mPhoneNumberEditText.text = null
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        assertEquals("(222) 333-4444", fragment.mPhoneNumberEditText.text.toString())
//
//        fragment.mPhoneNumberEditText.text = null
//        fragment.mPhoneNumberEditText.setText("12223334444")
//
//        assertEquals("1 222-333-4444", fragment.mPhoneNumberEditText.text.toString())
//    }
//
//    @Test
//    fun onPhoneNumberTextChanged_enablesContinueButton() {
//        fragment.mPhoneNumberEditText.text = null
//
//        assertFalse(fragment.mContinueButton.isEnabled)
//
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        assertTrue(fragment.mContinueButton.isEnabled)
//    }
//
//    @Test
//    fun onContinueButtonClicked_callsToAnalyticsManager() {
//        fragment.mPhoneNumberEditText.text = null
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        if (fragment.mContinueButton.isClickable) {
//            fragment.mContinueButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ONBOARDING_THIS_PHONE_NUMBER, Enums.Analytics.EventName.CONTINUE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onContinueButtonClicked_callsToSettingsManagerToSetPhoneNumberValue() {
//        fragment.mPhoneNumberEditText.text = null
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        if (fragment.mContinueButton.isClickable) {
//            fragment.mContinueButton.performClick()
//        }
//
//        verify(mockViewModel).setPhoneNumber("(222) 333-4444")
//    }
//
//    @Test
//    fun onContinueButtonClicked_callsToFragmentListenerToIndicateNumberWasSaved() {
//        if (fragment.mContinueButton.isClickable) {
//            fragment.mContinueButton.performClick()
//        }
//
//        verify(mockFragmentListener).onPhoneNumberSaved()
//    }
//}