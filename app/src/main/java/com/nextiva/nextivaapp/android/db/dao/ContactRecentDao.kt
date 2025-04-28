package com.nextiva.nextivaapp.android.db.dao;

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nextiva.nextivaapp.android.db.AppDatabase
import com.nextiva.nextivaapp.android.db.model.DbContactRecent
import com.nextiva.nextivaapp.android.models.NextivaContact
import java.util.List
import java.util.UUID

@Dao
interface ContactRecentDao {

    @Query("SELECT contacts.* FROM contacts " +
                "INNER JOIN contacts_recent ON contacts_recent.contact_type_id = contacts.contact_type_id " +
                "ORDER BY contacts_recent.id ASC")
    fun getContactLiveData() : LiveData<List<NextivaContact>>

    @Query("SELECT contacts.* FROM contacts " +
            "INNER JOIN contacts_recent ON contacts_recent.contact_type_id = contacts.contact_type_id " +
            "WHERE contacts.contact_type IN (:types)" +
            "ORDER BY contacts_recent.id ASC")
    fun getContactPagingSource(types: IntArray) : PagingSource<Int, NextivaContact>

    @Insert
    fun insertContact(contact: DbContactRecent)

    @Query("DELETE FROM contacts_recent WHERE transaction_id != :transactionId")
    fun deleteContacts(transactionId: String)

    @Transaction
    fun insertContacts(contacts: List<NextivaContact>, appDatabase: AppDatabase, transactionId: String) {
        deleteContacts(UUID.randomUUID().toString())
        contacts.forEach { contact ->
            insertContact(DbContactRecent(contact, transactionId))
        }
    }

}
