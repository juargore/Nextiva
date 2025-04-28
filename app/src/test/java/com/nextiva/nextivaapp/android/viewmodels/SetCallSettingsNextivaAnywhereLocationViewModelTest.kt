package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.Observer
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
import com.nextiva.nextivaapp.android.models.Resource
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.models.SingleEvent
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereLocationViewModel
import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereLocationViewModel.ValidationEvent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import javax.inject.Inject

//TODO This could be a PowerMockTest because we don't use the Android Application, but there were some underlying issues with LiveData
@HiltAndroidTest
class SetCallSettingsNextivaAnywhereLocationViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockNextivaAnywhereServiceSettingsObserver: Observer<ServiceSettings> = mock()
    private val mockEditingLocationObserver: Observer<Resource<NextivaAnywhereLocation>> = mock()
    private val mockSaveLocationValidationEventObserver: Observer<SingleEvent<ValidationEvent>> = mock()
    private val mockSaveLocationObserver: Observer<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>> = mock()
    private val mockDeleteLocationValidationEventObserver: Observer<SingleEvent<ValidationEvent>> = mock()
    private val mockDeleteLocationObserver: Observer<Resource<NextivaAnywhereLocation>> = mock()

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var viewModel: SetCallSettingsNextivaAnywhereLocationViewModel

    private lateinit var nextivaAnywhereServiceSettings: ServiceSettings

    override fun setup() {
        super.setup()
        hiltRule.inject()

        viewModel = SetCallSettingsNextivaAnywhereLocationViewModel(
                Application(),
                userRepository,
                settingsManager,
                sessionManager)

        viewModel.nextivaAnywhereServiceSettingsLiveData.observeForever(mockNextivaAnywhereServiceSettingsObserver)
        viewModel.editingLocationLiveData.observeForever(mockEditingLocationObserver)
        viewModel.saveLocationValidationEventLiveData.observeForever(mockSaveLocationValidationEventObserver)
        viewModel.saveLocationLiveData.observeForever(mockSaveLocationObserver)
        viewModel.deleteLocationValidationEventLiveData.observeForever(mockDeleteLocationValidationEventObserver)
        viewModel.deleteLocationLiveData.observeForever(mockDeleteLocationObserver)

        nextivaAnywhereServiceSettings = ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "", null, null, null, null, null, true, false, arrayListOf(NextivaAnywhereLocation("8887776666", "My Phone", false, null, null, null)), null, null)
    }

    @Test
    fun setNextivaAnywhereServiceSettings_setsNextivaAnywhereServiceSettingsValue() {
        val serviceSettings = ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "uri")
        viewModel.setNextivaAnywhereServiceSettings(serviceSettings)

        val argumentCaptor: KArgumentCaptor<ServiceSettings> = argumentCaptor()

        verify(mockNextivaAnywhereServiceSettingsObserver).onChanged(argumentCaptor.capture())

        assertEquals(serviceSettings, argumentCaptor.firstValue)
    }

    @Test
    fun setEditingNextivaAnywhereLocation_setsNextivaAnywhereLocationValue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        viewModel.editingLocation = editingLocation

        val argumentCaptor: KArgumentCaptor<Resource<NextivaAnywhereLocation>> = argumentCaptor()

        verify(mockEditingLocationObserver).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.SUCCESS, argumentCaptor.firstValue.status)
        assertEquals(editingLocation, argumentCaptor.firstValue.data)
    }

    @Test
    fun fetchNextivaAnywhereLocation_requiresEditingLocation() {
        viewModel.fetchNextivaAnywhereLocation()

        verify(mockEditingLocationObserver, times(0)).onChanged(any())
    }

    @Test
    fun fetchNextivaAnywhereLocation_validEditingLocation_setsLoadingValue() {
        whenever(userRepository.getNextivaAnywhereLocation((any()))).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationGetResponseEvent(false, null)))

        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        viewModel.editingLocation = editingLocation
        reset(mockEditingLocationObserver)

        viewModel.fetchNextivaAnywhereLocation()

        val argumentCaptor: KArgumentCaptor<Resource<NextivaAnywhereLocation>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockEditingLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.LOADING, argumentCaptor.firstValue.status)
    }

    @Test
    fun fetchNextivaAnywhereLocation_validEditingLocation_callsToUserRepositoryToGetNextivaAnywhereLocation() {
        whenever(userRepository.getNextivaAnywhereLocation((any()))).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationGetResponseEvent(false, null)))

        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        viewModel.editingLocation = editingLocation

        viewModel.fetchNextivaAnywhereLocation()

        verify(userRepository).getNextivaAnywhereLocation("2223334444")
    }

    @Test
    fun fetchNextivaAnywhereLocation_validEditingLocation_success_setsSuccessValue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        val apiLocation = NextivaAnywhereLocation("3334445555", "Desc", true, true, true, true)
        whenever(userRepository.getNextivaAnywhereLocation("2223334444")).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationGetResponseEvent(true, apiLocation)))

        viewModel.editingLocation = editingLocation
        reset(mockEditingLocationObserver)

        viewModel.fetchNextivaAnywhereLocation()

        val argumentCaptor: KArgumentCaptor<Resource<NextivaAnywhereLocation>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockEditingLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.SUCCESS, argumentCaptor.secondValue.status)
        assertEquals(apiLocation, argumentCaptor.secondValue.data)
    }

    @Test
    fun fetchNextivaAnywhereLocation_validEditingLocation_error_setsErrorValue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        whenever(userRepository.getNextivaAnywhereLocation("2223334444")).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationGetResponseEvent(false, null)))

        viewModel.editingLocation = editingLocation
        reset(mockEditingLocationObserver)

        viewModel.fetchNextivaAnywhereLocation()

        val argumentCaptor: KArgumentCaptor<Resource<NextivaAnywhereLocation>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockEditingLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.ERROR, argumentCaptor.secondValue.status)
        assertEquals(editingLocation, argumentCaptor.secondValue.data)
    }

    @Test
    fun validateSaveNextivaAnywhereLocation_requiresNextivaAnywhereServiceSettings() {
        val newLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.validateSaveNextivaAnywhereLocation(newLocation)

        verifyNoMoreInteractions(mockSaveLocationValidationEventObserver)
    }

    @Test
    fun validateSaveNextivaAnywhereLocation_callBackConflict_setsValidationEventValue() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)
        whenever(sessionManager.getIsCallBackEnabled(any(), any())).thenReturn(false)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)

        val newLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.validateSaveNextivaAnywhereLocation(newLocation)

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockSaveLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertTrue(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertFalse(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!![1])
    }

    @Test
    fun validateSaveNextivaAnywhereLocation_callThroughConflict_setsValidationEventValue() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)
        whenever(sessionManager.getIsCallThroughEnabled(any(), any())).thenReturn(false)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)

        val newLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.validateSaveNextivaAnywhereLocation(newLocation)

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockSaveLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertFalse(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertTrue(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!![1])
    }

    @Test
    fun validateSaveNextivaAnywhereLocation_noConflict_setsValidationEventValue() {
        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)

        val newLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.validateSaveNextivaAnywhereLocation(newLocation)

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockSaveLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertFalse(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertFalse(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!![1])
    }

    @Test
    fun validateSaveNextivaAnywhereLocation_editingLocationWithCallBackConflict_setsValidationEventValue() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)
        whenever(sessionManager.getIsCallBackEnabled(any(), any())).thenReturn(false)

        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)
        viewModel.editingLocation = editingLocationFull

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)

        val newLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.validateSaveNextivaAnywhereLocation(newLocation)

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockSaveLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertTrue(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertFalse(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!![1])
    }

    @Test
    fun validateSaveNextivaAnywhereLocation_editingLocationWithCallThroughConflict_setsValidationEventValue() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)
        whenever(sessionManager.getIsCallThroughEnabled(any(), any())).thenReturn(false)

        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)
        viewModel.editingLocation = editingLocationFull

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)

        val newLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.validateSaveNextivaAnywhereLocation(newLocation)

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockSaveLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertFalse(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertTrue(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!![1])
    }

    @Test
    fun validateSaveNextivaAnywhereLocation_editingLocationWithNoConflict_setsValidationEventValue() {
        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)

        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)
        viewModel.editingLocation = editingLocationFull

        val newLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.validateSaveNextivaAnywhereLocation(newLocation)

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockSaveLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertFalse(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertFalse(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(newLocation, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!![1])
    }

    @Test
    fun saveNextivaAnywhereLocation_callBackConflict_callsToSettingsManagerToSetDefaultDialingService() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)
        whenever(sessionManager.getIsCallBackEnabled(any(), any())).thenReturn(false)

        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)

        whenever(userRepository.postNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, newLocationFull, null)))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        verify(settingsManager).dialingService = Enums.Service.DialingServiceTypes.VOIP
    }

    @Test
    fun saveNextivaAnywhereLocation_callThroughConflict_callsToSettingsManagerToSetDefaultDialingService() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)
        whenever(sessionManager.getIsCallThroughEnabled(any(), any())).thenReturn(false)

        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)

        whenever(userRepository.postNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, newLocationFull, null)))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        verify(settingsManager).dialingService = Enums.Service.DialingServiceTypes.VOIP
    }

    @Test
    fun saveNextivaAnywhereLocation_setsLoadingValue() {
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)

        whenever(userRepository.postNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, newLocationFull, null)))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        val argumentCaptor: KArgumentCaptor<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockSaveLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.LOADING, argumentCaptor.firstValue.status)
    }

    @Test
    fun saveNextivaAnywhereLocation_savingNewLocation_callsToUserRepositoryToSaveLocation() {
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)

        whenever(userRepository.postNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, newLocationFull, null)))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        verify(userRepository).postNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)
    }

    @Test
    fun saveNextivaAnywhereLocation_savingNewLocation_success_callsToSessionManagerToSetNextivaAnywhereServiceSettings() {
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)

        whenever(userRepository.postNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, newLocationFull, null)))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        verify(sessionManager).nextivaAnywhereServiceSettings = nextivaAnywhereServiceSettings
    }

    @Test
    fun saveNextivaAnywhereLocation_savingNewLocation_success_setsSuccessValue() {
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)

        whenever(userRepository.postNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, newLocationFull, null)))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        val argumentCaptor: KArgumentCaptor<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockSaveLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.SUCCESS, argumentCaptor.secondValue.status)
        assertEquals(nextivaAnywhereServiceSettings, argumentCaptor.secondValue.data!!.nextivaAnywhereServiceSettings)
        assertEquals(newLocationFull, argumentCaptor.secondValue.data!!.nextivaAnywhereLocation)
        assertNull(argumentCaptor.secondValue.data!!.oldPhoneNumber)
    }

    @Test
    fun saveNextivaAnywhereLocation_savingNewLocation_error_setsErrorValue() {
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)

        whenever(userRepository.postNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, newLocationFull, null)))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        val argumentCaptor: KArgumentCaptor<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockSaveLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.ERROR, argumentCaptor.secondValue.status)
    }

    @Test
    fun saveNextivaAnywhereLocation_savingUpdatedLocation_callsToUserRepositoryToUpdateLocation() {
        val editingLocation = NextivaAnywhereLocation("4443332222", "Old Description", true, true, true, true)
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)
        viewModel.editingLocation = editingLocation

        whenever(userRepository.putNextivaAnywhereLocation(any(), any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, newLocationFull, "4443332222")))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        verify(userRepository).putNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull, "4443332222")
    }

    @Test
    fun saveNextivaAnywhereLocation_savingUpdatedLocation_success_callsToSessionManagerToSetNextivaAnywhereServiceSettings() {
        val editingLocation = NextivaAnywhereLocation("4443332222", "Old Description", true, true, true, true)
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)
        viewModel.editingLocation = editingLocation

        whenever(userRepository.putNextivaAnywhereLocation(any(), any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, newLocationFull, "4443332222")))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        verify(sessionManager).nextivaAnywhereServiceSettings = nextivaAnywhereServiceSettings
    }

    @Test
    fun saveNextivaAnywhereLocation_savingUpdatedLocation_success_setsSuccessValue() {
        val editingLocation = NextivaAnywhereLocation("4443332222", "Old Description", true, true, true, true)
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)
        viewModel.editingLocation = editingLocation

        whenever(userRepository.putNextivaAnywhereLocation(any(), any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, newLocationFull, "4443332222")))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        val argumentCaptor: KArgumentCaptor<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockSaveLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.SUCCESS, argumentCaptor.secondValue.status)
        assertEquals(nextivaAnywhereServiceSettings, argumentCaptor.secondValue.data!!.nextivaAnywhereServiceSettings)
        assertEquals(newLocationFull, argumentCaptor.secondValue.data!!.nextivaAnywhereLocation)
        assertEquals("4443332222", argumentCaptor.secondValue.data!!.oldPhoneNumber)
    }

    @Test
    fun saveNextivaAnywhereLocation_savingUpdatedLocation_error_setsErrorValue() {
        val editingLocation = NextivaAnywhereLocation("4443332222", "Old Description", true, true, true, true)
        val newLocationLite = NextivaAnywhereLocation("2223334444", "Description", false, null, null, null)
        val newLocationFull = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(newLocationLite)
        viewModel.editingLocation = editingLocation

        whenever(userRepository.putNextivaAnywhereLocation(any(), any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, newLocationFull, "4443332222")))

        viewModel.saveNextivaAnywhereLocation(nextivaAnywhereServiceSettings, newLocationFull)

        val argumentCaptor: KArgumentCaptor<Resource<RxEvents.NextivaAnywhereLocationSaveResponseEvent>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockSaveLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.ERROR, argumentCaptor.secondValue.status)
    }

    @Test
    fun validateDeleteNextivaAnywhereLocation_requiresNextivaAnywhereServiceSettings() {
        viewModel.validateDeleteNextivaAnywhereLocation()

        verifyNoMoreInteractions(mockDeleteLocationValidationEventObserver)
    }

    @Test
    fun validateDeleteNextivaAnywhereLocation_callBackConflict_setsValidationEventValue() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)
        whenever(sessionManager.getIsCallBackEnabled(any(), any())).thenReturn(false)

        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        viewModel.validateDeleteNextivaAnywhereLocation()

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockDeleteLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertTrue(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertFalse(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(editingLocationFull, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(1, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!!.size)
    }

    @Test
    fun validateDeleteNextivaAnywhereLocation_callThroughConflict_setsValidationEventValue() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)
        whenever(sessionManager.getIsCallThroughEnabled(any(), any())).thenReturn(false)

        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        viewModel.validateDeleteNextivaAnywhereLocation()

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockDeleteLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertFalse(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertTrue(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(editingLocationFull, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(1, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!!.size)
    }

    @Test
    fun validateDeleteNextivaAnywhereLocation_noConflict_setsValidationEventValue() {
        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        viewModel.validateDeleteNextivaAnywhereLocation()

        val argumentCaptor: KArgumentCaptor<SingleEvent<ValidationEvent>> = argumentCaptor()

        verify(mockDeleteLocationValidationEventObserver).onChanged(argumentCaptor.capture())

        assertFalse(argumentCaptor.firstValue.peekContent().isCallBackConflicting)
        assertFalse(argumentCaptor.firstValue.peekContent().isCallThroughConflicting)
        assertEquals(editingLocationFull, argumentCaptor.firstValue.peekContent().nextivaAnywhereLocation)
        assertEquals(1, argumentCaptor.firstValue.peekContent().proposedServiceSettings.nextivaAnywhereLocationsList!!.size)
    }

    @Test
    fun deleteNextivaAnywhereLocation_callBackConflict_callsToSettingsManagerToSetDefaultDialingService() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)
        whenever(sessionManager.getIsCallBackEnabled(any(), any())).thenReturn(false)

        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        whenever(userRepository.deleteNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationDeleteResponseEvent(false, nextivaAnywhereServiceSettings, editingLocationFull)))

        viewModel.deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, editingLocationFull)

        verify(settingsManager).dialingService = Enums.Service.DialingServiceTypes.VOIP
    }

    @Test
    fun deleteNextivaAnywhereLocation_callThroughConflict_callsToSettingsManagerToSetDefaultDialingService() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)
        whenever(sessionManager.getIsCallThroughEnabled(any(), any())).thenReturn(false)
        whenever(sessionManager.getIsCallBackEnabled(any(), any())).thenReturn(false)

        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        whenever(userRepository.deleteNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationDeleteResponseEvent(false, nextivaAnywhereServiceSettings, editingLocationFull)))

        viewModel.deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, editingLocationFull)

        verify(settingsManager).dialingService = Enums.Service.DialingServiceTypes.VOIP
    }

    @Test
    fun deleteNextivaAnywhereLocation_setsLoadingValue() {
        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        whenever(userRepository.deleteNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationDeleteResponseEvent(false, nextivaAnywhereServiceSettings, editingLocationFull)))

        viewModel.deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, editingLocationFull)

        val argumentCaptor: KArgumentCaptor<Resource<NextivaAnywhereLocation>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockDeleteLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.LOADING, argumentCaptor.firstValue.status)
    }

    @Test
    fun deleteNextivaAnywhereLocation_callsToUserRepositoryToDeleteLocation() {
        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        whenever(userRepository.deleteNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationDeleteResponseEvent(true, nextivaAnywhereServiceSettings, editingLocationFull)))

        viewModel.deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, editingLocationFull)

        verify(userRepository).deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, editingLocationFull)
    }

    @Test
    fun deleteNextivaAnywhereLocation_success_callsToSessionManagerToSetNextivaAnywhereServiceSettings() {
        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        whenever(userRepository.deleteNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationDeleteResponseEvent(true, nextivaAnywhereServiceSettings, editingLocationFull)))

        viewModel.deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, editingLocationFull)

        verify(sessionManager).nextivaAnywhereServiceSettings = nextivaAnywhereServiceSettings
    }

    @Test
    fun deleteNextivaAnywhereLocation_success_setsSuccessValue() {
        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        whenever(userRepository.deleteNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationDeleteResponseEvent(true, nextivaAnywhereServiceSettings, editingLocationFull)))

        viewModel.deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, editingLocationFull)

        val argumentCaptor: KArgumentCaptor<Resource<NextivaAnywhereLocation>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockDeleteLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.SUCCESS, argumentCaptor.secondValue.status)
        assertEquals(editingLocationFull, argumentCaptor.secondValue.data)
    }

    @Test
    fun deleteNextivaAnywhereLocation_error_setsErrorValue() {
        val editingLocationLite = NextivaAnywhereLocation("4443332222", "Description", true, null, null, null)
        val editingLocationFull = NextivaAnywhereLocation("4443332222", "Description", true, true, true, true)
        nextivaAnywhereServiceSettings.nextivaAnywhereLocationsList!!.add(editingLocationLite)

        viewModel.setNextivaAnywhereServiceSettings(nextivaAnywhereServiceSettings)
        viewModel.editingLocation = editingLocationFull

        whenever(userRepository.deleteNextivaAnywhereLocation(any(), any())).thenReturn(Single.just(RxEvents.NextivaAnywhereLocationDeleteResponseEvent(false, nextivaAnywhereServiceSettings, editingLocationFull)))

        viewModel.deleteNextivaAnywhereLocation(nextivaAnywhereServiceSettings, editingLocationFull)

        val argumentCaptor: KArgumentCaptor<Resource<NextivaAnywhereLocation>> = argumentCaptor()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockDeleteLocationObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.ERROR, argumentCaptor.secondValue.status)
    }

    //
    @Test
    fun getEditingLocation_setValue_returnsCorrectValue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertEquals(editingLocation, viewModel.editingLocation)
    }

    @Test
    fun getEditingLocation_unsetValue_returnsNull() {
        assertNull(viewModel.editingLocation)
    }

    @Test
    fun changesMade_newLocationWithEnteredPhoneNumber_returnsTrue() {
        assertTrue(viewModel.changesMade("2223334444", "", false, false, false, false))
    }

    @Test
    fun changesMade_newLocationWithEnteredDescription_returnsTrue() {
        assertTrue(viewModel.changesMade("", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_newLocationWithCheckedEnableThisLocation_returnsTrue() {
        assertTrue(viewModel.changesMade("", "", true, false, false, false))
    }

    @Test
    fun changesMade_newLocationWithCheckedCallControl_returnsTrue() {
        assertTrue(viewModel.changesMade("", "", false, true, false, false))
    }

    @Test
    fun changesMade_newLocationWithCheckedPreventDivertingCalls_returnsTrue() {
        assertTrue(viewModel.changesMade("", "", false, false, true, false))
    }

    @Test
    fun changesMade_newLocationWithCheckedAnswerConfirmation_returnsTrue() {
        assertTrue(viewModel.changesMade("", "", false, false, false, true))
    }

    @Test
    fun changesMade_newLocationBlankForm_returnsFalse() {
        assertFalse(viewModel.changesMade("", "", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithBlankOldEnteredNewPhoneNumber_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithEnteredOldBlankNewPhoneNumber_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithEnteredOldChangesNewPhoneNumber_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("3334445555", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithBlankOldEnteredNewDescription_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithEnteredOldBlankNewDescription_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithEnteredOldChangedNewDescription_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "New Desc", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithFalseOldTrueNewEnableThisLocation_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", true, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithTrueOldFalseNewEnableThisLocation_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", true, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithFalseOldTrueNewCallControl_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, true, false, false))
    }

    @Test
    fun changesMade_editingLocationWithTrueOldFalseNewCallControl_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, true, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithFalseOldTrueNewPreventingDivertingCalls_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, false, true, false))
    }

    @Test
    fun changesMade_editingLocationWithTrueOldFalseNewPreventingDivertingCalls_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, true, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithFalseOldTrueNewAnswerConfirmation_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, false, false, true))
    }

    @Test
    fun changesMade_editingLocationWithTrueOldFalseNewAnswerConfirmation_returnsTrue() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, true)

        viewModel.editingLocation = editingLocation

        assertTrue(viewModel.changesMade("2223334444", "Description", false, false, false, false))
    }

    @Test
    fun changesMade_editingLocationWithSameForm_returnsFalse() {
        val editingLocation = NextivaAnywhereLocation("2223334444", "Description", false, false, false, false)

        viewModel.editingLocation = editingLocation

        assertFalse(viewModel.changesMade("2223334444", "Description", false, false, false, false))
    }
}