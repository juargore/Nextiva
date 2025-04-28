/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbContact;
import com.nextiva.nextivaapp.android.models.NextivaContact;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by joedephillipo on 3/8/18.
 */

@Dao
public interface ContactDao {

    @Query("SELECT display_name FROM contacts " +
            "WHERE jid COLLATE NOCASE = :jid " +
            "LIMIT 1")
    Maybe<String> getUserNameFromJid(String jid);

    @Query("SELECT ui_name FROM contacts " +
            "WHERE jid COLLATE NOCASE = :jid " +
            "AND contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + " " +
            "LIMIT 1")
    String getRosterContactUINameFromJid(String jid);

    @Query("SELECT ui_name FROM contacts " +
            "WHERE jid COLLATE NOCASE = :jid " +
            "COLLATE NOCASE " +
            "LIMIT 1")
    String getUINameFromJid(String jid);

    @Query("SELECT COUNT(*) " +
            "FROM contacts " +
            "WHERE jid COLLATE NOCASE = :jid AND contact_type = " + Enums.Contacts.ContactTypes.PERSONAL)
    int doesRosterContactWithJidExist(String jid);

    @Query("SELECT id FROM contacts WHERE contact_type_id = :contactTypeId LIMIT 1")
    Single<Long> getContactId(String contactTypeId);

    @Query("SELECT id FROM contacts WHERE contact_type_id = :contactTypeId LIMIT 1")
    long getContactIdInThread(String contactTypeId);

    @Query("SELECT id FROM contacts WHERE jid COLLATE NOCASE = :jid AND contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + " LIMIT 1")
    Single<Integer> getRosterContactIdFromJid(String jid);

    @Query("SELECT * FROM contacts WHERE jid COLLATE NOCASE = :jid LIMIT 1")
    Single<NextivaContact> getContact(String jid);

    @Query("SELECT COUNT(*) " +
            "FROM contacts " +
            "WHERE ui_name = :uiName AND contact_type = " + Enums.Contacts.ContactTypes.LOCAL +
            " COLLATE NOCASE")
    int doesLocalContactWithUiNameExist(String uiName);

    @Query("SELECT COUNT(*) " +
            "FROM contacts " +
            "WHERE lookup_key = :lookupKey AND contact_type = " + Enums.Contacts.ContactTypes.CONNECT_SHARED +
            " COLLATE NOCASE")
    int doesLocalContactWithLookupKeyExist(String lookupKey);

    @Query("SELECT * FROM contacts WHERE jid COLLATE NOCASE = :jid AND contact_type = :contactType LIMIT 1")
    Single<NextivaContact> getContact(String jid, @Enums.Contacts.ContactTypes.Type int contactType);

    @Query("SELECT * FROM contacts WHERE contact_type_id = :contactTypeId")
    Single<NextivaContact> getContactFromContactTypeId(String contactTypeId);

    @Query("SELECT * FROM contacts WHERE contact_type_id = :contactTypeId")
    LiveData<NextivaContact> getContactFromContactTypeIdLiveData(String contactTypeId);

    @Query("SELECT contact_type_id FROM contacts WHERE contact_type = " + Enums.Contacts.ContactTypes.PERSONAL)
    List<String> getRosterContactIds();

    //TODO this could be replaced with an @Update annotated method like @Insert below
    @Query("UPDATE contacts " +
            "SET contact_type_id = :contactTypeId, contact_type = :contactType, jid = :jid, display_name = :displayName, first_name = :firstName, last_name = :lastName," +
            "hiragana_first_name = :hiraganaFirstName, hiragana_last_name = :hiraganaLastName, title = :title, company = :company, is_favorite = :favorite, group_id = :groupId," +
            "short_jid = :shortJid, subscription_state = :subscriptionState, sort_name = :sortName, sort_name_first_initial = :sortFirstInitial, ui_name = :uiName, " +
            "transaction_id = :transactionId, lookup_key = :lookupKey, website = :website, department = :department, description = :description, last_modified_on = :lastModifiedOn, " +
            "last_modified_by = :lastModifiedBy, created_by = :createdBy, sort_group = :sortGroup, aliases = :aliases " +
            "WHERE id = :contactId")
    void updateContact(Long contactId, String contactTypeId, int contactType, String jid, String displayName, String firstName, String lastName,
                       String hiraganaFirstName, String hiraganaLastName, String title, String company, int favorite, String groupId, String shortJid,
                       int subscriptionState, String sortName, String sortFirstInitial, String uiName, String website, String department, String description,
                       String createdBy, String lastModifiedBy, String lastModifiedOn, String lookupKey, String transactionId, Integer sortGroup, String aliases);

    @Query("DELETE FROM contacts WHERE jid COLLATE NOCASE = :jid COLLATE NOCASE AND contact_type = " + Enums.Contacts.ContactTypes.PERSONAL)
    void deleteRosterContactByJid(String jid);

    @Query("DELETE FROM contacts")
    void deleteAllContacts();

    @Query("DELETE FROM contacts WHERE contact_type_id = :contactTypeId")
    void deleteContactByContactTypeId(String contactTypeId);

    @Query("DELETE FROM contacts WHERE contact_type = :contactType")
    void deleteContactsByContactType(int contactType);

    @Query("UPDATE contacts SET subscription_state = :subscriptionState WHERE jid = :jid AND contact_type = " + Enums.Contacts.ContactTypes.PERSONAL)
    void updateContactSubscriptionState(int subscriptionState, String jid);

    @Transaction
    @Query("DELETE FROM contacts WHERE contact_type IN (:contactType) AND transaction_id != :transactionId")
    void clearOldContactsByTransactionId(int[] contactType, String transactionId);

    @Query("SELECT COUNT(*) " +
            "FROM contacts " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ", " +
            Enums.Contacts.ContactTypes.CONNECT_USER + ", " +
            Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW + ", " +
            Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS + ", " +
            Enums.Contacts.ContactTypes.CONNECT_TEAM + ", " +
            Enums.Contacts.ContactTypes.CONNECT_SHARED + ") AND " +
            "is_favorite == 1 " +
            " COLLATE NOCASE")
    LiveData<Integer> getConnectFavoritesCount();

    @Query("SELECT COUNT(*) " +
            "FROM contacts " +
            "WHERE contacts.contact_type IN (:types) " +
            "COLLATE NOCASE")
    LiveData<Integer> getConnectTypeCount(int[] types);

    @Query("UPDATE contacts " +
            "SET is_favorite = :isFavorite " +
            "WHERE contact_type_id = :contactTypeId")
    void setFavorite(String contactTypeId, boolean isFavorite);

    @Insert
    long insertContact(DbContact contact);

    @Query("DELETE FROM contacts WHERE transaction_id != :transactionId")
    void deleteContacts(String transactionId);

    @Query("SELECT COUNT(*) " +
    "FROM contacts " +
    "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.LOCAL + ") ")
    Integer getLocalContactsCount();

    @Query("SELECT contact_type_id " +
    "FROM contacts " +
    "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.CONNECT_USER)
    List<String> getTeammateContactIds();
}
