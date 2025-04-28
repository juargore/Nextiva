package com.nextiva.nextivaapp.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.db.model.PhoneNumber;

import java.util.List;

@Dao
public interface PhoneDao {
    @Insert
    void insertNumber(PhoneNumber phone);

    @Insert
    void insertAllNumbers(List<PhoneNumber> phones);

    @Query("SELECT id FROM phones WHERE contact_id = :contactId AND number = :number")
    int getNumberId(int contactId, String number);

    @Query("SELECT * FROM phones WHERE contact_id = :contactId")
    List<PhoneNumber> getPhoneNumbers(int contactId);

    @Query("SELECT * FROM phones ")
    List<PhoneNumber> getAllPhoneNumbers();

    @Query("UPDATE phones " +
            "SET stripped_number = :strippedNumber, type = :type, label = :label, pin_one = :pinOne, pin_two = :pinTwo, transaction_id = :transactionId " +
            "WHERE contact_id = :contactId AND number = :number")
    void updateNumber(int contactId, String number, String strippedNumber, int type, String label, String pinOne, String pinTwo, String transactionId);

    @Transaction
    @Query("DELETE FROM phones WHERE phones.contact_id IN ( SELECT contacts.id FROM contacts WHERE contacts.contact_type IN (:contactType) ) AND transaction_id != :transactionId;")
    void clearOldPhonesByTransactionId(int[] contactType, String transactionId);

    @Transaction
    @Query("DELETE FROM phones WHERE contact_id = :contactId")
    void deleteNumbers(int contactId);

    @Transaction
    @Query("DELETE FROM phones WHERE contact_id = :contactId AND stripped_number = :stripped_number")
    void deleteContactStrippedNumber(int contactId, String stripped_number);

    @Transaction
    @Query("DELETE FROM phones WHERE id = :rowId")
    void deletePhoneByRowId(Long rowId);


    @Query("DELETE FROM phones WHERE transaction_id != :transactionId")
    void deletePhones(String transactionId);
}
