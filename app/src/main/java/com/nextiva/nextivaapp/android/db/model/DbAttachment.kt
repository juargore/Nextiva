package com.nextiva.nextivaapp.android.db.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums.Attachment.ContentExtensionType.EXT_3GP
import com.nextiva.nextivaapp.android.constants.Enums.Attachment.ContentExtensionType.EXT_H263
import com.nextiva.nextivaapp.android.constants.Enums.Attachment.ContentExtensionType.EXT_H264
import com.nextiva.nextivaapp.android.constants.Enums.Attachment.ContentExtensionType.EXT_M4V
import com.nextiva.nextivaapp.android.constants.Enums.Attachment.ContentExtensionType.EXT_MP4
import com.nextiva.nextivaapp.android.constants.Enums.Attachment.ContentMajorType.AUDIO
import com.nextiva.nextivaapp.android.constants.Enums.Attachment.ContentMajorType.IMAGE
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.util.MessageUtil
import org.threeten.bp.Instant

@Entity(tableName = DbConstants.TABLE_NAME_ATTACHMENTS,
        foreignKeys = [ForeignKey(entity = DbSmsMessage::class,
                parentColumns = [DbConstants.SMS_MESSAGE_COLUMN_NAME_ID],
                childColumns = [DbConstants.ATTACHMENTS_COLUMN_SMS_ID],
                onDelete = ForeignKey.CASCADE)])
data class DbAttachment(@PrimaryKey(autoGenerate = true)
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_NAME_ID) var id: Long?,
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_LINK) var link: String?,
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_CONTENT_TYPE) var contentType: String?,
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_SMS_ID) var sms_id: Long?,
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_CONTENT_DATA, typeAffinity = ColumnInfo.BLOB) var contentData: ByteArray?,
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_UPLOADED_DATE) var uploadedDate: Instant?,
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_THUMB_NAIL_LINK) var thumbnailLink: String?,
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_FILE_DURATION) var fileDuration: Long?,
                        @ColumnInfo(name = DbConstants.ATTACHMENTS_COLUMN_FILE_NAME) var fileName: String?) {

    fun getBodyText(context: Context): String {
        return when {
            contentType?.contains(AUDIO) == true -> context.getString(R.string.chat_details_shared_an_audio_file)
            contentType?.contains(IMAGE) == true -> context.getString(R.string.chat_details_shared_an_image)
            else -> ""
        }
    }

    fun getFileSupportedPlaceholderDrawableId(): Int {
        return when {
            contentType?.contains(IMAGE) == true || MessageUtil.isFileExtensionSMSSupportedImageType(fileName) -> R.drawable.ic_photo
            contentType?.contains(AUDIO) == true || MessageUtil.isFileExtensionSMSSupportedAudioType(fileName) -> R.drawable.ic_soundfile
            else -> R.drawable.ic_soundfile
        }
    }


    fun getFileUnsupportedPlaceholderDrawableId(): Int {
        return R.drawable.ic_unsupported_file
    }


    private fun isFileExtensionVideo(): Boolean {
        fileName?.let { fileName ->
            return when {
                fileName.endsWith(EXT_3GP) ||
                        fileName.endsWith(EXT_H263) ||
                        fileName.endsWith(EXT_H264) ||
                        fileName.endsWith(EXT_MP4) ||
                        fileName.endsWith(EXT_M4V) -> true
                else -> false
            }
        }

        return false
    }
}