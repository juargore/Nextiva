package com.nextiva.nextivaapp.android.viewmodels

import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.api.ContactManagementPolicyRepository
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.AuthenticationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MobileConfigRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ProductsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.FeatureAccessCodes
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.models.SingleEvent
import com.nextiva.nextivaapp.android.models.UserDetails
import com.nextiva.nextivaapp.android.models.net.platform.Products
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
class LoginViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var roomsDbManager: RoomsDbManager

    @Inject
    lateinit var settingsManager: SettingsManager
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var pushNotificationManager: PushNotificationManager
    @Inject
    lateinit var mobileConfigRepository: MobileConfigRepository
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var authenticationServiceRepository: AuthenticationRepository
    @Inject
    lateinit var callManagementRepository: CallManagementRepository
    @Inject
    lateinit var schedulerProvider: SchedulerProvider
    @Inject
    lateinit var xmppConnectionActionManager: XMPPConnectionActionManager
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferencesManager
    @Inject
    lateinit var platformRepository: PlatformRepository
    @Inject
    lateinit var smsManagementRepository: SmsManagementRepository
    @Inject
    lateinit var productsRepository: ProductsRepository
    @Inject
    lateinit var conversationRepository: ConversationRepository
    @Inject
    lateinit var presenceRepository: PresenceRepository
    @Inject
    lateinit var contactManagementPolicyRepository: ContactManagementPolicyRepository
    @Inject
    lateinit var configManager: ConfigManager
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var netManager: NetManager

    private val mockLoginFailedObserver: Observer<SingleEvent<Boolean>> = mock()
    private val mockFinalizeLoginProcessObserver: Observer<Boolean> = mock()

    private lateinit var viewModel: LoginViewModel

    override fun setup() {
        super.setup()
        hiltRule.inject()

        viewModel = LoginViewModel(ApplicationProvider.getApplicationContext(),
                dbManager,
                roomsDbManager,
                sessionManager,
                settingsManager,
                pushNotificationManager,
                mobileConfigRepository,
                userRepository,
                authenticationServiceRepository,
                callManagementRepository,
                schedulerProvider,
                xmppConnectionActionManager,
                sharedPreferenceManager,
                platformRepository,
                smsManagementRepository,
                productsRepository,
                conversationRepository,
                presenceRepository,
                contactManagementPolicyRepository,
                configManager,
                notificationManager,
                netManager)

        viewModel.finalizeLoginProcessLiveData.observeForever(mockFinalizeLoginProcessObserver)
        viewModel.loginFailedLiveData.observeForever(mockLoginFailedObserver)
    }

    @Test
    fun onCleared_clearsCompositeDisposable() {
        viewModel.compositeDisposable.add(Completable.never().subscribe())
        assertEquals(1, viewModel.compositeDisposable.size())
        viewModel.onCleared()
        assertEquals(0, viewModel.compositeDisposable.size())
    }

    @Test
    fun hasLicenseOrPhoneNumber_hasNumberNoLicense_returnsTrue() {
        whenever(sessionManager.isLicenseApproved).thenReturn(false)
        whenever(settingsManager.phoneNumber).thenReturn("1112223333")

        assertTrue(viewModel.hasLicenseOrPhoneNumber())
    }

    @Test
    fun hasLicenseOrPhoneNumber_NoNumberHasLicense_returnsTrue() {
        whenever(sessionManager.isLicenseApproved).thenReturn(true)
        whenever(settingsManager.phoneNumber).thenReturn(null)

        assertTrue(viewModel.hasLicenseOrPhoneNumber())
    }

    @Test
    fun hasLicenseOrPhoneNumber_NoNumberNoLicense_returnsFalse() {
        whenever(sessionManager.isLicenseApproved).thenReturn(false)
        whenever(settingsManager.phoneNumber).thenReturn(null)

        assertFalse(viewModel.hasLicenseOrPhoneNumber())
    }

    @Test
    fun hasLicenseOrPhoneNumber_hasNumberHasLicense_returnsTrue() {
        whenever(sessionManager.isLicenseApproved).thenReturn(true)
        whenever(settingsManager.phoneNumber).thenReturn("1112223333")

        assertTrue(viewModel.hasLicenseOrPhoneNumber())
    }

    @Test
    fun processExistingDialingServiceSetting_callbackConflict_setsDialingServiceToVoip() {

        val mockServiceSettings1: ServiceSettings = mock()
        val mockServiceSettings2: ServiceSettings = mock()

        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_BACK)
        whenever(sessionManager.remoteOfficeServiceSettings).thenReturn(mockServiceSettings1)
        whenever(sessionManager.nextivaAnywhereServiceSettings).thenReturn(mockServiceSettings2)
        whenever(sessionManager.getIsCallBackEnabled(mockServiceSettings1, mockServiceSettings2)).thenReturn(false)

        viewModel.processExistingDialingServiceSetting()

        verify(settingsManager).dialingService = Enums.Service.DialingServiceTypes.VOIP
    }

    @Test
    fun processExistingDialingServiceSetting_callThroughConflict_setsDialingServiceToVoip() {

        val mockServiceSettings1: ServiceSettings = mock()

        whenever(settingsManager.dialingService).thenReturn(Enums.Service.DialingServiceTypes.CALL_THROUGH)
        whenever(settingsManager.phoneNumber).thenReturn("1112223333")
        whenever(sessionManager.nextivaAnywhereServiceSettings).thenReturn(mockServiceSettings1)
        whenever(sessionManager.getIsCallThroughEnabled(mockServiceSettings1, "1112223333")).thenReturn(false)

        viewModel.processExistingDialingServiceSetting()

        verify(settingsManager).dialingService = Enums.Service.DialingServiceTypes.VOIP
    }

    @Test
    fun processExistingDialingServiceSetting_noConflict_doesntSetDialingService() {
        viewModel.processExistingDialingServiceSetting()

        verify(settingsManager, never()).dialingService = Enums.Service.DialingServiceTypes.VOIP
    }

    @Test
    fun authenticateUser_doesNotContainHost_appendsNextivaHost() {
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")

        viewModel.authenticateUser("name", "password")

        verify(authenticationServiceRepository).authenticateUser("name@nextiva.com", "password")
    }

    @Test
    fun authenticateUser_containsHost_doesNotAppendsNextivaHost() {
        viewModel.authenticateUser("name@fake.com", "password")

        verify(authenticationServiceRepository).authenticateUser("name@fake.com", "password")
    }

    @Test
    fun authenticateUser_callsToAuthenticateUser() {
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")

        viewModel.authenticateUser("username", "password")

        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")
    }

    @Test
    fun authenticateUser_successLoggingIn_allMethodsCalled() {
        val mockUserDetails: UserDetails = mock()
        val emptyMap: Map<String, ServiceSettings> = emptyMap()
        val featureAccessCodes = FeatureAccessCodes(null)

        whenever(authenticationServiceRepository.authenticateUser("username@nextiva.com", "password"))
                .thenReturn(Single.just(RxEvents.AuthenticationResponseEvent(true, true)))
        whenever(mobileConfigRepository.mobileConfig)
                .thenReturn(Single.just(RxEvents.MobileConfigResponseEvent(true)))
        whenever(userRepository.userDetails)
                .thenReturn(Single.just(RxEvents.UserDetailsResponseEvent(true, mockUserDetails)))
        whenever(userRepository.featureAccessCodes)
                .thenReturn(Single.just(RxEvents.FeatureAccessCodesResponseEvent(true, featureAccessCodes)))
        whenever(userRepository.getServiceSettingsFiltered(arrayOf(Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE)))
                .thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap)))
        whenever(productsRepository.getProducts())
                .thenReturn(Single.just(RxEvents.ProductsResponseEvent(true, Products(ArrayList()))))
        whenever(platformRepository.getFeatureFlags())
                .thenReturn(Single.just(RxEvents.FeatureFlagsResponseEvent(true)))
        whenever(platformRepository.getAccountInformation())
                .thenReturn(Single.just(RxEvents.AccountInformationResponseEvent(true)))
        whenever(userRepository.voicemailMessageSummary)
                .thenReturn(Single.just(RxEvents.VoicemailMessageSummaryResponseEvent(true, 0, 0)))
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME))
                .thenReturn("@nextiva.com")
        whenever(productsRepository.refreshLicenses())
                .thenReturn(Single.just(RxEvents.PhoneInformationResponseEvent(true)))
        whenever(smsManagementRepository.getUsersTeams())
            .thenReturn(Single.just(RxEvents.BaseResponseEvent(true)))
        whenever(sessionManager.isNextivaConnectEnabled).thenReturn(true)
        whenever(contactManagementPolicyRepository.getContactManagementPrivilege()).thenReturn(Single.never())


        viewModel.authenticateUser("username", "password")

        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")
        verify(mobileConfigRepository).mobileConfig
        verify(userRepository).userDetails
        verify(userRepository).featureAccessCodes
        verify(userRepository).getServiceSettingsFiltered(arrayOf(Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE))
        //verify(userRepository).voicemailMessageSummary
        //verify(callManagementRepository).allCallLogEntries
        verify(pushNotificationManager).enableFCM()
        verify(mockFinalizeLoginProcessObserver).onChanged(true)
    }

    @Test
    fun authenticateUser_badAuthenticationResponseEvent_errorThrown() {
        whenever(authenticationServiceRepository.authenticateUser("username@nextiva.com", "password"))
                .thenReturn(Single.just(RxEvents.AuthenticationResponseEvent(false, false)))
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")

        viewModel.authenticateUser("username", "password")

        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")

        val argumentCaptor: KArgumentCaptor<SingleEvent<Boolean>> = argumentCaptor()
        verify(mockLoginFailedObserver).onChanged(argumentCaptor.capture())
        assertTrue(argumentCaptor.firstValue.peekContent())

        verify(sessionManager).rememberPassword = false
        verify(dbManager).clearAndResetAllTables()
    }

    @Test
    fun authenticateUser_badMobileConfigResponseEvent_errorThrown() {
        whenever(authenticationServiceRepository.authenticateUser("username@nextiva.com", "password"))
                .thenReturn(Single.just(RxEvents.AuthenticationResponseEvent(true, true)))
        whenever(mobileConfigRepository.mobileConfig)
                .thenReturn(Single.just(RxEvents.MobileConfigResponseEvent(false)))
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")

        viewModel.authenticateUser("username", "password")

        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")
        verify(mobileConfigRepository).mobileConfig

        val argumentCaptor: KArgumentCaptor<SingleEvent<Boolean>> = argumentCaptor()
        verify(mockLoginFailedObserver).onChanged(argumentCaptor.capture())
        assertTrue(argumentCaptor.firstValue.peekContent())

        verify(sessionManager).rememberPassword = false
        verify(dbManager).clearAndResetAllTables()
    }

    @Test
    fun authenticateUser_badUserDetailsResponseEvent_errorThrown() {
        val mockUserDetails: UserDetails = mock()

        whenever(authenticationServiceRepository.authenticateUser("username@nextiva.com", "password"))
                .thenReturn(Single.just(RxEvents.AuthenticationResponseEvent(true, true)))
        whenever(mobileConfigRepository.mobileConfig)
                .thenReturn(Single.just(RxEvents.MobileConfigResponseEvent(true)))
        whenever(userRepository.userDetails)
                .thenReturn(Single.just(RxEvents.UserDetailsResponseEvent(false, mockUserDetails)))
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")

        viewModel.authenticateUser("username", "password")

        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")
        verify(mobileConfigRepository).mobileConfig
        verify(userRepository).userDetails

        val argumentCaptor: KArgumentCaptor<SingleEvent<Boolean>> = argumentCaptor()
        verify(mockLoginFailedObserver).onChanged(argumentCaptor.capture())
        assertTrue(argumentCaptor.firstValue.peekContent())

        verify(sessionManager).rememberPassword = false
        verify(dbManager).clearAndResetAllTables()
    }

    @Test
    fun authenticateUser_badFeatureAccessCodesResponseEvent_errorThrown() {
        val mockUserDetails: UserDetails = mock()
        val featureAccessCodes = FeatureAccessCodes(null)

        whenever(authenticationServiceRepository.authenticateUser("username@nextiva.com", "password"))
                .thenReturn(Single.just(RxEvents.AuthenticationResponseEvent(true, true)))
        whenever(mobileConfigRepository.mobileConfig)
                .thenReturn(Single.just(RxEvents.MobileConfigResponseEvent(true)))
        whenever(userRepository.userDetails)
                .thenReturn(Single.just(RxEvents.UserDetailsResponseEvent(true, mockUserDetails)))
        whenever(userRepository.featureAccessCodes)
                .thenReturn(Single.just(RxEvents.FeatureAccessCodesResponseEvent(false, featureAccessCodes)))
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")

        viewModel.authenticateUser("username", "password")

        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")
        verify(mobileConfigRepository).mobileConfig
        verify(userRepository).userDetails
        verify(userRepository).featureAccessCodes

        val argumentCaptor: KArgumentCaptor<SingleEvent<Boolean>> = argumentCaptor()
        verify(mockLoginFailedObserver).onChanged(argumentCaptor.capture())
        assertTrue(argumentCaptor.firstValue.peekContent())

        verify(sessionManager).rememberPassword = false
        verify(dbManager).clearAndResetAllTables()
    }

    @Test
    fun authenticateUser_badServiceSettingsMapResponseEvent_errorThrown() {
        val mockUserDetails: UserDetails = mock()
        val emptyMap: Map<String, ServiceSettings> = emptyMap()
        val featureAccessCodes = FeatureAccessCodes(null)

        whenever(authenticationServiceRepository.authenticateUser("username@nextiva.com", "password"))
                .thenReturn(Single.just(RxEvents.AuthenticationResponseEvent(true, true)))
        whenever(mobileConfigRepository.mobileConfig)
                .thenReturn(Single.just(RxEvents.MobileConfigResponseEvent(true)))
        whenever(userRepository.userDetails)
                .thenReturn(Single.just(RxEvents.UserDetailsResponseEvent(true, mockUserDetails)))
        whenever(userRepository.featureAccessCodes)
                .thenReturn(Single.just(RxEvents.FeatureAccessCodesResponseEvent(true, featureAccessCodes)))
        whenever(userRepository.getServiceSettingsFiltered(arrayOf(Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE)))
                .thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(false, emptyMap)))
        whenever(productsRepository.getProducts())
                .thenReturn(Single.just(RxEvents.ProductsResponseEvent(true, Products(ArrayList()))))
        whenever(platformRepository.getFeatureFlags())
                .thenReturn(Single.just(RxEvents.FeatureFlagsResponseEvent(true)))
        whenever(platformRepository.getAccountInformation())
                .thenReturn(Single.just(RxEvents.AccountInformationResponseEvent(true)))
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")
        whenever(productsRepository.refreshLicenses())
                .thenReturn(Single.just(RxEvents.PhoneInformationResponseEvent(true)))
        whenever(smsManagementRepository.getUsersTeams())
            .thenReturn(Single.just(RxEvents.BaseResponseEvent(true)))
        whenever(sessionManager.isNextivaConnectEnabled).thenReturn(true)
        whenever(contactManagementPolicyRepository.getContactManagementPrivilege()).thenReturn(Single.never())

        viewModel.authenticateUser("username", "password")

        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")
        verify(mobileConfigRepository).mobileConfig
        verify(userRepository).userDetails
        verify(userRepository).featureAccessCodes
        verify(userRepository).getServiceSettingsFiltered(arrayOf(Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE))

        val argumentCaptor: KArgumentCaptor<SingleEvent<Boolean>> = argumentCaptor()
        verify(mockLoginFailedObserver).onChanged(argumentCaptor.capture())
        assertTrue(argumentCaptor.firstValue.peekContent())

        verify(sessionManager).rememberPassword = false
        verify(dbManager).clearAndResetAllTables()
    }

