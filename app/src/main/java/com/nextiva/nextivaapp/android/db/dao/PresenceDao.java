package com.nextiva.nextivaapp.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceResponse;

import java.util.ArrayList;

import io.reactivex.Single;

@Dao
public abstract class PresenceDao {

    @Transaction
    public void updatePresences(ArrayList<ConnectPresenceResponse> presences, ContactDao contactDao) {
        for (ConnectPresenceResponse presence : presences) {
            long rowsUpdate = updatePresenceForUserId(presence.getPresenceState(),
                    presence.getCustomMessage(),
                    presence.getUuid(),
                    presence.getUserSettingStatusExpiresAt(),
                    presence.getInCall());

            if (rowsUpdate == 0) {
                int contactId = (int) contactDao.getContactIdInThread(presence.getUuid());

                if (contactId != 0) {
                    insertPresence(new DbPresence(contactId,
                            presence.getUuid(),
                            presence.getPresenceState(),
                            presence.getCustomMessage(),
                            presence.getUserSettingStatusExpiresAt(),
                            presence.getInCall()));
                }
            }
        }
    }

    @Insert
    public abstract void insertPresence(DbPresence presence);

    @Query("UPDATE presences " +
            "SET presence_state = :presenceState, presence_type = :presenceType, priority = :priority, status_text = :statusText " +
            "WHERE jid COLLATE NOCASE = :jid")
    public abstract int updatePresenceFromJidDisposable(int presenceState, int presenceType, int priority, String statusText, String jid);

    @Query("UPDATE presences " +
            "SET presence_state = :presenceState, presence_type = :presenceType, priority = :priority, status_text = :statusText, transaction_id = :transactionId " +
            "WHERE jid COLLATE NOCASE = :jid")
    public abstract void updatePresenceFromJid(int presenceState, int presenceType, int priority, String statusText, String jid, String transactionId);

    @Query("UPDATE presences " +
            "SET presence_state = :presenceState, status_text = :status " +
            "WHERE user_id = :userId")
    public abstract int updatePresenceForUserId(int presenceState, String status, String userId);

    @Query("UPDATE presences " +
            "SET presence_state = :presenceState, status_text = :status, status_expiry_time = :presenceExpiresAt, in_call = :inCall " +
            "WHERE user_id = :userId")
    public abstract int updatePresenceForUserId(int presenceState, String status, String userId, String presenceExpiresAt, Boolean inCall);

    @Query("SELECT * FROM presences WHERE jid COLLATE NOCASE = :jid")
    public abstract LiveData<DbPresence> getPresenceFromUserJidLiveData(String jid);

    @Transaction
    @Query("DELETE FROM presences WHERE presences.contact_id IN ( SELECT contacts.id FROM contacts WHERE contacts.contact_type IN (:contactType) ) AND transaction_id != :transactionId;")
    public abstract void clearOldPresencesByTransactionId(int[] contactType, String transactionId);

    @Transaction
    @Query("DELETE FROM presences")
    public abstract void deleteAllPresences();

    @Query("SELECT * FROM presences WHERE jid COLLATE NOCASE = :jid LIMIT 1")
    public abstract Single<DbPresence> getPresenceByJid(String jid);

    @Query("SELECT * FROM presences WHERE jid COLLATE NOCASE = :jid LIMIT 1")
    public abstract DbPresence getPresenceByJidInThread(String jid);

    @Query("SELECT * FROM presences WHERE user_id = :contactTypeId LIMIT 1")
    public abstract DbPresence getPresenceByContactTypeId(String contactTypeId);

    @Query("SELECT * FROM presences WHERE user_id = :contactTypeId LIMIT 1")
    public abstract LiveData<DbPresence> getPresenceByContactTypeIdLiveData(String contactTypeId);
}
