package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextiva.nextivaapp.android.db.model.DbAttachment
import io.reactivex.Single

@Dao
interface AttachmentsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(attachment: DbAttachment): Long

    @Query("UPDATE attachments SET content_data = :contentData WHERE link = :link")
    fun updateAttachment(contentData: ByteArray, link: String)

    @Query("UPDATE attachments SET file_duration = :duration WHERE link = :link")
    fun updateFileDuration(link: String, duration: Long)

    @Query("SELECT content_data FROM attachments WHERE link = :link LIMIT 1")
    fun getContentData(link: String): Single<ByteArray>

    @Query("SELECT * FROM attachments WHERE sms_id = :smsId AND file_name = :fileName LIMIT 1")
    fun getAttachment(smsId: Long, fileName: String): DbAttachment?

    @Query("UPDATE attachments SET link = :link WHERE sms_id = :sms_id")
    fun updateAttachment(link: String, sms_id: Long)

    @Query("SELECT * FROM attachments LIMIT 1")
    fun getFirstAttachment(): DbAttachment

    @Query("DELETE FROM attachments WHERE sms_id = :sms_id")
    fun deleteAttachments(sms_id: Long)
}