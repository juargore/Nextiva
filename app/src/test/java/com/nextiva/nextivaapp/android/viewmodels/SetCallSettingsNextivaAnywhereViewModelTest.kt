package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.Observer
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
class SetCallSettingsNextivaAnywhereViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockServiceSettingsGetResponseEventObserver: Observer<Boolean> = mock()
    private val mockServiceSettingsSaveResponseEventObserver: Observer<Boolean> = mock()

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var viewModel: SetCallSettingsNextivaAnywhereViewModel

    override fun setup() {
        super.setup()
        hiltRule.inject()

        viewModel = SetCallSettingsNextivaAnywhereViewModel(Application(), userRepository, sessionManager)

        viewModel.serviceSettingsSaveResponseEventLiveData.observeForever(mockServiceSettingsSaveResponseEventObserver)
        viewModel.serviceSettingsGetResponseEventLiveData.observeForever(mockServiceSettingsGetResponseEventObserver)
    }

    @Test
    fun saveSessionServiceSettings_callsToSessionManager() {
        viewModel.saveSessionServiceSettings()

        verify(sessionManager).nextivaAnywhereServiceSettings = viewModel.serviceSettings
    }

    @Test
    fun getSingleServiceSettings_callsToUserRepository() {
        whenever(userRepository.getSingleServiceSettings(any(), any())).thenReturn(Observable.never())

        val mockServiceSettings: ServiceSettings = mock()
        whenever(mockServiceSettings.uri).thenReturn("URI")
        whenever(mockServiceSettings.type).thenReturn("type")

        viewModel.serviceSettings = mockServiceSettings
        viewModel.getSingleServiceSettings()

        verify(userRepository).getSingleServiceSettings("type", "URI")
    }

    @Test
    fun saveServiceSettings_callsToUserRepository() {
        val mockFormCallSettings: ServiceSettings = mock()
        val mockServiceSettings: ServiceSettings = mock()
        whenever(mockServiceSettings.uri).thenReturn("URI")
        whenever(mockServiceSettings.type).thenReturn("type")

        val putResponseEvent = RxEvents.ServiceSettingsPutResponseEvent(true, null, mockServiceSettings)
        val mockGetObservable: Observable<RxEvents.ServiceSettingsGetResponseEvent> = mock()

        viewModel.serviceSettings = mockServiceSettings

        whenever(userRepository.putServiceSettings(mockFormCallSettings)).thenReturn(Single.just(putResponseEvent))
        whenever(userRepository.getSingleServiceSettings("type", "URI")).thenReturn(mockGetObservable)
        whenever(mockGetObservable.single(any())).thenReturn(Single.never())

        viewModel.saveServiceSettings(mockFormCallSettings)

        verify(userRepository).putServiceSettings(mockFormCallSettings)
        verify(userRepository).getSingleServiceSettings("type", "URI")
    }

    @Test
    fun getLocationListItems_hasLocationsList_returnsLocationsList() {
        val mockServiceSettings: ServiceSettings = mock()
        val mockSettingsList: ArrayList<NextivaAnywhereLocation> = mock()
        whenever(mockServiceSettings.nextivaAnywhereLocationsList).thenReturn(mockSettingsList)

        viewModel.serviceSettings = mockServiceSettings

        assertEquals(mockSettingsList, viewModel.getLocationListItems())
    }

    @Test
    fun getLocationListItems_noLocationsList_returnsEmptyList() {
        val mockServiceSettings: ServiceSettings = mock()
        whenever(mockServiceSettings.nextivaAnywhereLocationsList).thenReturn(null)

        viewModel.serviceSettings = mockServiceSettings

        assertTrue(viewModel.getLocationListItems().isEmpty())
    }

    @Test
    fun onFormUpdated_callListenerOnFormUpdated() {
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        viewModel.onFormUpdated()

        verify(mockSetCallSettingsListener).onFormUpdated()
    }

    @Test
    fun serviceSettingsGetResponseEventConsumer_eventIsSuccessful() {
        val mockServiceSettings: ServiceSettings = mock()
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        val event = RxEvents.ServiceSettingsGetResponseEvent(true, mockServiceSettings)

        viewModel.getServiceSettingsGetResponseEventConsumer().accept(event)

        assertEquals(mockServiceSettings, viewModel.serviceSettings)
        verify(mockSetCallSettingsListener).onCallSettingsRetrieved(any())
        verify(sessionManager).nextivaAnywhereServiceSettings = mockServiceSettings

        val argumentCaptor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(mockServiceSettingsGetResponseEventObserver).onChanged(argumentCaptor.capture())
        assertEquals(true, argumentCaptor.firstValue)
    }

    @Test
    fun serviceSettingsGetResponseEventConsumer_eventIsNotSuccessful() {
        val mockServiceSettings: ServiceSettings = mock()
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        val event = RxEvents.ServiceSettingsGetResponseEvent(false, mockServiceSettings)

        viewModel.getServiceSettingsGetResponseEventConsumer().accept(event)

        verify(mockSetCallSettingsListener, never()).onCallSettingsRetrieved(any())
        verify(sessionManager, never()).nextivaAnywhereServiceSettings = mockServiceSettings

        val argumentCaptor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(mockServiceSettingsGetResponseEventObserver).onChanged(argumentCaptor.capture())
        assertEquals(false, argumentCaptor.firstValue)
    }

    @Test
    fun saveServiceSettingsConsumer_eventIsSuccessful() {
        val mockServiceSettings: ServiceSettings = mock()
        val mockEventServiceSettings: ServiceSettings = mock()
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        viewModel.serviceSettings = mockServiceSettings

        whenever(mockEventServiceSettings.alertAllLocationsForClickToDialCallsRaw).thenReturn(true)

        val event = RxEvents.ServiceSettingsGetResponseEvent(true, mockEventServiceSettings)

        viewModel.getSaveSettingsConsumer().accept(event)

        verify(mockServiceSettings).setAlertAllLocationsForClickToDialCalls(true)
        verify(mockSetCallSettingsListener).onCallSettingsSaved(any())

        val argumentCaptor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(mockServiceSettingsSaveResponseEventObserver).onChanged(argumentCaptor.capture())
        assertEquals(true, argumentCaptor.firstValue)
    }

    @Test
    fun saveServiceSettingsConsumer_eventIsNotSuccessful() {
        val mockServiceSettings: ServiceSettings = mock()
        val mockEventServiceSettings: ServiceSettings = mock()
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        viewModel.serviceSettings = mockServiceSettings

        val event = RxEvents.ServiceSettingsGetResponseEvent(false, mockEventServiceSettings)

        viewModel.getSaveSettingsConsumer().accept(event)

        verify(mockServiceSettings, never()).setAlertAllLocationsForClickToDialCalls(any())
        verify(mockSetCallSettingsListener, never()).onCallSettingsSaved(any())

        val argumentCaptor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(mockServiceSettingsSaveResponseEventObserver).onChanged(argumentCaptor.capture())
        assertEquals(false, argumentCaptor.firstValue)
    }
}