package com.nextiva.nextivaapp.android.db

import android.os.Looper.getMainLooper
import android.text.TextUtils
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.dao.CallLogsDao
import com.nextiva.nextivaapp.android.db.dao.CompleteContactDao
import com.nextiva.nextivaapp.android.db.dao.ContactDao
import com.nextiva.nextivaapp.android.db.dao.SessionDao
import com.nextiva.nextivaapp.android.db.dao.VCardDao
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbSession
import com.nextiva.nextivaapp.android.db.model.DbVCard
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.mocks.values.ContactLists
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.net.buses.RxBus
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockkStatic
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import org.robolectric.Shadows.shadowOf
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class NextivaDbManagerTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferencesManager

    @Inject
    lateinit var avatarManager: AvatarManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var umsRepository: UmsRepository

    @Inject
    lateinit var logManager: LogManager

    lateinit var dbManager: NextivaDbManager

    private val mockVCardDao: VCardDao = mock()
    private val mockContactDao: ContactDao = mock()
    private val mockCompleteContactDao: CompleteContactDao = mock()
    private val mockCallLogsDao: CallLogsDao = mock()
    private val mockSessionDao: SessionDao = mock()
    private val mockDatabase: AppDatabase = mock()

    private val mockRxBus: RxBus = mock()

    override fun setup() {
        super.setup()
        hiltRule.inject()

        dbManager = NextivaDbManager(ApplicationProvider.getApplicationContext(), schedulerProvider, sharedPreferenceManager, avatarManager, calendarManager, logManager)
    }

    override fun after() {
        super.after()
        dbManager.closeDatabase()
    }

    @Test
    fun setSetting_callsToUserDetailDao() {
        dbManager.setSessionDao(mockSessionDao)
        val argumentCaptor: KArgumentCaptor<DbSession> = argumentCaptor()

        dbManager.setSessionSetting("Key", "Value")
        verify(mockSessionDao).insertSession(argumentCaptor.capture())

        assertNull(argumentCaptor.firstValue.id)
        assertEquals("Key", argumentCaptor.firstValue.key)
        assertEquals("Value", argumentCaptor.firstValue.value)
    }

    @Test
    fun getSettingValue_getsValueFromUserDetailDao() {
        dbManager.setSessionDao(mockSessionDao)
        dbManager.getSessionSettingValue("SETTINGS_KEY")
        verify(mockSessionDao).getSessionFromKeyInThread("SETTINGS_KEY")
    }

    @Test
    fun getOwnAvatar_returnsAvatar() {
        dbManager.setSessionDao(mockSessionDao)
        whenever(mockSessionDao.getSessionFromKey(Enums.Session.DatabaseKey.USER_AVATAR)).thenReturn(Maybe.just(DbSession(null, "key", "value")))

        dbManager.ownAvatar
                .test()
                .assertNoErrors()

        verify(mockSessionDao).getSessionFromKey(Enums.Session.DatabaseKey.USER_AVATAR)
    }

    @Test
    fun saveOwnVCard_hasAvatarGetVCardReturnsNull_insertsVCardAndUpdatesUI() {
        val avatarBytes = ByteArray(3)
        dbManager.setRxBus(mockRxBus)

        whenever(avatarManager.byteArrayToString(avatarBytes)).thenReturn("Avatar Bytes")
        whenever(avatarManager.isByteArrayNotEmpty(avatarBytes)).thenReturn(true)

        dbManager.setSessionDao(mockSessionDao)

        whenever(mockSessionDao.getSessionFromKeyInThread("USER_AVATAR")).thenReturn(null)
        dbManager.saveOwnVCard(CompositeDisposable(), avatarBytes)

        val insertSessionArgumentCaptor: KArgumentCaptor<DbSession> = argumentCaptor()

        verify(mockSessionDao).insertSession(insertSessionArgumentCaptor.capture())

        assertEquals("Avatar Bytes", insertSessionArgumentCaptor.firstValue.value)
        assertEquals("USER_AVATAR", insertSessionArgumentCaptor.firstValue.key)
    }

    @Test
    fun saveOwnVCard_hasAvatarGetVCardReturnsDifferentVCard_insertsVCardAndUpdatesUi() {
        val avatarBytes = ByteArray(3)

        whenever(avatarManager.byteArrayToString(avatarBytes)).thenReturn("Avatar Bytes")
        whenever(avatarManager.isByteArrayNotEmpty(avatarBytes)).thenReturn(true)

        dbManager.setRxBus(mockRxBus)
        dbManager.setSessionDao(mockSessionDao)

        mockkStatic(TextUtils::class)
        every { TextUtils.equals(any(), any()) } returns false

        whenever(mockSessionDao.getSessionFromKeyInThread("USER_AVATAR")).thenReturn(DbSession())
        dbManager.saveOwnVCard(CompositeDisposable(), avatarBytes)

        val avatarArgumentCaptor: KArgumentCaptor<String> = argumentCaptor()

        verify(mockSessionDao).updateValue(eq("USER_AVATAR"), avatarArgumentCaptor.capture())

        assertEquals("Avatar Bytes", avatarArgumentCaptor.firstValue)
    }

