package com.nextiva.nextivaapp.android.viewmodels

import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.mocks.values.MobileConfigMock
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.FeatureAccessCode
import com.nextiva.nextivaapp.android.models.FeatureAccessCodes
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.*
import javax.inject.Inject

@HiltAndroidTest
class DialerViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockProcessedCallInfoObserver: Observer<ParticipantInfo> = mock()
    private val mockErrorStateObserver: Observer<Boolean> = mock()
    private val mockLastDialedPhoneNumberObserver: Observer<String> = mock()

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var configManager: ConfigManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var sipManager: PJSipManager

    @Inject
    lateinit var callManagementRepository: CallManagementRepository

    private lateinit var viewModel: DialerViewModel

    override fun setup() {
        super.setup()
        hiltRule.inject()

        whenever(configManager.mobileConfig).thenReturn(MobileConfigMock.getMobileConfig())

        viewModel = DialerViewModel(
                ApplicationProvider.getApplicationContext(),
                sessionManager,
                configManager,
                dbManager,
                sipManager,
                callManagementRepository)

        viewModel.processedCallInfoLiveData.observeForever(mockProcessedCallInfoObserver)
        viewModel.errorStateLiveData.observeForever(mockErrorStateObserver)
        viewModel.lastDialedPhoneNumberLiveData.observeForever(mockLastDialedPhoneNumberObserver)
    }

    @Test
    fun clearErrorState_clearsValue() {
        viewModel.clearErrorState()

        verify(mockErrorStateObserver).onChanged(false)
    }

    @Test
    fun clearProcessedCallInfo_clearsValue() {
        viewModel.clearProcessedCallInfo()

        verify(mockProcessedCallInfoObserver).onChanged(anyOrNull())
    }

    @Test
    fun placeCall_blankNumber_updatesLastDialedPhoneNumberValue() {
        whenever(sessionManager.lastDialedPhoneNumber).thenReturn("2223334444")

        viewModel.placeCall("", Enums.Sip.CallTypes.VIDEO)

        verify(mockLastDialedPhoneNumberObserver).onChanged("2223334444")
    }

    @Test
    fun placeCall_voicemailNumber_setsLastDialedPhoneNumber() {
        viewModel.placeCall("9999", Enums.Sip.CallTypes.VIDEO)

        verify(sessionManager).lastDialedPhoneNumber = "9999"
    }

    @Test
    fun placeCall_callsToDbManagerToGetContact() {
        viewModel.placeCall("9999", Enums.Sip.CallTypes.VIDEO)

        verify(dbManager).getConnectContactFromPhoneNumber("9999")
    }

    @Test
    fun placeCall_noFoundNextivaContact_callsToProcessedCallInfoLiveData() {
        whenever(dbManager.getConnectContactFromPhoneNumber("9999")).thenReturn(Single.just(DbResponse<NextivaContact>(null)))
        viewModel.placeCall("9999", Enums.Sip.CallTypes.VIDEO)

        val argumentCaptor: KArgumentCaptor<ParticipantInfo> = argumentCaptor()

        verify(mockProcessedCallInfoObserver).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Sip.CallTypes.VOICE, argumentCaptor.firstValue.callType)
        assertEquals("9999", argumentCaptor.firstValue.numberToCall)
        assertNull(argumentCaptor.firstValue.contactId)
        assertEquals(Enums.Service.DialingServiceTypes.THIS_PHONE, argumentCaptor.firstValue.disallowDialingServiceTypes!![0])
    }

    @Test
    fun placeCall_voicemailNumber_callsToProcessedCallInfoLiveData() {
        val nextivaContact = NextivaContact("123")
        whenever(dbManager.getConnectContactFromPhoneNumber("9999")).thenReturn(Single.just(DbResponse(nextivaContact)))
        viewModel.placeCall("9999", Enums.Sip.CallTypes.VIDEO)

        val argumentCaptor: KArgumentCaptor<ParticipantInfo> = argumentCaptor()

        verify(mockProcessedCallInfoObserver).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Sip.CallTypes.VOICE, argumentCaptor.firstValue.callType)
        assertEquals("9999", argumentCaptor.firstValue.numberToCall)
        assertEquals(nextivaContact.userId, argumentCaptor.firstValue.contactId)
        assertEquals(Enums.Service.DialingServiceTypes.THIS_PHONE, argumentCaptor.firstValue.disallowDialingServiceTypes!![0])
    }

    @Test
    fun placeCall_voicemailNumber_clearsLastDialedPhoneNumberLiveData() {
        whenever(dbManager.getConnectContactFromPhoneNumber("9999")).thenReturn(Single.just(DbResponse(NextivaContact("123"))))
        viewModel.placeCall("9999", Enums.Sip.CallTypes.VIDEO)

        verify(mockLastDialedPhoneNumberObserver).onChanged(anyOrNull())
    }

    @Test
    fun placeCall_dialedNumber_setsLastDialedPhoneNumber() {
        viewModel.placeCall("5554443333", Enums.Sip.CallTypes.VIDEO)

        verify(sessionManager).lastDialedPhoneNumber = "5554443333"
    }

    @Test
    fun placeCall_dialedNumber_callsToProcessedCallInfoLiveData() {
        val nextivaContact = NextivaContact("123")
        nextivaContact.phoneNumbers = arrayListOf(PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "5554443333", null))
        whenever(dbManager.getConnectContactFromPhoneNumber("5554443333")).thenReturn(Single.just(DbResponse(nextivaContact)))
        viewModel.placeCall("5554443333", Enums.Sip.CallTypes.VIDEO)

        val argumentCaptor: KArgumentCaptor<ParticipantInfo> = argumentCaptor()

        verify(mockProcessedCallInfoObserver).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Sip.CallTypes.VIDEO, argumentCaptor.firstValue.callType)
        assertEquals("5554443333", argumentCaptor.firstValue.numberToCall)
    }

    @Test
    fun placeCall_dialedNumber_clearsLastDialedPhoneNumberLiveData() {
        val nextivaContact = NextivaContact("123")
        nextivaContact.phoneNumbers = arrayListOf(PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "5554443333", null))
        whenever(dbManager.getConnectContactFromPhoneNumber("5554443333")).thenReturn(Single.just(DbResponse(nextivaContact)))
        viewModel.placeCall("5554443333", Enums.Sip.CallTypes.VIDEO)

        verify(mockLastDialedPhoneNumberObserver).onChanged(anyOrNull())
    }

    @Test
    fun placeVoicemailCall_setsLastDialedPhoneNumber() {
        viewModel.placeVoicemailCall()

        verify(sessionManager).lastDialedPhoneNumber = "9999"
    }

    @Test
    fun placeVoicemailCall_noVoicemailNumber_callsToErrorStateLiveData() {
        whenever(configManager.mobileConfig).thenReturn(MobileConfigMock.getMobileConfigEmpty())

        viewModel.placeVoicemailCall()

        verify(mockErrorStateObserver).onChanged(true)
    }

    @Test
    fun placeVoicemailCall_hasVoicemailNumber_callsToProcessedCallInfoLiveData() {
        whenever(dbManager.getConnectContactFromPhoneNumber("9999")).thenReturn(Single.just(DbResponse(NextivaContact("123"))))
        viewModel.placeVoicemailCall()

        val argumentCaptor: KArgumentCaptor<ParticipantInfo> = argumentCaptor()

        verify(mockProcessedCallInfoObserver).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Sip.CallTypes.VOICE, argumentCaptor.firstValue.callType)
        assertEquals("9999", argumentCaptor.firstValue.numberToCall)
        assertEquals(Enums.Service.DialingServiceTypes.THIS_PHONE, argumentCaptor.firstValue.disallowDialingServiceTypes!![0])
    }

    @Test
    fun placeVoicemailCall_hasVoicemailNumber_clearsLastDialedPhoneNumberLiveData() {
        whenever(dbManager.getConnectContactFromPhoneNumber("9999")).thenReturn(Single.just(DbResponse(NextivaContact("123"))))
        viewModel.placeVoicemailCall()

        verify(mockLastDialedPhoneNumberObserver).onChanged(anyOrNull())
    }

    @Test
    fun pullCall_noPullCallNumber_callsToErrorStateLiveData() {
        viewModel.pullCallUnchecked()

        verify(mockErrorStateObserver).onChanged(true)
    }

    @Test
    fun pullCall_hasPullCallNumber_callsToProcessedCallInfoLiveData() {
        val nextivaContact = NextivaContact("123")
        whenever(sessionManager.featureAccessCodes).thenReturn(FeatureAccessCodes(arrayListOf(FeatureAccessCode("202", Enums.Service.FeatureAccessCodes.CALL_RETRIEVE))))
        whenever(dbManager.getConnectContactFromPhoneNumber("202")).thenReturn(Single.just(DbResponse(nextivaContact)))

        viewModel.pullCallUnchecked()

        val argumentCaptor: KArgumentCaptor<ParticipantInfo> = argumentCaptor()

        verify(mockProcessedCallInfoObserver).onChanged(argumentCaptor.capture())

        assertEquals(Enums.Sip.CallTypes.VOICE, argumentCaptor.firstValue.callType)
        assertEquals("202", argumentCaptor.firstValue.numberToCall)
        assertEquals(nextivaContact.userId, argumentCaptor.firstValue.contactId)
        assertEquals(Enums.Service.DialingServiceTypes.THIS_PHONE, argumentCaptor.firstValue.disallowDialingServiceTypes!![0])
    }

    @Test
    fun pullCall_hasPullCallNumber_clearsLastDialedPhoneNumberLiveData() {
        whenever(sessionManager.featureAccessCodes).thenReturn(FeatureAccessCodes(arrayListOf(FeatureAccessCode("202", Enums.Service.FeatureAccessCodes.CALL_RETRIEVE))))
        whenever(dbManager.getConnectContactFromPhoneNumber("202")).thenReturn(Single.just(DbResponse<NextivaContact>(null)))

        viewModel.pullCallUnchecked()

        verify(mockLastDialedPhoneNumberObserver).onChanged(anyOrNull())
    }
}