package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.nextiva.nextivaapp.android.db.model.DbRecipient


@Dao
interface RecipientsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recipients: DbRecipient): Long

}
