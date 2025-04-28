package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nextiva.nextivaapp.android.db.model.DbLogging

@Dao
interface LoggingDao {

    @Insert
    fun insertLog(logging: DbLogging)

    @Query("SELECT * FROM logging")
    fun get100Logs(): List<DbLogging>

    @Query("SELECT * FROM logging")
    fun getAllLogs(): List<DbLogging>


    @Query("SELECT * FROM logging LIMIT :count")
    fun getLimitedLogs(count: Int): List<DbLogging>

    @Query("SELECT COUNT(id) FROM logging")
    fun getLogsCount(): Int

    @Transaction
    @Query("DELETE FROM logging")
    fun clearAllLogs()

    @Transaction
    @Query("DELETE FROM logging WHERE id IN (SELECT id FROM logging LIMIT :count)")
    fun clearPostedLogs(count: Int)

}