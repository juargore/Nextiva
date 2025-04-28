package com.nextiva.nextivaapp.android.fragments.setcallsettings

//import android.content.Intent
//import android.text.InputType
//import android.view.inputmethod.EditorInfo
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.constants.Constants
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeSetCallSettingsActivity
//import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
//import com.nextiva.nextivaapp.android.models.Resource
//import com.nextiva.nextivaapp.android.models.ServiceSettings
//import com.nextiva.nextivaapp.android.models.SingleEvent
//import com.nextiva.nextivaapp.android.net.buses.RxEvents
//import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereLocationViewModel
//import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereLocationViewModel.ValidationEvent
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.*
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment
//import javax.inject.Inject
//
///**
// * This test will test all standard features of the SetCallSettingsNextivaAnywhereLocationFragment
// * as well as any Add Nextiva Anywhere Location specific features
// */
//class SetCallSettingsNextivaAnywhereLocationFragmentAddLocationTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var dialogManager: DialogManager
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var fragment: SetCallSettingsNextivaAnywhereLocationFragment
//    private lateinit var nextivaAnywhereServiceSettings: ServiceSettings
//
//    private val mockViewModel: SetCallSettingsNextivaAnywhereLocationViewModel = mock()
//    private val mockEditingLocationLiveData: LiveData<Resource<NextivaAnywhereLocation>> = mock()
//    private val mockSaveLocationValidationEventLiveData: LiveData<SingleEvent<ValidationEvent>> = mock()
//    private val mockSaveLocationLiveData: LiveData<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>> = mock()
//    private val mockDeleteLocationValidationEventLiveData: LiveData<SingleEvent<ValidationEvent>> = mock()
//    private val mockDeleteLocationLiveData: LiveData<Resource<NextivaAnywhereLocation>> = mock()
//    private val mockSetCallSettingsListener: SetCallSettingsListener = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(SetCallSettingsNextivaAnywhereLocationViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.editingLocationLiveData).thenReturn(mockEditingLocationLiveData)
//        whenever(mockViewModel.saveLocationValidationEventLiveData).thenReturn(mockSaveLocationValidationEventLiveData)
//        whenever(mockViewModel.saveLocationLiveData).thenReturn(mockSaveLocationLiveData)
//        whenever(mockViewModel.deleteLocationValidationEventLiveData).thenReturn(mockDeleteLocationValidationEventLiveData)
//        whenever(mockViewModel.deleteLocationLiveData).thenReturn(mockDeleteLocationLiveData)
//
//        nextivaAnywhereServiceSettings = ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "", null, null, null, null, null, true, false, arrayListOf(NextivaAnywhereLocation("8887776666", "My Phone", false, false, false, false)), null, null)
//
//        fragment = SetCallSettingsNextivaAnywhereLocationFragment.newInstance(nextivaAnywhereServiceSettings, null)
//        startFragment(fragment, FakeSetCallSettingsActivity::class.java)
//        fragment.mSetCallSettingsListener = mockSetCallSettingsListener
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
//    fun newInstance_setsCorrectExtras() {
//        val fragment = SetCallSettingsNextivaAnywhereLocationFragment.newInstance(nextivaAnywhereServiceSettings, null)
//
//        assertEquals(nextivaAnywhereServiceSettings, fragment.arguments!!.getSerializable("PARAMS_NEXTIVA_ANYWHERE_SERVICE_SETTINGS"))
//        assertEquals(Enums.CallSettings.FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION, fragment.arguments!!.getString("PARAMS_CALL_SETTINGS_TYPE"))
//        assertNull(fragment.arguments!!.getSerializable("PARAMS_CALL_SETTINGS_VALUE"))
//    }
//
//    @Test
//    fun onCreateView_observesViewModelLiveDatas() {
//        verify(mockEditingLocationLiveData).observe(eq(fragment), any())
//        verify(mockSaveLocationValidationEventLiveData).observe(eq(fragment), any())
//        verify(mockSaveLocationLiveData).observe(eq(fragment), any())
//        verify(mockDeleteLocationValidationEventLiveData).observe(eq(fragment), any())
//        verify(mockDeleteLocationLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun onCreateView_setsNextivaAnywhereServiceSettings() {
//        verify(mockViewModel).setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
//    }
//
//    @Test
//    fun onResume_trackScreenViewAnalytics() {
//        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION)
//    }
//
//    @Test
//    fun verifyUI() {
//        assertEquals("Phone Number", fragment.mPhoneNumberTextInputLayout.hint)
//        assertEquals("Description", fragment.mDescriptionLayout.hint)
//        assertEquals("Enable This Location", fragment.mEnableThisLocationCheckBox.text)
//        assertEquals("Call Control", fragment.mCallControlCheckBox.text)
//        assertEquals("Prevent Diverting Calls", fragment.mPreventDivertingCallsCheckBox.text)
//        assertEquals("Answer Confirmation", fragment.mAnswerConfirmationCheckBox.text)
//
//        assertEquals(EditorInfo.IME_ACTION_NEXT, fragment.mPhoneNumberEditText.imeOptions)
//        assertEquals(EditorInfo.IME_ACTION_DONE, fragment.mDescriptionEditText.imeOptions)
//
//        assertEquals(InputType.TYPE_CLASS_PHONE or InputType.TYPE_TEXT_FLAG_MULTI_LINE, fragment.mPhoneNumberEditText.inputType)
//        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_TEXT_FLAG_MULTI_LINE, fragment.mDescriptionEditText.inputType)
//    }
//
//    @Test
//    fun getAnalyticsScreenName_returnsCorrectAnalyticsScreenName() {
//        assertEquals("add_nextiva_anywhere_location_screen", fragment.analyticScreenName)
//    }
//
//    @Test
//    fun phoneNumberFieldChanged_filledIn_enablesForm() {
//        assertFalse(fragment.mDescriptionLayout.isEnabled)
//        assertFalse(fragment.mEnableThisLocationCheckBox.isEnabled)
//        assertFalse(fragment.mCallControlCheckBox.isEnabled)
//        assertFalse(fragment.mPreventDivertingCallsCheckBox.isEnabled)
//        assertFalse(fragment.mAnswerConfirmationCheckBox.isEnabled)
//
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        assertTrue(fragment.mDescriptionLayout.isEnabled)
//        assertTrue(fragment.mEnableThisLocationCheckBox.isEnabled)
//        assertTrue(fragment.mCallControlCheckBox.isEnabled)
//        assertTrue(fragment.mPreventDivertingCallsCheckBox.isEnabled)
//        assertTrue(fragment.mAnswerConfirmationCheckBox.isEnabled)
//    }
//
//    @Test
//    fun phoneNumberFieldChanged_empty_disablesForm() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        assertTrue(fragment.mDescriptionLayout.isEnabled)
//        assertTrue(fragment.mEnableThisLocationCheckBox.isEnabled)
//        assertTrue(fragment.mCallControlCheckBox.isEnabled)
//        assertTrue(fragment.mPreventDivertingCallsCheckBox.isEnabled)
//        assertTrue(fragment.mAnswerConfirmationCheckBox.isEnabled)
//
//        fragment.mPhoneNumberEditText.setText("")
//
//        assertFalse(fragment.mDescriptionLayout.isEnabled)
//        assertFalse(fragment.mEnableThisLocationCheckBox.isEnabled)
//        assertFalse(fragment.mCallControlCheckBox.isEnabled)
//        assertFalse(fragment.mPreventDivertingCallsCheckBox.isEnabled)
//        assertFalse(fragment.mAnswerConfirmationCheckBox.isEnabled)
//    }
//
//    @Test
//    fun phoneNumberFieldChanged_cleared_unChecksEnableThisLocation() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//        fragment.mPhoneNumberEditText.setText("")
//
//        assertFalse(fragment.mEnableThisLocationCheckBox.isChecked)
//    }
//
//    @Test
//    fun phoneNumberFieldChanged_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        // Two times because this was changed initially and then again when the phone number formatter kicks in
//        verify(mockSetCallSettingsListener, times(2)).onFormUpdated()
//    }
//
//    @Test
//    fun phoneNumberFieldChanged_formatsNumber() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        assertEquals("(222) 333-4444", fragment.mPhoneNumberEditText.text.toString())
//    }
//
//    @Test
//    fun descriptionFieldChanged_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        fragment.mDescriptionEditText.setText("Description")
//
//        verify(mockSetCallSettingsListener).onFormUpdated()
//    }
//
//    @Test
//    fun enableCheckBoxChanged_checked_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//
//        verify(mockSetCallSettingsListener).onFormUpdated()
//    }
//
//    @Test
//    fun enableCheckBoxChanged_checked_callsToAnalyticsManager() {
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_CHECKED)
//    }
//
//    @Test
//    fun enableCheckBoxChanged_unchecked_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        // Set the initial value as checked so unchecking fires the checked change listener
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//        fragment.mEnableThisLocationCheckBox.isChecked = false
//
//        // Two times because it would have fired when we initially checked the check box
//        verify(mockSetCallSettingsListener, times(2)).onFormUpdated()
//    }
//
//    @Test
//    fun enableCheckBoxChanged_unchecked_callsToAnalyticsManager() {
//        // Set the initial value as checked so unchecking fires the checked change listener
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//        fragment.mEnableThisLocationCheckBox.isChecked = false
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_UNCHECKED)
//    }
//
//    @Test
//    fun callControlCheckBoxChanged_checked_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        fragment.mCallControlCheckBox.isChecked = true
//
//        verify(mockSetCallSettingsListener).onFormUpdated()
//    }
//
//    @Test
//    fun callControlCheckBoxChanged_checked_callsToAnalyticsManager() {
//        fragment.mCallControlCheckBox.isChecked = true
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_CHECKED)
//    }
//
//    @Test
//    fun callControlCheckBoxChanged_unchecked_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        // Set the initial value as checked so unchecking fires the checked change listener
//        fragment.mCallControlCheckBox.isChecked = true
//        fragment.mCallControlCheckBox.isChecked = false
//
//        // Two times because it would have fired when we initially checked the check box
//        verify(mockSetCallSettingsListener, times(2)).onFormUpdated()
//    }
//
//    @Test
//    fun callControlCheckBoxChanged_unchecked_callsToAnalyticsManager() {
//        // Set the initial value as checked so unchecking fires the checked change listener
//        fragment.mCallControlCheckBox.isChecked = true
//        fragment.mCallControlCheckBox.isChecked = false
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_UNCHECKED)
//    }
//
//    @Test
//    fun preventDivertingCallsCheckBoxChanged_checked_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        fragment.mPreventDivertingCallsCheckBox.isChecked = true
//
//        verify(mockSetCallSettingsListener).onFormUpdated()
//    }
//
//    @Test
//    fun preventDivertingCallsCheckBoxChanged_checked_callsToAnalyticsManager() {
//        fragment.mPreventDivertingCallsCheckBox.isChecked = true
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_CHECKED)
//    }
//
//    @Test
//    fun preventDivertingCallsCheckBoxChanged_unchecked_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        // Set the initial value as checked so unchecking fires the checked change listener
//        fragment.mPreventDivertingCallsCheckBox.isChecked = true
//        fragment.mPreventDivertingCallsCheckBox.isChecked = false
//
//        // Two times because it would have fired when we initially checked the check box
//        verify(mockSetCallSettingsListener, times(2)).onFormUpdated()
//    }
//
//    @Test
//    fun preventDivertingCallsCheckBoxChanged_unchecked_callsToAnalyticsManager() {
//        // Set the initial value as checked so unchecking fires the checked change listener
//        fragment.mPreventDivertingCallsCheckBox.isChecked = true
//        fragment.mPreventDivertingCallsCheckBox.isChecked = false
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED)
//    }
//
//    @Test
//    fun answerConfirmationCheckBoxChanged_checked_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        fragment.mAnswerConfirmationCheckBox.isChecked = true
//
//        verify(mockSetCallSettingsListener).onFormUpdated()
//    }
//
//    @Test
//    fun answerConfirmationCheckBoxChanged_checked_callsToAnalyticsManager() {
//        fragment.mAnswerConfirmationCheckBox.isChecked = true
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_CHECKED)
//    }
//
//    @Test
//    fun answerConfirmationCheckBoxChanged_unchecked_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        // Set the initial value as checked so unchecking fires the checked change listener
//        fragment.mAnswerConfirmationCheckBox.isChecked = true
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        // Two times because it would have fired when we initially checked the check box
//        verify(mockSetCallSettingsListener, times(2)).onFormUpdated()
//    }
//
//    @Test
//    fun answerConfirmationCheckBoxChanged_unchecked_callsToAnalyticsManager() {
//        // Set the initial value as checked so unchecking fires the checked change listener
//        fragment.mAnswerConfirmationCheckBox.isChecked = true
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_UNCHECKED)
//    }
//
//    @Test
//    fun getLayoutResId_returnsCorrectLayoutId() {
//        assertEquals(R.layout.fragment_set_call_settings_nextiva_anywhere_location, fragment.layoutResId)
//    }
//
//    @Test
//    fun getToolbarTitleStringResId_returnsCorrectStringId() {
//        assertEquals(R.string.set_call_settings_nextiva_anywhere_add_location_toolbar, fragment.toolbarTitleStringResId)
//    }
//
//    @Test
//    fun setupScreenWidgets_callsToViewModelToFetchNextivaAnywhereLocation() {
//        reset(mockViewModel)
//
//        fragment.setupScreenWidgets()
//
//        verify(mockViewModel).fetchNextivaAnywhereLocation()
//    }
//
//    @Test
//    fun changesMade_callsToViewModelToCheckChanges() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//        fragment.mDescriptionEditText.setText("Description")
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//        fragment.mCallControlCheckBox.isChecked = false
//        fragment.mPreventDivertingCallsCheckBox.isChecked = true
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        fragment.changesMade()
//
//        verify(mockViewModel).changesMade("(222) 333-4444", "Description", true, false, true, false)
//    }
//
//    @Test
//    fun enableSaveButton_changesMadeAndFilledInPhoneNumber_returnsTrue() {
//        whenever(mockViewModel.changesMade("(222) 333-4444", "", false, false, false, false)).thenReturn(true)
//
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        assertTrue(fragment.enableSaveButton())
//    }
//
//    @Test
//    fun enableSaveButton_noChangesMadeAndFilledInPhoneNumber_returnsFalse() {
//        whenever(mockViewModel.changesMade("(222) 333-4444", "", false, false, false, false)).thenReturn(false)
//
//        fragment.mPhoneNumberEditText.setText("2223334444")
//
//        assertFalse(fragment.enableSaveButton())
//    }
//
//    @Test
//    fun enableSaveButton_emptyPhoneNumber_returnsFalse() {
//        assertFalse(fragment.enableSaveButton())
//    }
//
//    @Test
//    fun enableDeleteButton_returnsFalse() {
//        assertFalse(fragment.enableDeleteButton())
//    }
//
//    @Test
//    fun getFormCallSettings_returnsCreatedNextivaAnywhereLocation() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//        fragment.mDescriptionEditText.setText("Description")
//        fragment.mEnableThisLocationCheckBox.isChecked = false
//        fragment.mCallControlCheckBox.isChecked = false
//        fragment.mPreventDivertingCallsCheckBox.isChecked = false
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        var nextivaAnywhereLocation = fragment.formCallSettings
//
//        assertEquals("(222) 333-4444", nextivaAnywhereLocation.phoneNumber)
//        assertEquals("Description", nextivaAnywhereLocation.description)
//        assertFalse(nextivaAnywhereLocation.active)
//        assertFalse(nextivaAnywhereLocation.callControlEnabled)
//        assertFalse(nextivaAnywhereLocation.preventDivertingCalls)
//        assertFalse(nextivaAnywhereLocation.answerConfirmationRequired)
//
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//        fragment.mCallControlCheckBox.isChecked = false
//        fragment.mPreventDivertingCallsCheckBox.isChecked = false
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        nextivaAnywhereLocation = fragment.formCallSettings
//
//        assertTrue(nextivaAnywhereLocation.active)
//        assertFalse(nextivaAnywhereLocation.callControlEnabled)
//        assertFalse(nextivaAnywhereLocation.preventDivertingCalls)
//        assertFalse(nextivaAnywhereLocation.answerConfirmationRequired)
//
//        fragment.mEnableThisLocationCheckBox.isChecked = false
//        fragment.mCallControlCheckBox.isChecked = true
//        fragment.mPreventDivertingCallsCheckBox.isChecked = false
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        nextivaAnywhereLocation = fragment.formCallSettings
//
//        assertFalse(nextivaAnywhereLocation.active)
//        assertTrue(nextivaAnywhereLocation.callControlEnabled)
//        assertFalse(nextivaAnywhereLocation.preventDivertingCalls)
//        assertFalse(nextivaAnywhereLocation.answerConfirmationRequired)
//
//        fragment.mEnableThisLocationCheckBox.isChecked = false
//        fragment.mCallControlCheckBox.isChecked = false
//        fragment.mPreventDivertingCallsCheckBox.isChecked = true
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        nextivaAnywhereLocation = fragment.formCallSettings
//
//        assertFalse(nextivaAnywhereLocation.active)
//        assertFalse(nextivaAnywhereLocation.callControlEnabled)
//        assertTrue(nextivaAnywhereLocation.preventDivertingCalls)
//        assertFalse(nextivaAnywhereLocation.answerConfirmationRequired)
//
//        fragment.mEnableThisLocationCheckBox.isChecked = false
//        fragment.mCallControlCheckBox.isChecked = false
//        fragment.mPreventDivertingCallsCheckBox.isChecked = false
//        fragment.mAnswerConfirmationCheckBox.isChecked = true
//
//        nextivaAnywhereLocation = fragment.formCallSettings
//
//        assertFalse(nextivaAnywhereLocation.active)
//        assertFalse(nextivaAnywhereLocation.callControlEnabled)
//        assertFalse(nextivaAnywhereLocation.preventDivertingCalls)
//        assertTrue(nextivaAnywhereLocation.answerConfirmationRequired)
//    }
//
//    @Test
//    fun getFormCallSettings_emptyDescription_setsDescriptionNull() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//        fragment.mDescriptionEditText.setText("")
//        fragment.mEnableThisLocationCheckBox.isChecked = false
//        fragment.mCallControlCheckBox.isChecked = false
//        fragment.mPreventDivertingCallsCheckBox.isChecked = false
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        var nextivaAnywhereLocation = fragment.formCallSettings
//
//        assertEquals("(222) 333-4444", nextivaAnywhereLocation.phoneNumber)
//        assertNull(nextivaAnywhereLocation.description)
//        assertFalse(nextivaAnywhereLocation.active)
//        assertFalse(nextivaAnywhereLocation.callControlEnabled)
//        assertFalse(nextivaAnywhereLocation.preventDivertingCalls)
//        assertFalse(nextivaAnywhereLocation.answerConfirmationRequired)
//    }
//
//    @Test
//    fun saveForm_callsToViewModelToValidateSave() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//        fragment.mDescriptionEditText.setText("Description")
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//        fragment.mCallControlCheckBox.isChecked = false
//        fragment.mPreventDivertingCallsCheckBox.isChecked = true
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        fragment.saveForm()
//
//        val argumentCaptor: KArgumentCaptor<NextivaAnywhereLocation> = argumentCaptor()
//
//        verify(mockViewModel).validateSaveNextivaAnywhereLocation(argumentCaptor.capture())
//        assertEquals("(222) 333-4444", argumentCaptor.firstValue.phoneNumber)
//        assertEquals("Description", argumentCaptor.firstValue.description)
//        assertTrue(argumentCaptor.firstValue.active)
//        assertFalse(argumentCaptor.firstValue.callControlEnabled)
//        assertTrue(argumentCaptor.firstValue.preventDivertingCalls)
//        assertFalse(argumentCaptor.firstValue.answerConfirmationRequired)
//    }
//
//    @Test
//    fun saveForm_callsToAnalyticsManager() {
//        fragment.mPhoneNumberEditText.setText("2223334444")
//        fragment.mDescriptionEditText.setText("Description")
//        fragment.mEnableThisLocationCheckBox.isChecked = true
//        fragment.mCallControlCheckBox.isChecked = false
//        fragment.mPreventDivertingCallsCheckBox.isChecked = true
//        fragment.mAnswerConfirmationCheckBox.isChecked = false
//
//        fragment.saveForm()
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.SAVE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallBack_callsToAnalyticsManager() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, true, false)))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallBack_callsToDialogManagerToShowDialog() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, true, false)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.dialing_service_conflict_call_back_title),
//                eq(R.string.dialing_service_conflict_call_back_message),
//                eq(R.string.general_yes),
//                buttonCaptor.capture(),
//                eq(R.string.general_no),
//                any())
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallBackClickPositiveButton_callsToAnalyticsManager() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, true, false)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.dialing_service_conflict_call_back_title),
//                eq(R.string.dialing_service_conflict_call_back_message),
//                eq(R.string.general_yes),
//                buttonCaptor.capture(),
//                eq(R.string.general_no),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_YES_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallBackClickPositiveButton_callsToViewModelToSaveLocation() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, true, false)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.dialing_service_conflict_call_back_title),
//                eq(R.string.dialing_service_conflict_call_back_message),
//                eq(R.string.general_yes),
//                buttonCaptor.capture(),
//                eq(R.string.general_no),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(mockViewModel).saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocation)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallBackClickNegativeButton_callsToAnalyticsManager() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, true, false)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.dialing_service_conflict_call_back_title),
//                eq(R.string.dialing_service_conflict_call_back_message),
//                eq(R.string.general_yes),
//                any(),
//                eq(R.string.general_no),
//                buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_NO_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallThrough_callsToAnalyticsManager() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, false, true)))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallThrough_callsToDialogManagerToShowDialog() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, false, true)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.dialing_service_conflict_call_through_title),
//                eq(R.string.dialing_service_conflict_call_through_message),
//                eq(R.string.general_yes),
//                buttonCaptor.capture(),
//                eq(R.string.general_no),
//                any())
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallThroughClickPositiveButton_callsToAnalyticsManager() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, false, true)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.dialing_service_conflict_call_through_title),
//                eq(R.string.dialing_service_conflict_call_through_message),
//                eq(R.string.general_yes),
//                buttonCaptor.capture(),
//                eq(R.string.general_no),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_YES_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallThroughClickPositiveButton_callsToViewModelToSaveLocation() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, false, true)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.dialing_service_conflict_call_through_title),
//                eq(R.string.dialing_service_conflict_call_through_message),
//                eq(R.string.general_yes),
//                buttonCaptor.capture(),
//                eq(R.string.general_no),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(mockViewModel).saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocation)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_conflictWithCallThroughClickNegativeButton_callsToAnalyticsManager() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, false, true)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(R.string.dialing_service_conflict_call_through_title),
//                eq(R.string.dialing_service_conflict_call_through_message),
//                eq(R.string.general_yes),
//                any(),
//                eq(R.string.general_no),
//                buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_NO_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun saveLocationValidationEventObserverValueUpdated_noConflict_callsToViewModelToSaveLocation() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//
//        verify(mockSaveLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, newLocation, false, false)))
//
//        verify(mockViewModel).saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocation)
//    }
//
//    @Test
//    fun saveLocationObserverValueUpdated_loadingValue_callsToDialogManagerToShowProgressDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>>> = argumentCaptor()
//
//        verify(mockSaveLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.loading(null))
//
//        verify(dialogManager).showProgressDialog(fragment.activity!!, Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION, R.string.progress_processing)
//    }
//
//    @Test
//    fun saveLocationObserverValueUpdated_successValue_callsToDialogManagerToDismissDialog() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>>> = argumentCaptor()
//
//        verify(mockSaveLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(RxEvents.NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, newLocation, null)))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun saveLocationObserverValueUpdated_successValue_callsToSetCallSettingsListenerToIndicateFormSaved() {
//        val newLocation = NextivaAnywhereLocation("2223334444", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>>> = argumentCaptor()
//        val callSettingsSavedCaptor: KArgumentCaptor<Intent> = argumentCaptor()
//
//        verify(mockSaveLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(RxEvents.NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, newLocation, null)))
//
//        verify(mockSetCallSettingsListener).onCallSettingsSaved(callSettingsSavedCaptor.capture())
//
//        assertEquals(Enums.CallSettings.FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION, callSettingsSavedCaptor.firstValue.getStringExtra(Constants.EXTRA_CALL_SETTINGS_KEY))
//        assertEquals(newLocation, callSettingsSavedCaptor.firstValue.getSerializableExtra(Constants.EXTRA_CALL_SETTINGS_VALUE))
//        assertNull(callSettingsSavedCaptor.firstValue.getStringExtra(Constants.EXTRA_OLD_PHONE_NUMBER))
//        assertEquals(Constants.ACTION_TYPE_SAVED, callSettingsSavedCaptor.firstValue.getStringExtra(Constants.EXTRA_ACTION))
//    }
//
//    @Test
//    fun saveLocationObserverValueUpdated_errorValue_callsToDialogManagerToDismissDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>>> = argumentCaptor()
//
//        verify(mockSaveLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.error("", null))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun saveLocationObserverValueUpdated_errorValue_callsToDialogManagerToShowErrorDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>>> = argumentCaptor()
//
//        verify(mockSaveLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.error("", null))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.ADD_NEXTIVA_ANYWHERE_LOCATION)
//    }
//}