//TODO:Causing Jenkins to run out of memory

//    @Test
//    fun saveOwnVCard_hasAvatarGetVCardReturnsSaveVCard_doesNothing() {
//        reset(mockRxBus)
//        dbManager.setRxBus(mockRxBus)
//        dbManager.setVCardDao(mockVCardDao)
//        dbManager.setSessionDao(mockSessionDao)
//
//        val byteArray = ByteArray(3)
//
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), any()) } returns true
//
//        whenever(mockVCardDao.getVCardFromUserJidInThread("jid@jid.im")).thenReturn(DbVCard())
//        dbManager.saveOwnVCard(CompositeDisposable(), "jid@jid.im", byteArray)
//
//        verify(mockRxBus, never()).publish(any())
//    }
//
//    @Test
//    fun saveOwnVCard_noAvatarGetVCardReturnsNull_insertsVCardAndUpdatesUI() {
//        dbManager.setRxBus(mockRxBus)
//        dbManager.setSessionDao(mockSessionDao)
//
//        whenever(mockSessionDao.getSessionFromKeyInThread("USER_AVATAR")).thenReturn(null)
//        dbManager.saveOwnVCard(CompositeDisposable(), "jid@jid.im", null)
//
//        val vCardEventArgumentCaptor: KArgumentCaptor<RxEvents.VCardResponseEvent> = argumentCaptor()
//
//        verify(mockSessionDao).insertSession(DbSession(null, "USER_AVATAR", null))
//        verify(mockRxBus).publish(vCardEventArgumentCaptor.capture())
//
//        assertTrue(vCardEventArgumentCaptor.firstValue.isSuccessful)
//    }

    @Test
    fun saveOwnVCard_noAvatarGetVCardReturnsVCardWithAvatar_clearsAvatarAndUpdatesUi() {
        dbManager.setSessionDao(mockSessionDao)
        dbManager.setRxBus(mockRxBus)
        val bytes = ByteArray(2)

        whenever(mockSessionDao.getSessionFromKeyInThread("USER_AVATAR")).thenReturn(DbSession(null, "USER_AVATAR", bytes.toString()))
        dbManager.saveOwnVCard(CompositeDisposable(), null)

        verify(mockSessionDao).updateValue("USER_AVATAR", null)
    }

    //TODO:Causing Jenkins to run out of memory

