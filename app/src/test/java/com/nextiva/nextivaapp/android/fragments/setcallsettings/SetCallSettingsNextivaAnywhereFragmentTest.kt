package com.nextiva.nextivaapp.android.fragments.setcallsettings

//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeSetCallSettingsActivity
//import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
//import com.nextiva.nextivaapp.android.models.ServiceSettings
//import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.assertTrue
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment
//import javax.inject.Inject
//
//class SetCallSettingsNextivaAnywhereFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var dialogManager: DialogManager
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var fragment: SetCallSettingsNextivaAnywhereFragment
//    private lateinit var nextivaAnywhereServiceSettings: ServiceSettings
//
//    private val mockViewModel: SetCallSettingsNextivaAnywhereViewModel = mock()
//    private val mockServiceSettingsGetResponseEventLiveData: MutableLiveData<Boolean> = mock()
//    private val mockServiceSettingsSaveResponseEventLiveData: MutableLiveData<Boolean> = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        nextivaAnywhereServiceSettings = ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "", null, null, null, null, null, true, false, arrayListOf(NextivaAnywhereLocation("8887776666", "My Phone", false, false, false, false)), null, null)
//
//        whenever(mockViewModel.serviceSettings).thenReturn(nextivaAnywhereServiceSettings)
//        whenever(mockViewModel.pendingAlertAllLocations).thenReturn(true)
//        whenever(mockViewModel.getLocationListItems()).thenReturn(arrayListOf(NextivaAnywhereLocation("8887776666", "My Phone", false, false, false, false)))
//        whenever(viewModelFactory.create(SetCallSettingsNextivaAnywhereViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.serviceSettingsGetResponseEventLiveData).thenReturn(mockServiceSettingsGetResponseEventLiveData)
//        whenever(mockViewModel.serviceSettingsSaveResponseEventLiveData).thenReturn(mockServiceSettingsSaveResponseEventLiveData)
//
//        fragment = SetCallSettingsNextivaAnywhereFragment.newInstance(nextivaAnywhereServiceSettings)
//        startFragment(fragment, FakeSetCallSettingsActivity::class.java)
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
//    fun onCreate_setsUpViewModel() {
//        verify(mockViewModel).serviceSettings = any()
//        verify(mockViewModel).getSingleServiceSettings()
//    }
//
//    @Test
//    fun setUpServiceSettings_viewModelHasSettings() {
//        verify(mockViewModel).onFormUpdated()
//
//        assertTrue(fragment.mAlertAllLocationsCheckBox.isChecked)
//    }
//
//    @Test
//    fun alertAllLocationsOnCheckedChangeListener_setsChecked_callsToViewModel() {
//        reset(mockViewModel)
//
//        fragment.mAlertAllLocationsCheckBox.performClick()
//
//        verify(mockViewModel).pendingAlertAllLocations = false
//        verify(mockViewModel).onFormUpdated()
//    }
//
//    @Test
//    fun serviceSettingsGetResponseObserver_eventIsSuccessful() {
//        reset(mockViewModel)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//
//        verify(mockServiceSettingsGetResponseEventLiveData).observe(any(), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(true)
//
//        verify(mockViewModel).onFormUpdated()
//        verify(dialogManager).dismissProgressDialog()
//        verify(dialogManager, never()).showErrorDialog(any(), any())
//    }
//
//    @Test
//    fun serviceSettingsGetResponseObserver_eventIsNotSuccessful() {
//        reset(mockViewModel)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//
//        verify(mockServiceSettingsGetResponseEventLiveData).observe(any(), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(false)
//
//        verify(mockViewModel).onFormUpdated()
//        verify(dialogManager).dismissProgressDialog()
//        verify(dialogManager).showErrorDialog(any(), any())
//    }
//
//    @Test
//    fun saveServiceSettingsObserver_eventIsSuccessful() {
//        reset(mockViewModel)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//
//        verify(mockServiceSettingsGetResponseEventLiveData).observe(any(), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(true)
//
//        verify(dialogManager).dismissProgressDialog()
//        verify(dialogManager, never()).showErrorDialog(any(), any())
//    }
//
//    @Test
//    fun saveServiceSettingsObserver_eventIsNotSuccessful() {
//        reset(mockViewModel)
//
//        val argumentCaptor: KArgumentCaptor<Observer<Boolean>> = argumentCaptor()
//
//        verify(mockServiceSettingsGetResponseEventLiveData).observe(any(), argumentCaptor.capture())
//        argumentCaptor.firstValue.onChanged(false)
//
//        verify(dialogManager).dismissProgressDialog()
//        verify(dialogManager).showErrorDialog(any(), any())
//    }
//}