//    @Test
//    fun authenticateUser_badVoiceMailSummaryResponseEvent_errorThrown() {
//        val mockUserDetails: UserDetails = mock()
//        val emptyMap: Map<String, ServiceSettings> = emptyMap()
//        val featureAccessCodes = FeatureAccessCodes(null)
//
//        whenever(authenticationServiceRepository.authenticateUser("username@nextiva.com", "password"))
//                .thenReturn(Single.just(RxEvents.AuthenticationResponseEvent(true, true)))
//        whenever(mobileConfigRepository.mobileConfig)
//                .thenReturn(Single.just(RxEvents.MobileConfigResponseEvent(true)))
//        whenever(userRepository.userDetails)
//                .thenReturn(Single.just(RxEvents.UserDetailsResponseEvent(true, mockUserDetails)))
//        whenever(userRepository.featureAccessCodes)
//                .thenReturn(Single.just(RxEvents.FeatureAccessCodesResponseEvent(true, featureAccessCodes)))
//        whenever(userRepository.getServiceSettingsFiltered(arrayOf(Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE)))
//                .thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap)))
//        whenever(userRepository.voicemailMessageSummary)
//                .thenReturn(Single.just(RxEvents.VoicemailMessageSummaryResponseEvent(false, 0, 0)))
//        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")
//
//        viewModel.authenticateUser("username", "password")
//
//        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")
//        verify(mobileConfigRepository).mobileConfig
//        verify(userRepository).userDetails
//        verify(userRepository).featureAccessCodes
//        verify(userRepository).getServiceSettingsFiltered(arrayOf(Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE))
//        verify(userRepository).voicemailMessageSummary
//
//        val argumentCaptor: KArgumentCaptor<SingleEvent<Boolean>> = argumentCaptor()
//        verify(mockLoginFailedObserver).onChanged(argumentCaptor.capture())
//        assertTrue(argumentCaptor.firstValue.peekContent())
//
//        verify(sessionManager).rememberPassword = false
//        verify(dbManager).clearAndResetAllTables()
//    }

