package com.nextiva.nextivaapp.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.db.model.Address;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface AddressDao {

    @Insert
    void insertAddress(Address address);

    @Insert
    void insertAllAddress(List<Address> addresses);

    @Query("UPDATE postal_addresses " +
            "SET address_line_one = :addressLineOne, address_line_two = :addressLineTwo, postal_code = :postalCode, city = :city, " +
            "region = :region, country = :country, location = :location, type = :type, transaction_id = :transactionId " +
            "WHERE contact_id = :contactId")
    void updateAddressFromContactId(String addressLineOne, String addressLineTwo, String postalCode, String city, String region, String country, String location,
                                    int type, String transactionId, int contactId);

    @Query("SELECT * FROM postal_addresses " +
            "WHERE contact_id = :contactId " +
            "LIMIT 1")
    Single<Address> getAddressByContactId(int contactId);

    @Transaction
    @Query("DELETE FROM postal_addresses WHERE postal_addresses.contact_id IN ( SELECT contacts.id FROM contacts WHERE contacts.contact_type IN (:contactType) ) AND transaction_id != :transactionId;")
    void clearOldAddressesByTransactionId(int[] contactType, String transactionId);

    @Query("SELECT id FROM postal_addresses WHERE contact_id = :contactId AND type = :type")
    int getAddressId(int contactId, int type);

    @Transaction
    @Query("DELETE FROM postal_addresses WHERE contact_id = :contactId")
    void deleteAddresses(int contactId);

    @Query("DELETE FROM postal_addresses WHERE transaction_id != :transactionId")
    void deleteAddresses(String transactionId);
}