//    @Test
//    fun saveOwnVCard_noAvatarGetVCardReturnsVCardWithNoAvatar_doesNothing() {
//        reset(mockRxBus)
//        dbManager.setRxBus(mockRxBus)
//        dbManager.setVCardDao(mockVCardDao)
//
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), any()) } returns false
//
//        whenever(mockVCardDao.getVCardFromUserJidInThread("jid@jid.im")).thenReturn(DbVCard(null, null, null, null, null, null, null))
//        dbManager.saveOwnVCard(CompositeDisposable(), "jid@jid.im", null)
//
//        verify(mockVCardDao, never()).updateVCardWithNewAvatar(any(), any(), any())
//        verify(mockRxBus, never()).publish(any())
//    }

    @Test
    fun getVCard_vCardExists_noErrors() {
        dbManager.setVCardDao(mockVCardDao)

        whenever(mockVCardDao.getVCardFromUserJid("jid@jid.im")).thenReturn(Maybe.just(DbVCard()))
        dbManager.getVCard("jid@jid.im")
                .test()
                .assertNoErrors()
    }

    @Test
    fun getSingleVCard_vCardExists_noErrors() {
        dbManager.setVCardDao(mockVCardDao)

        whenever(mockVCardDao.getSingleVCardFromUserJid("jid@jid.im")).thenReturn(Single.just(DbVCard()))
        dbManager.getSingleVCard("jid@jid.im")
                .test()
                .assertNoErrors()
    }

    @Test
    fun updateAllVCards_newHasAvatarCachedIsNull_insertsVCardAndNotifiesUI() {
        dbManager.setVCardDao(mockVCardDao)
        dbManager.setContactDao(mockContactDao)
        dbManager.setRxBus(mockRxBus)

        val bytes = ByteArray(2)
        val nextivaContactVCard = DbVCard("jid@jid.im", bytes)
        val nextivaContactVCardList: ArrayList<DbVCard> = ArrayList()
        nextivaContactVCardList.add(nextivaContactVCard)

        val singleInt: Single<Int> = Single.just(1)

        whenever(mockContactDao.getRosterContactIdFromJid("jid@jid.im")).thenReturn(singleInt)

        dbManager.updateAllVCards(CompositeDisposable(), nextivaContactVCardList)

        val vCardArgumentCaptor: KArgumentCaptor<DbVCard> = argumentCaptor()

        verify(mockVCardDao).insertVCard(vCardArgumentCaptor.capture())

        assertEquals(1, vCardArgumentCaptor.firstValue.contactId)
        assertEquals(bytes, vCardArgumentCaptor.firstValue.photoData)
        assertNull(vCardArgumentCaptor.firstValue.callId)
        assertNull(vCardArgumentCaptor.firstValue.sipUri)
    }

    @Test
    fun updateAllVCards_newHasAvatarCachedIsNotNullAndNotEqual_updatesVCardAndNotifiesUI() {
        dbManager.setVCardDao(mockVCardDao)
        dbManager.setContactDao(mockContactDao)
        dbManager.setRxBus(mockRxBus)

        val bytesOne = ByteArray(2)
        val bytesTwo = ByteArray(3)

        val dbVCardTwo = DbVCard(null, 1, bytesTwo, null, null, null, null)
        val nextivaContactVCardOne = DbVCard("jid@jid.im", bytesOne)
        val nextivaContactVCardTwo = DbVCard("jid@jid.im", bytesTwo)
        val nextivaContactVCardList: ArrayList<DbVCard> = ArrayList()
        nextivaContactVCardList.add(nextivaContactVCardOne)
        nextivaContactVCardList.add(nextivaContactVCardTwo)

        val singleInt: Single<Int> = Single.just(1)

        whenever(mockContactDao.getRosterContactIdFromJid("jid@jid.im")).thenReturn(singleInt)
        whenever(mockVCardDao.getVCardFromUserJidInThread("jid@jid.im")).thenReturn(dbVCardTwo)

        dbManager.updateAllVCards(CompositeDisposable(), nextivaContactVCardList)

        verify(mockVCardDao).updateVCardWithNewAvatar("jid@jid.im", bytesOne, null)
    }

    @Test
    fun updateAllVCards_noAvatarNoCachedVCard_insertsVCardAndNotifiesUI() {
        reset(mockVCardDao)
        dbManager.setVCardDao(mockVCardDao)
        dbManager.setContactDao(mockContactDao)
        dbManager.setRxBus(mockRxBus)

        val nextivaContactVCardOne = DbVCard("jid@jid.im", null)
        val nextivaContactVCardList: ArrayList<DbVCard> = ArrayList()
        nextivaContactVCardList.add(nextivaContactVCardOne)

        val singleInt: Single<Int> = Single.just(1)

        whenever(mockContactDao.getRosterContactIdFromJid("jid@jid.im")).thenReturn(singleInt)
        whenever(mockVCardDao.getVCardFromUserJidInThread("jid@jid.im")).thenReturn(null)

        val vCardArgumentCaptor: KArgumentCaptor<DbVCard> = argumentCaptor()

        dbManager.updateAllVCards(CompositeDisposable(), nextivaContactVCardList)

        verify(mockVCardDao).insertVCard(vCardArgumentCaptor.capture())

        assertEquals(1, vCardArgumentCaptor.firstValue.contactId)
        assertNull(vCardArgumentCaptor.firstValue.photoData)
        assertNull(vCardArgumentCaptor.firstValue.callId)
        assertNull(vCardArgumentCaptor.firstValue.sipUri)
    }

    @Test
    fun updateAllVCards_noAvatarWithOneCached_updatesVCardAndNotifiesUI() {
        dbManager.setVCardDao(mockVCardDao)
        dbManager.setContactDao(mockContactDao)
        dbManager.setRxBus(mockRxBus)

        val dbVCardTwo = DbVCard(null, 1, ByteArray(2), null, null, null, null)
        val nextivaContactVCardOne = DbVCard("jid@jid.im", null)
        val nextivaContactVCardList: ArrayList<DbVCard> = ArrayList()
        nextivaContactVCardList.add(nextivaContactVCardOne)

        val singleInt: Single<Int> = Single.just(1)

        whenever(mockContactDao.getRosterContactIdFromJid("jid@jid.im")).thenReturn(singleInt)
        whenever(mockVCardDao.getVCardFromUserJidInThread("jid@jid.im")).thenReturn(dbVCardTwo)

        dbManager.updateAllVCards(CompositeDisposable(), nextivaContactVCardList)

        verify(mockVCardDao).updateVCardWithNewAvatar("jid@jid.im", null, null)
    }

