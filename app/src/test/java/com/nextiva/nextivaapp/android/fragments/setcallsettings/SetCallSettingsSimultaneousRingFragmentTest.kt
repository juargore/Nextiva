package com.nextiva.nextivaapp.android.fragments.setcallsettings
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeSetCallSettingsActivity
//import com.nextiva.nextivaapp.android.models.ServiceSettings
//import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation
//import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsSimultaneousRingViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.assertTrue
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment
//import javax.inject.Inject
//
//class SetCallSettingsSimultaneousRingFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var fragment: SetCallSettingsSimultaneousRingFragment
//    private lateinit var simultaneousRingServiceSettings: ServiceSettings
//
//    private val mockViewModel: SetCallSettingsSimultaneousRingViewModel = mock()
//    private val mockServiceSettingsEventLiveData: MutableLiveData<Boolean> = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        simultaneousRingServiceSettings = ServiceSettings(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL, "", null, null, null, null, null, true, false, null, null, arrayListOf(SimultaneousRingLocation("8887776666", false)))
//
//        whenever(mockViewModel.serviceSettings).thenReturn(simultaneousRingServiceSettings)
//        whenever(mockViewModel.getLocationListItems()).thenReturn(arrayListOf(SimultaneousRingLocation("8887776666", false)))
//        whenever(viewModelFactory.create(SetCallSettingsSimultaneousRingViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.serviceSettingsEventLiveData).thenReturn(mockServiceSettingsEventLiveData)
//
//        fragment = SetCallSettingsSimultaneousRingFragment.newInstance(simultaneousRingServiceSettings)
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
//    fun onCreate_setupServiceSettings_setsUpViewModel() {
//        verify(mockViewModel).serviceSettings = any()
//        verify(mockViewModel).getSingleServiceSettings()
//
//        assertTrue(fragment.enabledCheckBox.isEnabled)
//    }
//
//    @Test
//    fun enabledOnCheckedChangeListener_setsChecked_callsToViewModel() {
//        reset(mockViewModel)
//
//        fragment.enabledCheckBox.performClick()
//
//        verify(mockViewModel).onFormUpdated()
//    }
//
//    @Test
//    fun dontRingWhileOnCallOnCheckedChangeListener_setsChecked_callsToViewModel() {
//        reset(mockViewModel)
//
//        fragment.dontRingWhileOnCallCheckBox.performClick()
//
//        verify(mockViewModel).onFormUpdated()
//    }
//
//    @Test
//    fun saveForm_showsProgressAndCallsToViewModel() {
//        fragment.saveForm()
//
//        verify(mockViewModel).saveServiceSettings(any())
//    }
//}