package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.Observer
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsSimultaneousRingViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Assert.assertEquals
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
class SetCallSettingsSimultaneousRingViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockServiceSettingsResponseEventObserver: Observer<Boolean> = mock()

    @Inject
    lateinit var userRepository: UserRepository

    lateinit var viewModel: SetCallSettingsSimultaneousRingViewModel

    override fun setup() {
        super.setup()
        hiltRule.inject()

        viewModel = SetCallSettingsSimultaneousRingViewModel(Application(), userRepository)

        viewModel.serviceSettingsEventLiveData.observeForever(mockServiceSettingsResponseEventObserver)
    }

    @Test
    fun saveServiceSettings_callsToUserRepository() {
        val mockFormServiceSettings: ServiceSettings = mock()
        val putResponseEvent = RxEvents.ServiceSettingsPutResponseEvent(true, null, mockFormServiceSettings)

        whenever(userRepository.putServiceSettings(mockFormServiceSettings)).thenReturn(Single.just(putResponseEvent))

        viewModel.saveServiceSettings(mockFormServiceSettings)

        verify(userRepository).putServiceSettings(mockFormServiceSettings)
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
    fun getLocationListItems_hasLocationsList_returnsLocationsList() {
        val mockServiceSettings: ServiceSettings = mock()
        val mockSettingsList: ArrayList<SimultaneousRingLocation> = mock()
        whenever(mockServiceSettings.simultaneousRingLocationsList).thenReturn(mockSettingsList)

        viewModel.serviceSettings = mockServiceSettings

        Assert.assertEquals(mockSettingsList, viewModel.getLocationListItems())
    }

    @Test
    fun getLocationListItems_noLocationsList_returnsEmptyList() {
        val mockServiceSettings: ServiceSettings = mock()
        whenever(mockServiceSettings.simultaneousRingLocationsList).thenReturn(null)

        viewModel.serviceSettings = mockServiceSettings

        Assert.assertTrue(viewModel.getLocationListItems().isEmpty())
    }

    @Test
    fun onFormUpdated_callListenerOnFormUpdated() {
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        viewModel.onFormUpdated()

        verify(mockSetCallSettingsListener).onFormUpdated()
    }

    @Test
    fun onCallSettingsRetrieved_callsToListener() {
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        viewModel.onCallSettingsRetrieved()

        verify(mockSetCallSettingsListener).onCallSettingsRetrieved(any())
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

        val argumentCaptor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(mockServiceSettingsResponseEventObserver).onChanged(argumentCaptor.capture())
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

        val argumentCaptor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(mockServiceSettingsResponseEventObserver).onChanged(argumentCaptor.capture())
        assertEquals(false, argumentCaptor.firstValue)
    }

    @Test
    fun serviceSettingsPutResponseEventConsumer_eventIsSuccessful() {
        val mockEventServiceSettings: ServiceSettings = mock()
        val mockServiceSettings: ServiceSettings = mock()
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.serviceSettings = mockServiceSettings
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        val event = RxEvents.ServiceSettingsPutResponseEvent(true, null, mockEventServiceSettings)

        whenever(mockEventServiceSettings.active).thenReturn(true)
        whenever(mockEventServiceSettings.dontRingWhileOnCall).thenReturn(true)

        viewModel.getServiceSettingsPutResponseEventConsumer().accept(event)

        assertEquals(mockServiceSettings, viewModel.serviceSettings)
        verify(mockSetCallSettingsListener).onCallSettingsSaved(any())
        verify(mockServiceSettings).setActive(true)
        verify(mockServiceSettings).setDontRingWhileOnCall(true)

        val argumentCaptor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(mockServiceSettingsResponseEventObserver).onChanged(argumentCaptor.capture())
        assertEquals(true, argumentCaptor.firstValue)
    }

    @Test
    fun serviceSettingsPutResponseEventConsumer_eventIsNotSuccessful() {
        val mockServiceSettings: ServiceSettings = mock()
        val mockSetCallSettingsListener: SetCallSettingsListener = mock()
        viewModel.setCallSettingsListener = mockSetCallSettingsListener

        val event = RxEvents.ServiceSettingsPutResponseEvent(false, null, mockServiceSettings)

        viewModel.getServiceSettingsPutResponseEventConsumer().accept(event)

        verify(mockSetCallSettingsListener, never()).onCallSettingsRetrieved(any())

        val argumentCaptor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(mockServiceSettingsResponseEventObserver).onChanged(argumentCaptor.capture())
        assertEquals(false, argumentCaptor.firstValue)
    }
}