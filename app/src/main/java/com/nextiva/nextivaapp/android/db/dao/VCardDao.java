/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.db.dao;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbVCard;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by joedephillipo on 3/1/18.
 */

@Dao
public interface VCardDao {

    @Query("SELECT vcards.*, contacts.jid FROM vcards INNER JOIN contacts ON contacts.id = contact_id " +
            "WHERE contacts.jid COLLATE NOCASE = :jid AND contacts.contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + " LIMIT 1")
    Single<DbVCard> getSingleVCardFromUserJid(String jid);

    @Query("SELECT vcards.* FROM vcards INNER JOIN contacts ON contacts.id = contact_id " +
            "WHERE contacts.jid COLLATE NOCASE = :jid AND contacts.contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + " LIMIT 1")
    Maybe<DbVCard> getVCardFromUserJid(String jid);

    @Query("SELECT vcards.* FROM vcards INNER JOIN contacts ON contacts.id = contact_id WHERE contacts.jid COLLATE NOCASE = :jid  " +
            "AND contacts.contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + " LIMIT 1")
    DbVCard getVCardFromUserJidInThread(String jid);

    @Query("SELECT vcards.* FROM vcards INNER JOIN contacts ON contacts.id = contact_id WHERE contacts.jid COLLATE NOCASE = :jid  " +
            "AND contacts.contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + " LIMIT 1")
    LiveData<DbVCard> getVCardFromUserJidLiveData(String jid);

    @Query("SELECT vcards.photo_data, results.jid FROM vcards " +
            "LEFT JOIN (SELECT contacts.id AS contact_id, jid FROM " +
            "contacts WHERE contacts.jid COLLATE NOCASE IN (SELECT DISTINCT message_from FROM messages WHERE chat_with = :chatWith) AND contacts.contact_type = 2) AS results " +
            "WHERE results.contact_id = vcards.contact_id")
    Single<List<DbVCard>> getVCardsForConversations(String chatWith);

    @Query("UPDATE vcards SET photo_data = :photoData, transaction_id = :transactionId" +
            " WHERE contact_id = (SELECT id FROM contacts WHERE contacts.jid COLLATE NOCASE = :jid  AND contacts.contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + " )")
    void updateVCardWithNewAvatar(String jid, @Nullable byte[] photoData, String transactionId);

    @Transaction
    @Query("DELETE FROM vcards WHERE vcards.contact_id IN ( SELECT contacts.id FROM contacts WHERE contacts.contact_type IN (:contactType) ) AND transaction_id != :transactionId;")
    void clearOldVCardsByTransactionId(int[] contactType, String transactionId);

    @Insert
    void insertVCard(DbVCard vCard);
}