//    @Test
//    fun updateAllVCards_noJid_doesNothing() {
//        reset(mockRxBus)
//        dbManager.setVCardDao(mockVCardDao)
//        dbManager.setContactDao(mockContactDao)
//        dbManager.setRxBus(mockRxBus)
//
//        val nextivaContactVCardOne = DbVCard("jid@jid.im", ByteArray(2))
//        val nextivaContactVCardList: ArrayList<DbVCard> = ArrayList()
//        nextivaContactVCardList.add(nextivaContactVCardOne)
//
//        whenever(mockContactDao.getRosterContactIdFromJid(null)).thenReturn(null)
//
//        mockkStatic(TextUtils::class)
//        every { TextUtils.isEmpty(null) } returns true
//
//        dbManager.updateAllVCards(CompositeDisposable(), nextivaContactVCardList)
//
//        verify(mockVCardDao, never()).updateVCardWithNewAvatar(any(), any(), any())
//        verify(mockRxBus, never()).publish(any())
//    }

    //TODO:Causing Jenkins to run out of memory

//    @Test
//    fun saveRosterContacts_updateFromContactDetails_savesContacts() {
//        dbManager.setVCardDao(mockVCardDao)
//        dbManager.setCompleteContactDao(mockCompleteContactDao)
//        dbManager.setDatabase(mockDatabase)
//        dbManager.setRxBus(mockRxBus)
//
//        val testList = ContactLists.getNextivaContactTestList()
//
//        whenever(mockVCardDao.getVCardFromUserJidInThread("jid@jid.im")).thenReturn(DbVCard(null, null, ByteArray(2), null, null, null, null))
//        whenever(mockVCardDao.getVCardFromUserJidInThread("jid2@jid.im")).thenReturn(DbVCard(null, null, ByteArray(3), null, null, null, null))
//        whenever(umsRepository.getVCards(any(), any())).thenReturn(Single.never())
//
//        val contactUpdatedResponseArgumentCaptor: KArgumentCaptor<RxEvents.ContactUpdatedResponseEvent> = argumentCaptor()
//
//        dbManager.saveRosterContacts(CompositeDisposable(), umsRepository, testList, true)
//
//        verify(sharedPreferenceManager).setLong(SharedPreferencesManager.ROSTER_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, calendarManager.nowMillis)
//        verify(mockCompleteContactDao).insertContacts(eq(testList), eq(mockDatabase), eq(Enums.Contacts.ContactTypes.PERSONAL), any())
//        verify(mockRxBus).publish(contactUpdatedResponseArgumentCaptor.capture())
//
//        assertTrue(contactUpdatedResponseArgumentCaptor.firstValue.isSuccessful)
//    }

    @Test
    fun saveRosterContacts_updateNotFromContactDetails_savesContacts() {
        dbManager.setVCardDao(mockVCardDao)
        dbManager.setCompleteContactDao(mockCompleteContactDao)
        dbManager.setDatabase(mockDatabase)
        dbManager.setRxBus(mockRxBus)

        val compositeDisposable = CompositeDisposable()
        val testList = ContactLists.getNextivaContactTestList()

        whenever(mockVCardDao.getVCardFromUserJidInThread("jid@jid.im")).thenReturn(DbVCard(null, null, ByteArray(2), null, null, null, null))
        whenever(mockVCardDao.getVCardFromUserJidInThread("jid2@jid.im")).thenReturn(DbVCard(null, null, ByteArray(3), null, null, null, null))
        whenever(umsRepository.getVCards(testList, compositeDisposable)).thenReturn(Single.just(true))

        val rosterResponseArgumentCaptor: KArgumentCaptor<RxEvents.RosterResponseEvent> = argumentCaptor()

        dbManager.saveRosterContacts(compositeDisposable, umsRepository, testList, false)

        verify(sharedPreferenceManager).setLong(SharedPreferencesManager.ROSTER_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, calendarManager.nowMillis)
        verify(mockCompleteContactDao).insertContacts(eq(testList), eq(mockDatabase), eq(Enums.Contacts.ContactTypes.PERSONAL), any())
        verify(umsRepository).getVCards(testList, compositeDisposable)
        verify(mockRxBus).publish(rosterResponseArgumentCaptor.capture())

        assertTrue(rosterResponseArgumentCaptor.firstValue.isSuccessful)
        assertEquals(3, rosterResponseArgumentCaptor.firstValue.favoritesNumber)
    }

    @Test
    fun saveEnterpriseContactsInThread_savesContacts() {
        dbManager.setVCardDao(mockVCardDao)
        dbManager.setCompleteContactDao(mockCompleteContactDao)
        dbManager.setDatabase(mockDatabase)

        val testList = ContactLists.getNextivaContactTestList()

        whenever(mockVCardDao.getSingleVCardFromUserJid("jid@jid.im")).thenReturn(Single.just(DbVCard(null, null, ByteArray(2), null, null, null, null)))
        whenever(mockVCardDao.getSingleVCardFromUserJid("jid2@jid.im")).thenReturn(Single.just(DbVCard(null, null, ByteArray(3), null, null, null, null)))
        whenever(mockVCardDao.getSingleVCardFromUserJid("jid3@jid.im")).thenReturn(Single.just(DbVCard(null, null, ByteArray(4), null, null, null, null)))

        val transactionId = UUID.randomUUID().toString()

        dbManager.saveEnterpriseContactsInThread(testList, transactionId)

        verify(sharedPreferenceManager).setLong(SharedPreferencesManager.ENTERPRISE_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, calendarManager.nowMillis)
        verify(mockCompleteContactDao).insertContacts(testList, mockDatabase, Enums.Contacts.ContactTypes.ENTERPRISE, transactionId)
    }

    @Test
    fun saveLocalContacts_savesContacts() {
        dbManager.setDatabase(mockDatabase)
        dbManager.setCompleteContactDao(mockCompleteContactDao)

        val testList = ContactLists.getNextivaContactTestList()

        dbManager.saveLocalContactsInThread(testList)

        verify(mockCompleteContactDao).insertContacts(eq(testList), eq(mockDatabase), eq(Enums.Contacts.ContactTypes.LOCAL), any())
    }

    @Test
    fun saveLocalContactsInThread_savesContacts() {
        dbManager.setDatabase(mockDatabase)
        dbManager.setCompleteContactDao(mockCompleteContactDao)

        val testList = ContactLists.getNextivaContactTestList()

        dbManager.saveLocalContactsInThread(testList)

        verify(mockCompleteContactDao).insertContacts(eq(testList), eq(mockDatabase), eq(Enums.Contacts.ContactTypes.LOCAL), any())
    }

    @Test
    fun addContact_savesContact() {
        dbManager.setCompleteContactDao(mockCompleteContactDao)
        dbManager.setDatabase(mockDatabase)

        val testContact = ContactLists.getNextivaContactTestList()[0]
        val contactListArgumentCaptor: KArgumentCaptor<ArrayList<NextivaContact>> = argumentCaptor()

        dbManager.addContact(testContact, CompositeDisposable())

        verify(mockCompleteContactDao).insertContacts(contactListArgumentCaptor.capture(), eq(mockDatabase), eq(Enums.Contacts.ContactTypes.NONE), any())
        assertEquals(testContact, contactListArgumentCaptor.firstValue[0])
    }

    @Test
    fun deleteContactByContactId_deletesContact() {
        dbManager.setContactDao(mockContactDao)

        dbManager.deleteContactByContactId(CompositeDisposable(), "testId")

        verify(mockContactDao).deleteContactByContactTypeId("testId")
    }

    @Test
    fun getDbContactsInThread_returnsContactsList() {
        dbManager.setCompleteContactDao(mockCompleteContactDao)
        dbManager.setVCardDao(mockVCardDao)

        dbManager.getDbContactsInThread(Enums.Contacts.CacheTypes.ALL_ROSTER)

        verify(mockCompleteContactDao).getNextivaContactsListInThread(Enums.Contacts.CacheTypes.ALL_ROSTER)
    }

    @Test
    fun getContacts_returnsSingleWithContactsList() {
        dbManager.setCompleteContactDao(mockCompleteContactDao)

        whenever(mockCompleteContactDao.getNextivaContactsList(Enums.Contacts.CacheTypes.ALL_ROSTER)).thenReturn(Single.just(ContactLists.getNextivaContactTestList()))

        dbManager.getContacts(Enums.Contacts.CacheTypes.ALL_ROSTER).test().assertNoErrors()
    }

    @Test
    fun getUserNameFromJid_returnsMaybe() {
        dbManager.setContactDao(mockContactDao)

        whenever(mockContactDao.getUserNameFromJid("jid@jid.im")).thenReturn(Maybe.just("Username"))

        dbManager.getUserNameFromJid("jid@jid.im")
                .test()
                .assertNoErrors()

        verify(mockContactDao).getUserNameFromJid("jid@jid.im")
    }

    @Test
    fun getContactFromJid_returnsContact() {
        dbManager.setContactDao(mockContactDao)

        whenever(mockContactDao.getContact("jid@jid.im")).thenReturn(Single.just(NextivaContact("Hi")))

        dbManager.getContactFromJid("jid@jid.im")
                .test()
                .assertNoErrors()

        verify(mockContactDao).getContact("jid@jid.im")
    }

    @Test
    fun getCompleteRosterContactFromJid_returnsContact() {
        dbManager.setCompleteContactDao(mockCompleteContactDao)

        whenever(mockCompleteContactDao.getCompleteRosterContactByJid("jid@jid.im")).thenReturn(Single.just(NextivaContact("Hi")))

        dbManager.getCompleteRosterContactFromJid("jid@jid.im")
                .test()
                .assertNoErrors()

        verify(mockCompleteContactDao).getCompleteRosterContactByJid("jid@jid.im")
    }

    @Test
    fun getContactFromJidAndContactType_returnsContact() {
        dbManager.setContactDao(mockContactDao)

        whenever(mockContactDao.getContact("jid@jid.im", Enums.Contacts.ContactTypes.PERSONAL)).thenReturn(Single.just(NextivaContact("Hid")))

        dbManager.getContactFromJidAndContactType("jid@jid.im", Enums.Contacts.ContactTypes.PERSONAL)
                .test()
                .assertNoErrors()

        verify(mockContactDao).getContact("jid@jid.im", Enums.Contacts.ContactTypes.PERSONAL)
    }

    @Test
    fun getContactFromContactTypeId_returnsContact() {
        dbManager.setContactDao(mockContactDao)

        whenever(mockContactDao.getContactFromContactTypeId("123456")).thenReturn(Single.just(NextivaContact("Hi")))

        dbManager.getContactFromContactTypeId("123456")
                .test()
                .assertNoErrors()

        verify(mockContactDao).getContactFromContactTypeId("123456")
    }

    @Test
    fun clearAndResetAllTables_databaseNotNull_clearsEverything() {
        dbManager.setDatabase(mockDatabase)
        val queryArgumentCaptor: KArgumentCaptor<SimpleSQLiteQuery> = argumentCaptor()

        dbManager.clearAndResetAllTables()
        shadowOf(getMainLooper()).idle()

        verify(mockDatabase).beginTransaction()
        verify(mockDatabase).clearAllTables()
        verify(mockDatabase).query(queryArgumentCaptor.capture(), signal = eq(null))
        verify(mockDatabase).setTransactionSuccessful()
        verify(mockDatabase).endTransaction()

        assertEquals("DELETE FROM sqlite_sequence", queryArgumentCaptor.firstValue.sql)
    }

    @Test
    fun clearAndResetAllTables_databaseNull_returnsFalse() {
        dbManager.setDatabase(null)

        assertFalse(dbManager.clearAndResetAllTables())
    }

    //TODO:Causing Jenkins to run out of memory