//    @Test
//    fun authenticateUser_badMissedCallLogEntriesResponseEvent_errorThrown() {
//        val mockUserDetails: UserDetails = mock()
//        val emptyMap: Map<String, ServiceSettings> = emptyMap()
//        val featureAccessCodes = FeatureAccessCodes(null)
//
//        whenever(authenticationServiceRepository.authenticateUser("username@nextiva.com", "password"))
//                .thenReturn(Single.just(RxEvents.AuthenticationResponseEvent(true, true)))
//        whenever(mobileConfigRepository.mobileConfig)
//                .thenReturn(Single.just(RxEvents.MobileConfigResponseEvent(true)))
//        whenever(userRepository.userDetails)
//                .thenReturn(Single.just(RxEvents.UserDetailsResponseEvent(true, mockUserDetails)))
//        whenever(userRepository.featureAccessCodes)
//                .thenReturn(Single.just(RxEvents.FeatureAccessCodesResponseEvent(true, featureAccessCodes)))
//        whenever(userRepository.getServiceSettingsFiltered(arrayOf(Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE)))
//                .thenReturn(Single.just(RxEvents.ServiceSettingsMapResponseEvent(true, emptyMap)))
//        whenever(userRepository.voicemailMessageSummary)
//                .thenReturn(Single.just(RxEvents.VoicemailMessageSummaryResponseEvent(true, 0, 0)))
//        whenever(callManagementRepository.allCallLogEntries)
//                .thenReturn(Single.just(RxEvents.CallHistoryResponseEvent(false, null)))
//        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")
//
//        viewModel.authenticateUser("username", "password")
//
//        verify(authenticationServiceRepository).authenticateUser("username@nextiva.com", "password")
//        verify(mobileConfigRepository).mobileConfig
//        verify(userRepository).userDetails
//        verify(userRepository).featureAccessCodes
//        verify(userRepository).getServiceSettingsFiltered(arrayOf(Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE))
//        verify(userRepository).voicemailMessageSummary
//        verify(callManagementRepository).allCallLogEntries
//
//        val argumentCaptor: KArgumentCaptor<SingleEvent<Boolean>> = argumentCaptor()
//        verify(mockLoginFailedObserver).onChanged(argumentCaptor.capture())
//        assertTrue(argumentCaptor.firstValue.peekContent())
//
//        verify(sessionManager).rememberPassword = false
//        verify(dbManager).clearAndResetAllTables()
//    }

    @Test
    fun clearAndResetAllTables_callsToDatabase() {
        viewModel.clearAndResetAllTables()
        verify(dbManager).clearAndResetAllTables()
    }

    @Test
    fun getSessionUsernameWithoutHostname_hasUsernameWithHostname_returnsValue() {
        mockkStatic(TextUtils::class)
        whenever(sessionManager.lastLoggedUsername).thenReturn("username@nextiva.com")
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")
        every { TextUtils.isEmpty("username@nextiva.com") } returns false

        assertEquals("username", viewModel.sessionUsernameWithoutHostname)
        unmockkAll()
    }

    @Test
    fun getSessionUsernameWithoutHostname_hasUsernameWithoutHostname_returnsValue() {
        mockkStatic(TextUtils::class)
        whenever(sessionManager.lastLoggedUsername).thenReturn("username")
        whenever(sharedPreferenceManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME)).thenReturn("@nextiva.com")
        every { TextUtils.isEmpty("username") } returns false

        assertEquals("username", viewModel.sessionUsernameWithoutHostname)
        unmockkAll()
    }

    @Test
    fun getSessionUsernameWithoutHostname_noHostname_returnsNull() {
        mockkStatic(TextUtils::class)
        whenever(sessionManager.lastLoggedUsername).thenReturn(null)
        every { TextUtils.isEmpty(null) } returns true

        assertEquals(null, viewModel.sessionUsernameWithoutHostname)
        unmockkAll()
    }

    @Test
    fun getSessionPassword_returnsPassword() {
        whenever(sessionManager.lastLoggedPassword).thenReturn("password")

        assertEquals("password", viewModel.sessionPassword)
    }

    @Test
    fun shouldRememberPassword_returnsSessionGetRememberPassword() {
        whenever(sessionManager.rememberPassword).thenReturn(true)

        assertTrue(viewModel.shouldRememberPassword())
    }
}