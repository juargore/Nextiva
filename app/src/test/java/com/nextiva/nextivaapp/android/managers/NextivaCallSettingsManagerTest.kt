package com.nextiva.nextivaapp.android.managers

import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LocalSettingListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
import com.nextiva.nextivaapp.android.models.Resource
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.Single
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf
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
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
class NextivaCallSettingsManagerTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var configManager: ConfigManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var callSettingsManager: NextivaCallSettingsManager

    private val mockListItemsListObserver: Observer<Resource<List<BaseListItem>>> = mock()
    private val mockNextivaAnywhereServiceSettings: ServiceSettings = mock()
    private val mockRemoteOfficeServiceSettings: ServiceSettings = mock()

    override fun setup() {
        super.setup()

        hiltRule.inject()

        whenever(sessionManager.nextivaAnywhereServiceSettings).thenReturn(mockNextivaAnywhereServiceSettings)
        whenever(sessionManager.remoteOfficeServiceSettings).thenReturn(mockRemoteOfficeServiceSettings)

        callSettingsManager = NextivaCallSettingsManager(
                ApplicationProvider.getApplicationContext(),
                configManager,
                sessionManager,
                settingsManager,
                userRepository)

        callSettingsManager.listItemsListLiveData.observeForever(mockListItemsListObserver)
    }

    @Test
    fun constructor_setsInitialServiceSettingsValues() {
        assertEquals(mockNextivaAnywhereServiceSettings, callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE])
        assertEquals(mockRemoteOfficeServiceSettings, callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE])
    }

    @Test
    fun getFilteredServiceSettings_setsLoadingResourceValue() {
        callSettingsManager.filteredServiceSettings

        val argumentCaptor = argumentCaptor<Resource<List<BaseListItem>>>()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.LOADING, argumentCaptor.firstValue.status)
    }

    @Test
    fun getFilteredServiceSettings_callsToUserRepositoryToGetFilteredServiceSettings() {
        callSettingsManager.filteredServiceSettings

        val argumentCaptor = argumentCaptor<Array<String>>()
        verify(userRepository).getServiceSettingsFiltered(argumentCaptor.capture())

        assertEquals(Enums.Service.TYPE_DO_NOT_DISTURB, argumentCaptor.firstValue[0])
        assertEquals(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS, argumentCaptor.firstValue[1])
        assertEquals(Enums.Service.TYPE_CALL_FORWARDING_BUSY, argumentCaptor.firstValue[2])
        assertEquals(Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER, argumentCaptor.firstValue[3])
        assertEquals(Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE, argumentCaptor.firstValue[4])
        assertEquals(Enums.Service.TYPE_REMOTE_OFFICE, argumentCaptor.firstValue[5])
        assertEquals(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING, argumentCaptor.firstValue[6])
        assertEquals(Enums.Service.TYPE_BROADWORKS_ANYWHERE, argumentCaptor.firstValue[7])
        assertEquals(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL, argumentCaptor.firstValue[8])
    }

    @Test
    fun getFilteredServiceSettings_success_setsServiceSettingsMap() {
        val mockOldRemoteOfficeServiceSettings: ServiceSettings = mock()
        val mockOldNextivaAnywhereServiceSettings: ServiceSettings = mock()

        val mockRemoteOfficeServiceSettings: ServiceSettings = mock()
        val mockCallForwardingAlwaysServiceSettings: ServiceSettings = mock()
        val mockDoNotDisturbServiceSettings: ServiceSettings = mock()
        val mockSimultaneousRingServiceSettings: ServiceSettings = mock()

        val newHashMap: HashMap<String, ServiceSettings> = hashMapOf()
        newHashMap[Enums.Service.TYPE_REMOTE_OFFICE] = mockRemoteOfficeServiceSettings
        newHashMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = mockCallForwardingAlwaysServiceSettings
        newHashMap[Enums.Service.TYPE_DO_NOT_DISTURB] = mockDoNotDisturbServiceSettings
        newHashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = mockSimultaneousRingServiceSettings

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, newHashMap)))

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE] = mockOldRemoteOfficeServiceSettings
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = mockOldNextivaAnywhereServiceSettings

        callSettingsManager.filteredServiceSettings

        assertEquals(mockRemoteOfficeServiceSettings, callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE])
        assertEquals(mockCallForwardingAlwaysServiceSettings, callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS])
        assertEquals(mockDoNotDisturbServiceSettings, callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB])
        assertEquals(mockSimultaneousRingServiceSettings, callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL])
        assertFalse(callSettingsManager.serviceSettingsMap.containsKey(Enums.Service.TYPE_BROADWORKS_ANYWHERE))
        assertNull(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE])
    }

    @Test
    fun getFilteredServiceSettings_success_setsSuccessResourceValue() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.SUCCESS, argumentCaptor.secondValue.status)
    }

    @Test
    fun getFilteredServiceSettings_error_setsErrorResourceValue() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(false, null)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.ERROR, argumentCaptor.secondValue.status)
    }

    @Test
    fun getFilteredServiceSettings_success_hasBroadworksAnywhereAndEnabled_setsMobilityHeaderListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Mobility", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasBroadworksAnywhereAndEnabledNoLocations_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasBroadworksAnywhereAndEnabledNoActiveLocations_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, arrayListOf(NextivaAnywhereLocation("2223334444", null, false, false, false, false)), null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasBroadworksAnywhereAndEnabledPhoneNumberLocation_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, arrayListOf(NextivaAnywhereLocation("2223334444", null, true, false, false, false)), null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(222) 333-4444", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasBroadworksAnywhereAndEnabledDescriptionLocation_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, arrayListOf(NextivaAnywhereLocation("2223334444", "Desc", true, false, false, false)), null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Desc", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasBroadworksAnywhereAndEnabledMultipleLocations_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, arrayListOf(NextivaAnywhereLocation("2223334444", "Desc", true, false, false, false), NextivaAnywhereLocation("3334445555", "Desc", true, false, false, false)), null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Multiple Locations", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasBroadworksAnywhereButDisabled_setsCorrectListItems() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(false)

        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        // List will always contain the Call Options section and list items, which are 3 items
        assertEquals(5, argumentCaptor.secondValue.data!!.size)
    }

    @Test
    fun getFilteredServiceSettings_success_hasRemoteOfficeAndEnabled_setsMobilityHeaderListItem() {
        whenever(configManager.remoteOfficeEnabled).thenReturn(true)

        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Mobility", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasRemoteOfficeAndEnabledActiveSetting_setsRemoteOfficeListItem() {
        whenever(configManager.remoteOfficeEnabled).thenReturn(true)

        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", true, null, null, "2223334444", null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_REMOTE_OFFICE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Remote Office", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(222) 333-4444", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasRemoteOfficeAndEnabledInactiveSetting_setsRemoteOfficeListItem() {
        whenever(configManager.remoteOfficeEnabled).thenReturn(true)

        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_REMOTE_OFFICE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Remote Office", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasRemoteOfficeButDisabled_setsRemoteOfficeListItem() {
        whenever(configManager.remoteOfficeEnabled).thenReturn(false)

        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", true, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        // List will always contain the Call Options section and list items, which are 3 items
        assertEquals(5, argumentCaptor.secondValue.data!!.size)
    }

    @Test
    fun getFilteredServiceSettings_success_hasSimultaneousRing_setsMobilityHeaderListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Mobility", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasSimultaneousRingNoLocations_setsSimultaneousRingListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Simultaneous Ring", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasSimultaneousRingNoActiveLocations_setsSimultaneousRingListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", true, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Simultaneous Ring", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasSimultaneousRingPhoneNumberLocation_setsSimultaneousRingListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", true, null, null, null, null, null, null, null, null, arrayListOf(SimultaneousRingLocation("4443332222", null)))

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Simultaneous Ring", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(444) 333-2222", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasSimultaneousRingMultipleLocations_setsSimultaneousRingListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", true, null, null, null, null, null, null, null, null, arrayListOf(SimultaneousRingLocation("4443332222", null), SimultaneousRingLocation("5554443333", null)))

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Simultaneous Ring", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Multiple Locations", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingAlways_setsForwardingHeaderListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = ServiceSettings("Call Forwarding Always", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingAlwaysActiveSetting_setsCallForwardingAlwaysListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = ServiceSettings("Call Forwarding Always", "uri", true, null, null, null, "5556667777", null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Always", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(555) 666-7777", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingAlwaysInactiveSetting_setsCallForwardingAlwaysListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = ServiceSettings("Call Forwarding Always", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Always", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenBusy_setsForwardingHeaderListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY] = ServiceSettings("Call Forwarding Busy", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenBusyActiveSetting_setsCallForwardingWhenBusyListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY] = ServiceSettings("Call Forwarding Busy", "uri", true, null, null, null, "5556667777", null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Busy", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(555) 666-7777", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenBusyInactiveSetting_setsCallForwardingWhenBusyListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY] = ServiceSettings("Call Forwarding Busy", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Busy", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenUnanswered_setsForwardingHeaderListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER] = ServiceSettings("Call Forwarding No Answer", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenUnansweredActiveSetting_setsCallForwardingWhenUnansweredListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER] = ServiceSettings("Call Forwarding No Answer", "uri", true, null, null, null, "5556667777", null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Unanswered", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(555) 666-7777", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenUnansweredInactiveSetting_setsCallForwardingWhenUnansweredListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER] = ServiceSettings("Call Forwarding No Answer", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Unanswered", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenUnreachable_setsForwardingHeaderListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE] = ServiceSettings("Call Forwarding Not Reachable", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenUnreachableActiveSetting_setsCallForwardingWhenUnreachableListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE] = ServiceSettings("Call Forwarding Not Reachable", "uri", true, null, null, null, "5556667777", null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Unreachable", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(555) 666-7777", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallForwardingWhenUnreachableInactiveSetting_setsCallForwardingWhenUnreachableListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE] = ServiceSettings("Call Forwarding Not Reachable", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Unreachable", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasDoNotDisturb_setsRoutingHeaderListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_DO_NOT_DISTURB] = ServiceSettings("Do Not Disturb", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Routing", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasDoNotDisturbActiveSetting_setsDoNotDisturbListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_DO_NOT_DISTURB] = ServiceSettings("Do Not Disturb", "uri", true, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_DO_NOT_DISTURB], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Do Not Disturb", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("On", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasDoNotDisturbInactiveSetting_setsDoNotDisturbListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_DO_NOT_DISTURB] = ServiceSettings("Do Not Disturb", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_DO_NOT_DISTURB], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Do Not Disturb", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallerIdBlocking_setsCallIdentificationHeaderListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = ServiceSettings("Calling Line ID Delivery Blocking", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Identification", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallerIdBlockingActiveSetting_setsCallerIdBlockingListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = ServiceSettings("Calling Line ID Delivery Blocking", "uri", true, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Block My Caller ID", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("On", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_hasCallerIdBlockingInactiveSetting_setsCallerIdBlockingListItem() {
        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = ServiceSettings("Calling Line ID Delivery Blocking", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Block My Caller ID", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_setsCallOptionsHeaderListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.NONE)

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Options", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.secondValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.secondValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun getFilteredServiceSettings_success_noDialingServiceSelected_setsDialingServiceListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.NONE)

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).title)
        assertNull((argumentCaptor.secondValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_voipDialingServiceSelected_setsDialingServiceListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.VOIP)

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Internet Call (VoIP)", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_callBackDialingServiceSelected_setsDialingServiceListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Call Back", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_callThroughDialingServiceSelected_setsDialingServiceListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Call Through", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_thisPhoneDialingServiceSelected_setsDialingServiceListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.THIS_PHONE)

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).title)
        assertEquals("This Phone", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_alwaysAskDialingServiceSelected_setsDialingServiceListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.ALWAYS_ASK)

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Always Ask", (argumentCaptor.secondValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_populatedPhoneNumber_setsThisPhoneNumberListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.phoneNumber).thenReturn("3335557777")

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![2], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.THIS_PHONE_NUMBER, (argumentCaptor.secondValue.data!![2] as LocalSettingListItem).settingKey)
        assertEquals("This Mobile Phone Number", (argumentCaptor.secondValue.data!![2] as LocalSettingListItem).title)
        assertEquals("(333) 555-7777", (argumentCaptor.secondValue.data!![2] as LocalSettingListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_emptyPhoneNumber_setsThisPhoneNumberListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.secondValue.data!![2], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.THIS_PHONE_NUMBER, (argumentCaptor.secondValue.data!![2] as LocalSettingListItem).settingKey)
        assertEquals("This Mobile Phone Number", (argumentCaptor.secondValue.data!![2] as LocalSettingListItem).title)
        assertEquals("Configure This Mobile Phone Number", (argumentCaptor.secondValue.data!![2] as LocalSettingListItem).subTitle)
    }

    @Test
    fun getFilteredServiceSettings_success_setsCorrectListItemOrder() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)
        whenever(configManager.remoteOfficeEnabled).thenReturn(true)

        val hashMap: HashMap<String, ServiceSettings> = HashMap()
        hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = ServiceSettings("Call Forwarding Always", "uri", null, null, null, null, null, null, null, null, null, null)
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY] = ServiceSettings("Call Forwarding Busy", "uri", null, null, null, null, null, null, null, null, null, null)
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER] = ServiceSettings("Call Forwarding No Answer", "uri", null, null, null, null, null, null, null, null, null, null)
        hashMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE] = ServiceSettings("Call Forwarding Not Reachable", "uri", null, null, null, null, null, null, null, null, null, null)
        hashMap[Enums.Service.TYPE_DO_NOT_DISTURB] = ServiceSettings("Do Not Disturb", "uri", null, null, null, null, null, null, null, null, null, null)
        hashMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = ServiceSettings("Calling Line ID Delivery Blocking", "uri", null, null, null, null, null, null, null, null, null, null)
        hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", null, null, null, null, null, null, null, null, null, null)
        hashMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", null, null, null, null, null, null, null, null, null, null)

        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, hashMap)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        // --------------------------------------------------------------------------------------------
        // Verify Mobility section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Mobility", (argumentCaptor.secondValue.data!![0] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Nextiva Anywhere list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.secondValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Remote Office list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![2], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_REMOTE_OFFICE], (argumentCaptor.secondValue.data!![2] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Simultaneous Ring list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![3], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.secondValue.data!![3] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Forwarding section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![4], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.secondValue.data!![4] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Forwarding Always list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![5], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS], (argumentCaptor.secondValue.data!![5] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Forwarding When Busy list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![6], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY], (argumentCaptor.secondValue.data!![6] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Forwarding When Unanswered list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![7], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER], (argumentCaptor.secondValue.data!![7] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Forwarding When Unreachable list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![8], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE], (argumentCaptor.secondValue.data!![8] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Routing section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![9], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Routing", (argumentCaptor.secondValue.data!![9] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Do Not Disturb list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![10], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_DO_NOT_DISTURB], (argumentCaptor.secondValue.data!![10] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Identification section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![11], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Identification", (argumentCaptor.secondValue.data!![11] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Block My Caller ID list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![12], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(hashMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING], (argumentCaptor.secondValue.data!![12] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Options section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![13], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Options", (argumentCaptor.secondValue.data!![13] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Dialing Service list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![14], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.secondValue.data!![14] as LocalSettingListItem).settingKey)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify This Phone Number list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.secondValue.data!![15], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.THIS_PHONE_NUMBER, (argumentCaptor.secondValue.data!![15] as LocalSettingListItem).settingKey)
        // --------------------------------------------------------------------------------------------
    }

    @Test
    fun getFilteredServiceSettings_failure_setsErrorResourceValue() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(false, null)))

        callSettingsManager.filteredServiceSettings

        val argumentCaptor = argumentCaptor<Resource<List<BaseListItem>>>()

        // Two times because it is changed when it starts the call and when it finishes the call
        verify(mockListItemsListObserver, times(2)).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Net.StatusTypes.ERROR, argumentCaptor.secondValue.status)
    }

    @Test
    fun clearServiceSettings_clearsServiceSettings() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)
        assertEquals(2, callSettingsManager.serviceSettingsMap.size)

        callSettingsManager.clearServiceSettings()

        assertEquals(0, callSettingsManager.serviceSettingsMap.size)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_setsCallOptionsHeaderListItem() {
        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Options", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_noDialingServiceSelected_setsDialingServiceListItem() {
        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Internet Call (VoIP)", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_voipDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.VOIP)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Internet Call (VoIP)", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_callBackDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Call Back", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_callThroughDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Call Through", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_thisPhoneDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.THIS_PHONE)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("This Phone", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_alwaysAskDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.ALWAYS_ASK)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Always Ask", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_emptyPhoneNumber_setsThisPhoneNumberListItem() {
        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![2], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.THIS_PHONE_NUMBER, (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).settingKey)
        assertEquals("This Mobile Phone Number", (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).title)
        assertEquals("Configure This Mobile Phone Number", (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_emptyServiceSettings_populatedPhoneNumber_setsThisPhoneNumberListItem() {
        whenever(userRepository.getServiceSettingsFiltered(any())).thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap())))
        whenever(settingsManager.phoneNumber).thenReturn("3335557777")

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![2], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.THIS_PHONE_NUMBER, (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).settingKey)
        assertEquals("This Mobile Phone Number", (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).title)
        assertEquals("(333) 555-7777", (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_clearsNullValues() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB] = null
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = mockNextivaAnywhereServiceSettings

        reset(mockListItemsListObserver)

        callSettingsManager.processServiceSettings()

        assertFalse(callSettingsManager.serviceSettingsMap.containsKey(Enums.Service.TYPE_DO_NOT_DISTURB))
        assertTrue(callSettingsManager.serviceSettingsMap.containsKey(Enums.Service.TYPE_BROADWORKS_ANYWHERE))
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasBroadworksAnywhereAndEnabled_setsMobilityHeaderListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Mobility", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasBroadworksAnywhereAndEnabledNoLocations_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasBroadworksAnywhereAndEnabledNoActiveLocations_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, arrayListOf(NextivaAnywhereLocation("2223334444", null, false, false, false, false)), null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasBroadworksAnywhereAndEnabledPhoneNumberLocation_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, arrayListOf(NextivaAnywhereLocation("2223334444", null, true, false, false, false)), null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(222) 333-4444", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasBroadworksAnywhereAndEnabledDescriptionLocation_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, arrayListOf(NextivaAnywhereLocation("2223334444", "Desc", true, false, false, false)), null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Desc", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasBroadworksAnywhereAndEnabledMultipleLocations_setsBroadworksAnywhereListItem() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, arrayListOf(NextivaAnywhereLocation("2223334444", "Desc", true, false, false, false), NextivaAnywhereLocation("3334445555", "Desc", true, false, false, false)), null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Nextiva Anywhere", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Multiple Locations", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasBroadworksAnywhereButDisabled_setsCorrectListItems() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(false)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        // List will always contain the Call Options section and list items, which are 3 items
        assertEquals(5, argumentCaptor.firstValue.data!!.size)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasRemoteOfficeAndEnabled_setsMobilityHeaderListItem() {
        whenever(configManager.remoteOfficeEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Mobility", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasRemoteOfficeAndEnabledActiveSetting_setsRemoteOfficeListItem() {
        whenever(configManager.remoteOfficeEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", true, null, null, "2223334444", null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Remote Office", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(222) 333-4444", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasRemoteOfficeAndEnabledInactiveSetting_setsRemoteOfficeListItem() {
        whenever(configManager.remoteOfficeEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Remote Office", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasRemoteOfficeButDisabled_setsRemoteOfficeListItem() {
        whenever(configManager.remoteOfficeEnabled).thenReturn(false)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", true, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        // List will always contain the Call Options section and list items, which are 3 items
        assertEquals(5, argumentCaptor.firstValue.data!!.size)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasSimultaneousRing_setsMobilityHeaderListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Mobility", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasSimultaneousRingNoLocations_setsSimultaneousRingListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Simultaneous Ring", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasSimultaneousRingNoActiveLocations_setsSimultaneousRingListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", true, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Simultaneous Ring", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasSimultaneousRingPhoneNumberLocation_setsSimultaneousRingListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", true, null, null, null, null, null, null, null, null, arrayListOf(SimultaneousRingLocation("4443332222", null)))

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Simultaneous Ring", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(444) 333-2222", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasSimultaneousRingMultipleLocations_setsSimultaneousRingListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", true, null, null, null, null, null, null, null, null, arrayListOf(SimultaneousRingLocation("4443332222", null), SimultaneousRingLocation("5554443333", null)))

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Simultaneous Ring", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Multiple Locations", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingAlways_setsForwardingHeaderListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = ServiceSettings("Call Forwarding Always", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingAlwaysActiveSetting_setsCallForwardingAlwaysListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = ServiceSettings("Call Forwarding Always", "uri", true, null, null, null, "5556667777", null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Always", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(555) 666-7777", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingAlwaysInactiveSetting_setsCallForwardingAlwaysListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = ServiceSettings("Call Forwarding Always", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Always", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenBusy_setsForwardingHeaderListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY] = ServiceSettings("Call Forwarding Busy", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenBusyActiveSetting_setsCallForwardingWhenBusyListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY] = ServiceSettings("Call Forwarding Busy", "uri", true, null, null, null, "5556667777", null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Busy", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(555) 666-7777", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenBusyInactiveSetting_setsCallForwardingWhenBusyListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY] = ServiceSettings("Call Forwarding Busy", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Busy", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenUnanswered_setsForwardingHeaderListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER] = ServiceSettings("Call Forwarding No Answer", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenUnansweredActiveSetting_setsCallForwardingWhenUnansweredListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER] = ServiceSettings("Call Forwarding No Answer", "uri", true, null, null, null, "5556667777", null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Unanswered", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(555) 666-7777", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenUnansweredInactiveSetting_setsCallForwardingWhenUnansweredListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER] = ServiceSettings("Call Forwarding No Answer", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Unanswered", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenUnreachable_setsForwardingHeaderListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE] = ServiceSettings("Call Forwarding Not Reachable", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenUnreachableActiveSetting_setsCallForwardingWhenUnreachableListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE] = ServiceSettings("Call Forwarding Not Reachable", "uri", true, null, null, null, "5556667777", null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Unreachable", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("(555) 666-7777", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallForwardingWhenUnreachableInactiveSetting_setsCallForwardingWhenUnreachableListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE] = ServiceSettings("Call Forwarding Not Reachable", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("When Unreachable", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasDoNotDisturb_setsRoutingHeaderListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB] = ServiceSettings("Do Not Disturb", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Routing", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasDoNotDisturbActiveSetting_setsDoNotDisturbListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB] = ServiceSettings("Do Not Disturb", "uri", true, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Do Not Disturb", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("On", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasDoNotDisturbInactiveSetting_setsDoNotDisturbListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB] = ServiceSettings("Do Not Disturb", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Do Not Disturb", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallerIdBlocking_setsCallIdentificationHeaderListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = ServiceSettings("Calling Line ID Delivery Blocking", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Identification", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallerIdBlockingActiveSetting_setsCallerIdBlockingListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = ServiceSettings("Calling Line ID Delivery Blocking", "uri", true, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Block My Caller ID", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("On", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_hasCallerIdBlockingInactiveSetting_setsCallerIdBlockingListItem() {
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = ServiceSettings("Calling Line ID Delivery Blocking", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        assertEquals("Block My Caller ID", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).title)
        assertEquals("Off", (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_setsCallOptionsHeaderListItem() {
        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Options", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        assertNull((argumentCaptor.firstValue.data!![0] as HeaderListItem).baseListItemsList)
        assertFalse((argumentCaptor.firstValue.data!![0] as HeaderListItem).isClickable)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_noDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.NONE)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertNull((argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_voipDialingServiceSelected_setsDialingServiceListItem() {
        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Internet Call (VoIP)", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_callBackDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Call Back", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_callThroughDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Call Through", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_thisPhoneDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.THIS_PHONE)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("This Phone", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_alwaysAskDialingServiceSelected_setsDialingServiceListItem() {
        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.ALWAYS_ASK)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).settingKey)
        assertEquals("Dialing Service", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).title)
        assertEquals("Always Ask", (argumentCaptor.firstValue.data!![1] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_populatedPhoneNumber_setsThisPhoneNumberListItem() {
        whenever(settingsManager.phoneNumber).thenReturn("3335557777")

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![2], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.THIS_PHONE_NUMBER, (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).settingKey)
        assertEquals("This Mobile Phone Number", (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).title)
        assertEquals("(333) 555-7777", (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_emptyPhoneNumber_setsThisPhoneNumberListItem() {
        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        assertThat(argumentCaptor.firstValue.data!![2], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.THIS_PHONE_NUMBER, (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).settingKey)
        assertEquals("This Mobile Phone Number", (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).title)
        assertEquals("Configure This Mobile Phone Number", (argumentCaptor.firstValue.data!![2] as LocalSettingListItem).subTitle)
    }

    @Test
    fun processServiceSettings_populatedServiceSettings_setsCorrectListItemOrder() {
        whenever(configManager.nextivaAnywhereEnabled).thenReturn(true)
        whenever(configManager.remoteOfficeEnabled).thenReturn(true)

        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE] = ServiceSettings("BroadWorks Anywhere", "uri", null, null, null, null, null, null, null, null, null, null)
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS] = ServiceSettings("Call Forwarding Always", "uri", null, null, null, null, null, null, null, null, null, null)
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY] = ServiceSettings("Call Forwarding Busy", "uri", null, null, null, null, null, null, null, null, null, null)
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER] = ServiceSettings("Call Forwarding No Answer", "uri", null, null, null, null, null, null, null, null, null, null)
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE] = ServiceSettings("Call Forwarding Not Reachable", "uri", null, null, null, null, null, null, null, null, null, null)
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB] = ServiceSettings("Do Not Disturb", "uri", null, null, null, null, null, null, null, null, null, null)
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = ServiceSettings("Calling Line ID Delivery Blocking", "uri", null, null, null, null, null, null, null, null, null, null)
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL] = ServiceSettings("Simultaneous Ring Personal", "uri", null, null, null, null, null, null, null, null, null, null)
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE] = ServiceSettings("Remote Office", "uri", null, null, null, null, null, null, null, null, null, null)

        callSettingsManager.processServiceSettings()

        val argumentCaptor: KArgumentCaptor<Resource<List<BaseListItem>>> = argumentCaptor()
        verify(mockListItemsListObserver).onChanged(argumentCaptor.capture())

        // --------------------------------------------------------------------------------------------
        // Verify Mobility section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![0], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Mobility", (argumentCaptor.firstValue.data!![0] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Nextiva Anywhere list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![1], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_BROADWORKS_ANYWHERE], (argumentCaptor.firstValue.data!![1] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Remote Office list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![2], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_REMOTE_OFFICE], (argumentCaptor.firstValue.data!![2] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Simultaneous Ring list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![3], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL], (argumentCaptor.firstValue.data!![3] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Forwarding section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![4], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Forwarding", (argumentCaptor.firstValue.data!![4] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Forwarding Always list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![5], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_ALWAYS], (argumentCaptor.firstValue.data!![5] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Forwarding When Busy list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![6], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_BUSY], (argumentCaptor.firstValue.data!![6] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Forwarding When Unanswered list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![7], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER], (argumentCaptor.firstValue.data!![7] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Forwarding When Unreachable list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![8], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE], (argumentCaptor.firstValue.data!![8] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Routing section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![9], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Routing", (argumentCaptor.firstValue.data!![9] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Do Not Disturb list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![10], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_DO_NOT_DISTURB], (argumentCaptor.firstValue.data!![10] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Identification section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![11], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Identification", (argumentCaptor.firstValue.data!![11] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Block My Caller ID list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![12], IsInstanceOf(ServiceSettingsListItem::class.java))
        assertEquals(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING], (argumentCaptor.firstValue.data!![12] as ServiceSettingsListItem).serviceSettings)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Call Options section header
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![13], IsInstanceOf(HeaderListItem::class.java))
        assertEquals("Call Options", (argumentCaptor.firstValue.data!![13] as HeaderListItem).data.title)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify Dialing Service list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![14], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.DIALING_SERVICE, (argumentCaptor.firstValue.data!![14] as LocalSettingListItem).settingKey)
        // --------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------
        // Verify This Phone Number list item
        // --------------------------------------------------------------------------------------------
        assertThat(argumentCaptor.firstValue.data!![15], IsInstanceOf(LocalSettingListItem::class.java))
        assertEquals(SharedPreferencesManager.THIS_PHONE_NUMBER, (argumentCaptor.firstValue.data!![15] as LocalSettingListItem).settingKey)
        // --------------------------------------------------------------------------------------------
    }

    @Test
    fun putServiceSettings_nullValue_clearsServiceSettingValue() {
        val mockServiceSettings: ServiceSettings = mock()
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = mockServiceSettings

        callSettingsManager.putServiceSetting(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING, null)

        assertNull(callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING])
    }

    @Test
    fun putServiceSettings_nonNullValue_setsServiceSettingValue() {
        val mockServiceSettings: ServiceSettings = mock()
        callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING] = null

        callSettingsManager.putServiceSetting(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING, mockServiceSettings)

        assertEquals(mockServiceSettings, callSettingsManager.serviceSettingsMap[Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING])
    }
}