//    @Test
//    fun updatePresence_presenceNotNull_updatesPresence() {
//        dbManager.setPresenceDao(mockPresenceDao)
//        dbManager.setContactDao(mockContactDao)
//
//        val testContact = ContactLists.getNextivaContactTestList()[0]
//        val nextivaPresence = DbPresence(testContact.jid,
//                testContact.presence!!.state,
//                testContact.presence!!.priority,
//                testContact.presence!!.status,
//                Enums.Contacts.PresenceTypes.UNAVAILABLE)
//
//        dbManager.updatePresence(nextivaPresence, CompositeDisposable())
//
//        verify(mockPresenceDao).updatePresenceFromJidDisposable(testContact.presence!!.state, Enums.Contacts.PresenceTypes.UNAVAILABLE, testContact.presence!!.priority,
//                testContact.presence!!.status, testContact.jid)
//    }

    @Test
    fun insertCallLogs_insertsCallLog() {
        dbManager.setCallLogsDao(mockCallLogsDao)
        val mockCallLogEntries: ArrayList<DbCallLogEntry> = mock()

        dbManager.insertCallLogs(mockCallLogEntries).subscribe()

        verify(mockCallLogsDao).replaceCallLogs(mockCallLogEntries)
    }

    @Test
    fun getCallLogEntriesLiveData_getsCallLogEntriesLiveData() {
        dbManager.setCallLogsDao(mockCallLogsDao)
        val mockStringList: List<String> = mock()

        dbManager.getCallLogEntriesLiveData(mockStringList)

        verify(mockCallLogsDao).getCallLogEntriesLiveData(mockStringList)
    }

    @Test
    fun deleteCallLogByCallLogId_deletesCallLog() {
        dbManager.setCallLogsDao(mockCallLogsDao)

        dbManager.deleteCallLogByCallLogId("callLogId").subscribe()

        val callLogIdArgumentCaptor: KArgumentCaptor<String> = argumentCaptor()

        verify(mockCallLogsDao).deleteCallLogFromId(callLogIdArgumentCaptor.capture())

        assertEquals("callLogId", callLogIdArgumentCaptor.firstValue)
    }

    @Test
    fun deleteCallLogs_deletesCallLogs() {
        dbManager.setCallLogsDao(mockCallLogsDao)

        dbManager.deleteCallLogs().subscribe()

        verify(mockCallLogsDao).deleteCallLogs()
    }

    @Test
    fun markAllCallLogEntriesAsRead_callsToCallLogsDao() {
        dbManager.setCallLogsDao(mockCallLogsDao)

        dbManager.markAllCallLogEntriesRead().subscribe()

        verify(mockCallLogsDao).markAllCallLogEntriesAsRead()
    }

    @Test
    fun markCallLogEntryAsRead_callsToCallLogsDao() {
        dbManager.setCallLogsDao(mockCallLogsDao)

        dbManager.markCallLogEntryRead("123").subscribe()

        verify(mockCallLogsDao).markCallLogEntryAsRead("123")
    }

    @Test
    fun getUnreadCallLogEntriesCount_getsCountFromDao() {
        dbManager.setCallLogsDao(mockCallLogsDao)

        dbManager.unreadCallLogEntriesCount

        verify(mockCallLogsDao).unreadCallLogEntriesCount
    }

    @Test
    fun isCacheExpired_rosterKeyNotExpired_returnsFalse() {
        whenever(sharedPreferenceManager.getLong(SharedPreferencesManager.ROSTER_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, 0)).thenReturn(1000000000)
        whenever(calendarManager.nowMillis).thenReturn(0)

        assertFalse(dbManager.isCacheExpired(SharedPreferencesManager.ROSTER_CONTACTS))
    }

    @Test
    fun isCacheExpired_enterpriseKeyNotExpired_returnsFalse() {
        whenever(sharedPreferenceManager.getLong(SharedPreferencesManager.ENTERPRISE_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, 0)).thenReturn(1000000000)
        whenever(calendarManager.nowMillis).thenReturn(0)

        assertFalse(dbManager.isCacheExpired(SharedPreferencesManager.ENTERPRISE_CONTACTS))
    }

    @Test
    fun isCacheExpired_rosterKeyExpired_returnsTrue() {
        whenever(sharedPreferenceManager.getLong(SharedPreferencesManager.ROSTER_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, 0)).thenReturn(0)
        whenever(calendarManager.nowMillis).thenReturn(100000000000000000)

        assertTrue(dbManager.isCacheExpired(SharedPreferencesManager.ROSTER_CONTACTS))
    }

    @Test
    fun isCacheExpired_enterpriseKeyExpired_returnsTrue() {
        whenever(sharedPreferenceManager.getLong(SharedPreferencesManager.ENTERPRISE_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, 0)).thenReturn(0)
        whenever(calendarManager.nowMillis).thenReturn(1000000000000000000)

        assertTrue(dbManager.isCacheExpired(SharedPreferencesManager.ENTERPRISE_CONTACTS))
    }

    @Test
    fun expireContactsCache_expiresContactsCache() {
        dbManager.expireContactCache()

        verify(sharedPreferenceManager).setLong(SharedPreferencesManager.ROSTER_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, 0)
        verify(sharedPreferenceManager).setLong(SharedPreferencesManager.ROSTER_CONTACTS + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX, 0)
    }

    //TODO:Causing Jenkins to run out of memory

