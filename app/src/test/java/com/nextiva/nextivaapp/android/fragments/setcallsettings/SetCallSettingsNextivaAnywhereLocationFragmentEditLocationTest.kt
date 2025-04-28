package com.nextiva.nextivaapp.android.fragments.setcallsettings
//
//import android.content.Intent
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
// * This test will only test the Edit Nextiva Anywhere Location specific features
// */
//class SetCallSettingsNextivaAnywhereLocationFragmentEditLocationTest : BaseRobolectricTest() {
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
//    private lateinit var editingLocation: NextivaAnywhereLocation
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
//        nextivaAnywhereServiceSettings = ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "", null, null, null, null, null, true, false, arrayListOf(NextivaAnywhereLocation("8887776666", "My Phone", false, false, false, false)), null, null)
//        editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
//
//        whenever(viewModelFactory.create(SetCallSettingsNextivaAnywhereLocationViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.editingLocationLiveData).thenReturn(mockEditingLocationLiveData)
//        whenever(mockViewModel.saveLocationValidationEventLiveData).thenReturn(mockSaveLocationValidationEventLiveData)
//        whenever(mockViewModel.saveLocationLiveData).thenReturn(mockSaveLocationLiveData)
//        whenever(mockViewModel.deleteLocationValidationEventLiveData).thenReturn(mockDeleteLocationValidationEventLiveData)
//        whenever(mockViewModel.deleteLocationLiveData).thenReturn(mockDeleteLocationLiveData)
//        whenever(mockViewModel.editingLocation).thenReturn(editingLocation)
//
//        fragment = SetCallSettingsNextivaAnywhereLocationFragment.newInstance(nextivaAnywhereServiceSettings, editingLocation)
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
//        val fragment = SetCallSettingsNextivaAnywhereLocationFragment.newInstance(nextivaAnywhereServiceSettings, editingLocation)
//
//        assertEquals(nextivaAnywhereServiceSettings, fragment.arguments!!.getSerializable("PARAMS_NEXTIVA_ANYWHERE_SERVICE_SETTINGS"))
//        assertEquals(Enums.CallSettings.FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION, fragment.arguments!!.getString("PARAMS_CALL_SETTINGS_TYPE"))
//        assertEquals(editingLocation, fragment.arguments!!.getSerializable("PARAMS_CALL_SETTINGS_VALUE"))
//    }
//
//    @Test
//    fun onCreateView_setsEditingLocation() {
//        verify(mockViewModel).editingLocation = editingLocation
//    }
//
//    @Test
//    fun onResume_trackScreenViewAnalytics() {
//        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION)
//    }
//
//    @Test
//    fun getAnalyticsScreenName_returnsCorrectAnalyticsScreenName() {
//        assertEquals("edit_nextiva_anywhere_location_screen", fragment.analyticScreenName)
//    }
//
//    @Test
//    fun getToolbarTitleStringResId_returnsCorrectStringId() {
//        assertEquals(R.string.set_call_settings_nextiva_anywhere_edit_location_toolbar, fragment.toolbarTitleStringResId)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_CHECKED)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_UNCHECKED)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_CHECKED)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_UNCHECKED)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_CHECKED)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_CHECKED)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_UNCHECKED)
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.SAVE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun enableDeleteButton_returnsTrue() {
//        assertTrue(fragment.enableDeleteButton())
//    }
//
//    @Test
//    fun deleteForm_callsToViewModelToValidateDelete() {
//        fragment.deleteForm()
//
//        verify(mockViewModel).validateDeleteNextivaAnywhereLocation()
//    }
//
//    @Test
//    fun deleteForm_callsToAnalyticsManager() {
//        fragment.deleteForm()
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.DELETE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_loadingValue_callsToDialogManagerToShowProgressDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.loading(null))
//
//        verify(dialogManager).showProgressDialog(fragment.activity!!, Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, R.string.progress_processing)
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_successValue_callsToDialogManagerToDismissDialog() {
//        val newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_successValue_setsEditingLocationFormValues() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        var newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", false, false, false, false)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        assertEquals("(444) 333-2222", fragment.mPhoneNumberEditText.text.toString())
//        assertEquals("Home Phone", fragment.mDescriptionEditText.text.toString())
//        assertFalse(fragment.mEnableThisLocationCheckBox.isChecked)
//        assertFalse(fragment.mCallControlCheckBox.isChecked)
//        assertFalse(fragment.mPreventDivertingCallsCheckBox.isChecked)
//        assertFalse(fragment.mAnswerConfirmationCheckBox.isChecked)
//
//        newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, false, false, false)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        assertTrue(fragment.mEnableThisLocationCheckBox.isChecked)
//        assertFalse(fragment.mCallControlCheckBox.isChecked)
//        assertFalse(fragment.mPreventDivertingCallsCheckBox.isChecked)
//        assertFalse(fragment.mAnswerConfirmationCheckBox.isChecked)
//
//        newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", false, true, false, false)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        assertFalse(fragment.mEnableThisLocationCheckBox.isChecked)
//        assertTrue(fragment.mCallControlCheckBox.isChecked)
//        assertFalse(fragment.mPreventDivertingCallsCheckBox.isChecked)
//        assertFalse(fragment.mAnswerConfirmationCheckBox.isChecked)
//
//        newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", false, false, true, false)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        assertFalse(fragment.mEnableThisLocationCheckBox.isChecked)
//        assertFalse(fragment.mCallControlCheckBox.isChecked)
//        assertTrue(fragment.mPreventDivertingCallsCheckBox.isChecked)
//        assertFalse(fragment.mAnswerConfirmationCheckBox.isChecked)
//
//        newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", false, false, false, true)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        assertFalse(fragment.mEnableThisLocationCheckBox.isChecked)
//        assertFalse(fragment.mCallControlCheckBox.isChecked)
//        assertFalse(fragment.mPreventDivertingCallsCheckBox.isChecked)
//        assertTrue(fragment.mAnswerConfirmationCheckBox.isChecked)
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_successValue_doesNotCallToAnalyticsManager() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        var newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", false, false, false, false)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_UNCHECKED)
//
//        newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, false, false, false)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_CHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_UNCHECKED)
//
//        newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", false, true, false, false)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_CHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_UNCHECKED)
//
//        newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", false, false, true, false)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_CHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_UNCHECKED)
//
//        newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", false, false, false, true)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ENABLED_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_CONTROL_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.PREVENT_DIVERTING_CALLS_SWITCH_UNCHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.ANSWER_CONFIRMATION_SWITCH_CHECKED)
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_successValue_phoneNumberEntered_enablesForm() {
//        fragment.mPhoneNumberEditText.setText("")
//
//        assertFalse(fragment.mDescriptionLayout.isEnabled)
//        assertFalse(fragment.mEnableThisLocationCheckBox.isEnabled)
//        assertFalse(fragment.mCallControlCheckBox.isEnabled)
//        assertFalse(fragment.mPreventDivertingCallsCheckBox.isEnabled)
//        assertFalse(fragment.mAnswerConfirmationCheckBox.isEnabled)
//
//        val newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        assertTrue(fragment.mDescriptionLayout.isEnabled)
//        assertTrue(fragment.mEnableThisLocationCheckBox.isEnabled)
//        assertTrue(fragment.mCallControlCheckBox.isEnabled)
//        assertTrue(fragment.mPreventDivertingCallsCheckBox.isEnabled)
//        assertTrue(fragment.mAnswerConfirmationCheckBox.isEnabled)
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_successValue_noPhoneNumberEntered_disablesForm() {
//        val newLocation = NextivaAnywhereLocation("", "Home Phone", true, true, true, true)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        assertFalse(fragment.mDescriptionLayout.isEnabled)
//        assertFalse(fragment.mEnableThisLocationCheckBox.isEnabled)
//        assertFalse(fragment.mCallControlCheckBox.isEnabled)
//        assertFalse(fragment.mPreventDivertingCallsCheckBox.isEnabled)
//        assertFalse(fragment.mAnswerConfirmationCheckBox.isEnabled)
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_successValue_callsToSetCallSettingsListenerToIndicateFormUpdated() {
//        reset(mockSetCallSettingsListener)
//
//        val newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//        whenever(mockViewModel.editingLocation).thenReturn(newLocation)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(mockSetCallSettingsListener).onFormUpdated()
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_errorValue_callsToDialogManagerToDismissDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.error("", null))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun editingLocationObserverValueUpdated_errorValue_callsToDialogManagerToShowErrorDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockEditingLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.error("", null))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION)
//    }
//
//    @Test
//    fun saveLocationObserverValueUpdated_successValue_callsToSetCallSettingsListenerToIndicateFormSaved() {
//        val newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>>> = argumentCaptor()
//        val callSettingsSavedCaptor: KArgumentCaptor<Intent> = argumentCaptor()
//
//        verify(mockSaveLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(RxEvents.NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, newLocation, "2223334444")))
//
//        verify(mockSetCallSettingsListener).onCallSettingsSaved(callSettingsSavedCaptor.capture())
//
//        assertEquals(Enums.CallSettings.FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION, callSettingsSavedCaptor.firstValue.getStringExtra(Constants.EXTRA_CALL_SETTINGS_KEY))
//        assertEquals(newLocation, callSettingsSavedCaptor.firstValue.getSerializableExtra(Constants.EXTRA_CALL_SETTINGS_VALUE))
//        assertEquals("2223334444", callSettingsSavedCaptor.firstValue.getStringExtra(Constants.EXTRA_OLD_PHONE_NUMBER))
//        assertEquals(Constants.ACTION_TYPE_SAVED, callSettingsSavedCaptor.firstValue.getStringExtra(Constants.EXTRA_ACTION))
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallBack_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, true, false)))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallBack_callsToDialogManagerToShowDialog() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, true, false)))
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
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallBackClickPositiveButton_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, true, false)))
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_YES_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallBackClickPositiveButton_callsToViewModelToDeleteLocation() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, true, false)))
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
//        verify(mockViewModel).deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, nextivaAnywhereLocation)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallBackClickNegativeButton_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, true, false)))
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_BACK_CONFLICT_DIALOG_NO_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallThrough_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, true)))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallThrough_callsToDialogManagerToShowDialog() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, true)))
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
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallThroughClickPositiveButton_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, true)))
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
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_YES_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallThroughClickPositiveButton_callsToViewModelToDeleteLocation() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, true)))
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
//        verify(mockViewModel).deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, nextivaAnywhereLocation)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_conflictWithCallThroughClickNegativeButton_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, true)))
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
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.NEGATIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.CALL_THROUGH_CONFLICT_DIALOG_NO_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_noConflict_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, false)))
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_noConflict_callsToDialogManagerToShowDialog() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, false)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(0),
//                eq(R.string.set_call_settings_nextiva_anywhere_location_delete_location_content),
//                eq(R.string.general_delete),
//                buttonCaptor.capture(),
//                eq(R.string.general_cancel),
//                any())
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_noConflictClickPositiveButton_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, false)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(0),
//                eq(R.string.set_call_settings_nextiva_anywhere_location_delete_location_content),
//                eq(R.string.general_delete),
//                buttonCaptor.capture(),
//                eq(R.string.general_cancel),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_DELETE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_noConflictClickPositiveButton_callsToViewModelToDeleteLocation() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, false)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(0),
//                eq(R.string.set_call_settings_nextiva_anywhere_location_delete_location_content),
//                eq(R.string.general_delete),
//                buttonCaptor.capture(),
//                eq(R.string.general_cancel),
//                any())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(mockViewModel).deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, nextivaAnywhereLocation)
//    }
//
//    @Test
//    fun deleteLocationValidationEventObserverValueUpdated_noConflictClickNegativeButton_callsToAnalyticsManager() {
//        val nextivaAnywhereLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<SingleEvent<ValidationEvent>>> = argumentCaptor()
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(mockDeleteLocationValidationEventLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(SingleEvent(ValidationEvent(nextivaAnywhereServiceSettings, nextivaAnywhereLocation, false, false)))
//
//        verify(dialogManager).showDialog(
//                eq(fragment.activity!!),
//                eq(0),
//                eq(R.string.set_call_settings_nextiva_anywhere_location_delete_location_content),
//                eq(R.string.general_delete),
//                any(),
//                eq(R.string.general_cancel),
//                buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.NEGATIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, Enums.Analytics.EventName.DELETE_LOCATION_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun deleteLocationObserverValueUpdated_loadingValue_callsToDialogManagerToShowProgressDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockDeleteLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.loading(null))
//
//        verify(dialogManager).showProgressDialog(fragment.activity!!, Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION, R.string.progress_processing)
//    }
//
//    @Test
//    fun deleteLocationObserverValueUpdated_successValue_callsToDialogManagerToDismissDialog() {
//        val newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockDeleteLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun deleteLocationObserverValueUpdated_successValue_callsToSetCallSettingsListenerToIndicateFormSaved() {
//        val newLocation = NextivaAnywhereLocation("4443332222", "Home Phone", true, true, true, true)
//
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//        val callSettingsSavedCaptor: KArgumentCaptor<Intent> = argumentCaptor()
//
//        verify(mockDeleteLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.success(newLocation))
//
//        verify(mockSetCallSettingsListener).onCallSettingsDeleted(callSettingsSavedCaptor.capture())
//
//        assertEquals(Enums.CallSettings.FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION, callSettingsSavedCaptor.firstValue.getStringExtra(Constants.EXTRA_CALL_SETTINGS_KEY))
//        assertEquals(newLocation, callSettingsSavedCaptor.firstValue.getSerializableExtra(Constants.EXTRA_CALL_SETTINGS_VALUE))
//        assertEquals(Constants.ACTION_TYPE_DELETED, callSettingsSavedCaptor.firstValue.getStringExtra(Constants.EXTRA_ACTION))
//    }
//
//    @Test
//    fun deleteLocationObserverValueUpdated_errorValue_callsToDialogManagerToDismissDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockDeleteLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.error("", null))
//
//        verify(dialogManager).dismissProgressDialog()
//    }
//
//    @Test
//    fun deleteLocationObserverValueUpdated_errorValue_callsToDialogManagerToShowErrorDialog() {
//        val observerCaptor: KArgumentCaptor<Observer<Resource<NextivaAnywhereLocation>>> = argumentCaptor()
//
//        verify(mockDeleteLocationLiveData).observe(any(), observerCaptor.capture())
//
//        observerCaptor.firstValue.onChanged(Resource.error("", null))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.EDIT_NEXTIVA_ANYWHERE_LOCATION)
//    }
//}