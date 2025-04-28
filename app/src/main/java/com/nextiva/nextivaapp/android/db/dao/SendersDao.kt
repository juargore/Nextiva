package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.nextiva.nextivaapp.android.db.model.DbSender


@Dao
interface SendersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(senders: DbSender): Long
}