//    fun saveChatMessage_noMessageExists_insertsMessage() {
//        dbManager.setMessagesDao(mockMessagesDao)
//        dbManager.setVCardDao(mockVCardDao)
//
//        whenever(mockMessagesDao.getMessageWithTimestampAndBody("1234", "Hi")).thenReturn(null)
//
//        val chatMessage = ChatMessage()
//        chatMessage.timestamp = 2234
//        chatMessage.messageId = "message_id"
//        chatMessage.body = "message body"
//        chatMessage.to = "jid"
//        chatMessage.from = "from"
//        chatMessage.setIsRead(true)
//
//        dbManager.saveChatMessage(chatMessage)
//
//        verify(mockMessagesDao).insert(any())
//    }
//
//
//    @Test
//    fun markAllMessagesRead_marksMessagesRead() {
//        dbManager.setMessagesDao(mockMessagesDao)
//
//        dbManager.markAllMessagesRead()
//
//        verify(mockMessagesDao).markAllMessagesRead()
//    }
//
//
//    @Test
//    fun markMessagesFromSenderRead_marksMessagesFromSenderRead() {
//        dbManager.setMessagesDao(mockMessagesDao)
//
//        dbManager.markMessagesFromSenderRead("sender")
//
//        verify(mockMessagesDao).markMessagesFromSenderRead("sender")
//    }

}