package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.SocialMediaAccount

@Dao
public interface SocialMediaAccountDao {
    @Insert
    fun insertAccount(account: SocialMediaAccount)

    @Insert
    fun insertAllAccounts(accounts: List<SocialMediaAccount>)

    @Query("SELECT id FROM social_media_accounts WHERE contact_id = :contactId AND link = :link")
    fun getAccountId(contactId: Int, link: String?): Int

    @Query("""UPDATE social_media_accounts
            SET type = :type, transaction_id = :transactionId 
            WHERE contact_id = :contactId AND link = :link""")
    fun updateAccount(contactId: Int, link: String?, @Enums.Contacts.SocialMediaType.Type type: Int, transactionId: String?)

    @Transaction
    @Query("DELETE FROM social_media_accounts WHERE social_media_accounts.contact_id IN ( SELECT contacts.id FROM contacts WHERE contacts.contact_type IN (:contactType) ) AND transaction_id != :transactionId;")
    fun clearOldAccountsByTransactionId(contactType: IntArray?, transactionId: String?)

    @Transaction
    @Query("DELETE FROM social_media_accounts WHERE contact_id = :contactId")
    fun deleteAccounts(contactId: Int)

    @Query("DELETE FROM social_media_accounts WHERE transaction_id != :transactionId")
    fun deleteAccounts(transactionId: String?)
}