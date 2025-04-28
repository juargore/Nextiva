package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbDate

@Dao
public interface DateDao {
    @Insert
    fun insertDate(date: DbDate)

    @Insert
    fun insertAllDates(dates: List<DbDate>)

    @Query("SELECT id FROM dates WHERE contact_id = :contactId AND date = :date")
    fun getDateId(contactId: Int, date: String?): Int

    @Query("""UPDATE dates
            SET type = :type, transaction_id = :transactionId 
            WHERE contact_id = :contactId AND date = :date""")
    fun updateDate(contactId: Int, date: String?, @Enums.Contacts.DateType.Type type: Int, transactionId: String?)

    @Transaction
    @Query("DELETE FROM dates WHERE dates.contact_id IN ( SELECT contacts.id FROM contacts WHERE contacts.contact_type IN (:contactType) ) AND transaction_id != :transactionId;")
    fun clearOldDatesByTransactionId(contactType: IntArray?, transactionId: String?)

    @Transaction
    @Query("DELETE FROM dates WHERE contact_id = :contactId")
    fun deleteDates(contactId: Int)

    @Query("DELETE FROM dates WHERE transaction_id != :transactionId")
    fun deleteDates(transactionId: String?)
}