package com.nextiva.nextivaapp.android.db.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nextiva.nextivaapp.android.db.model.DbSession;

import java.util.List;

import io.reactivex.Maybe;
import kotlinx.coroutines.flow.Flow;

@Dao
public interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSession(DbSession dbSession);

    @Query("SELECT * FROM session WHERE `key` = :key")
    Maybe<DbSession> getSessionFromKey(String key);

    @Query("SELECT * FROM session WHERE `key` = :key")
    DbSession getSessionFromKeyInThread(String key);

    @Query("UPDATE session SET value = :value WHERE `key` = :key")
    void updateValue(String key, String value);

    @Query("SELECT * FROM session WHERE `key` = :key")
    LiveData<DbSession> getSessionLiveDataFromKey(String key);

    @Query("SELECT * FROM session WHERE `key` = :key")
    Flow<DbSession> getSessionFlowFromKey(String key);

    @NonNull
    @Query("SELECT * FROM session WHERE `key` IN (:keys)")
    LiveData<List<DbSession>> getSessionLiveDataFromMultipleKeys(@NonNull List<String> keys);

    @NonNull
    @Query("SELECT SUM(value) FROM session WHERE `key` IN (:keys)")
    LiveData<Integer> getTotalUnreadNotificationsLiveDataFromMultipleKeys(@NonNull List<String> keys);

    @Query("SELECT value FROM session WHERE `key` = :key")
    LiveData<String> getNewVoicemailCount(String key);

}

