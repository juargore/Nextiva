package com.nextiva.nextivaapp.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.EmailAddress;

import java.util.List;

@Dao
public interface EmailDao {
    @Insert
    void insertEmail(EmailAddress email);

    @Insert
    void insertAllEmails(List<EmailAddress> emails);

    @Query("SELECT id FROM emails WHERE contact_id = :contactId AND address = :address")
    int getEmailId(int contactId, String address);

    @Query("UPDATE emails " +
            "SET type = :type, label = :label, transaction_id = :transactionId " +
            "WHERE contact_id = :contactId AND address = :address")
    void updateEmail(int contactId, String address, @Enums.Contacts.EmailTypes.Type int type, String label, String transactionId);

    @Transaction
    @Query("DELETE FROM emails WHERE emails.contact_id IN ( SELECT contacts.id FROM contacts WHERE contacts.contact_type IN (:contactType) ) AND transaction_id != :transactionId;")
    void clearOldEmailsByTransactionId(int[] contactType, String transactionId);

    @Transaction
    @Query("DELETE FROM emails WHERE contact_id = :contactId")
    void deleteEmails(int contactId);

    @Query("DELETE FROM emails WHERE transaction_id != :transactionId")
    void deleteEmails(String transactionId);
}
