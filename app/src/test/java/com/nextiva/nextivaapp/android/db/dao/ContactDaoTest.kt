package com.nextiva.nextivaapp.android.db.dao

import androidx.room.EmptyResultSetException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.AppDatabase
import com.nextiva.nextivaapp.android.db.model.DbContact
import org.junit.Test

class ContactDaoTest : BaseRobolectricTest() {

    private lateinit var testDatabase: AppDatabase

    private lateinit var dbContact: DbContact

    override fun setup() {
        super.setup()
        testDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).allowMainThreadQueries().build()

        dbContact = DbContact(2000, "123456", Enums.Contacts.ContactTypes.PERSONAL, "jid@jid.com", "display", "First", "Last",
                "HFirst", "HLast", "Title", "Company", 1, "Group Id", "Short jid", 1, "First Last", 0, "F", "display",
                null, null, null, null, null, null, null, null, null)
    }

    override fun after() {
        super.after()
        testDatabase.clearAllTables()
        testDatabase.close()
    }

    @Test
    fun insertContact() {
        testDatabase.contactDao().insertContact(dbContact)

        testDatabase.contactDao().getContact("jid@jid.com")
                .test()
                .assertNoErrors()
    }

    @Test
    fun getUserNameFromJid_returnsDisplayName() {
        testDatabase.contactDao().insertContact(dbContact)

        testDatabase.contactDao().getUserNameFromJid("jid@jid.com")
                .test()
                .assertNoErrors()
                .assertResult("display")
    }

    @Test
    fun getContactId_returnsContactId() {
        testDatabase.contactDao().insertContact(dbContact)

        testDatabase.contactDao().getContactId("123456")
                .test()
                .assertNoErrors()
                .assertResult(2000)
    }

    @Test
    fun getContact_passesInJid_returnsContact() {
        testDatabase.contactDao().insertContact(dbContact)

        testDatabase.contactDao().getContact("jid@jid.com")
                .test()
                .assertNoErrors()
    }


    @Test
    fun getContact_passesInJidAndContactType_returnsContact() {
        testDatabase.contactDao().insertContact(dbContact)

        testDatabase.contactDao().getContact("jid@jid.com", Enums.Contacts.ContactTypes.PERSONAL)
                .test()
                .assertNoErrors()
    }

    @Test
    fun getContactFromContactTypeId_returnsContact() {
        testDatabase.contactDao().insertContact(dbContact)

        testDatabase.contactDao().getContactFromContactTypeId("123456")
                .test()
                .assertNoErrors()
    }

    @Test
    fun updateContact_updatesExistingContact() {
        val updatedDbContact = DbContact(2000, "234567", Enums.Contacts.ContactTypes.PERSONAL, "jid2@jid.com", "display", "First", "Last",
                "HFirst", "HLast2", "Title2", "Company2", 1, "Group Id", "Short jid", 1, "First Last", 0, "F", "display",
                null, null, null, null, null, null, null, null, null)

        testDatabase.contactDao().insertContact(dbContact)

        testDatabase.contactDao().updateContact(2000, "234567", Enums.Contacts.ContactTypes.PERSONAL, "jid2@jid.com", "display", "First", "Last",
                "HFirst", "HLast2", "Title2", "Company2", 1, "Group Id", "Short jid", 1, "First Last", "F", "display",
                null, null, null, null, null, null, null, null, 0, null)

        testDatabase.contactDao().getContact("jid2@jid.com")
                .test()
                .assertNoErrors()
    }

    @Test
    fun deleteContactByContactTypeId_deletedContact() {
        testDatabase.contactDao().insertContact(dbContact)

        testDatabase.contactDao().getContact("jid@jid.com")
                .test()
                .assertNoErrors()

        testDatabase.contactDao().deleteContactByContactTypeId("123456")

        testDatabase.contactDao().getContact("jid@jid.com")
                .test()
                .assertError(EmptyResultSetException::class.java)

    }
}
