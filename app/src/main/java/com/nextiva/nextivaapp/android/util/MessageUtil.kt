package com.nextiva.nextivaapp.android.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import android.util.DisplayMetrics
import android.util.TypedValue
import android.webkit.MimeTypeMap
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.SmsParticipant
import io.reactivex.Single
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MessageUtil {

    companion object {
        @JvmStatic
        fun getFileExtensionFromMimeType(mimeType: String): String? {
            val mimeTypePart = mimeType.split("/")[0]
            val extension = mimeType.split("/")[1]

            when (mimeTypePart) {
                Enums.Attachment.ContentMajorType.IMAGE -> {
                    return when (extension) {
                        // BMP should be converted to PNG before it goes to the API
                        Enums.Attachment.ContentExtensionType.EXT_BMP -> Enums.Attachment.ContentExtensionType.EXT_PNG
                        Enums.Attachment.ContentExtensionType.EXT_X_MS_BMP -> Enums.Attachment.ContentExtensionType.EXT_PNG
                        Enums.Attachment.ContentExtensionType.EXT_JPG -> Enums.Attachment.ContentExtensionType.EXT_JPG
                        Enums.Attachment.ContentExtensionType.EXT_JPEG,
                        Enums.Attachment.ContentExtensionType.EXT_HEIC,
                        Enums.Attachment.ContentExtensionType.EXT_HEIF -> Enums.Attachment.ContentExtensionType.EXT_JPEG
                        Enums.Attachment.ContentExtensionType.EXT_PNG -> Enums.Attachment.ContentExtensionType.EXT_PNG
                        Enums.Attachment.ContentExtensionType.EXT_GIF -> Enums.Attachment.ContentExtensionType.EXT_GIF
                        else -> Enums.Attachment.ContentExtensionType.EXT_NONE
                    }
                }

                Enums.Attachment.ContentMajorType.AUDIO -> {
                    return when (extension) {
                        Enums.Attachment.ContentExtensionType.EXT_AMR -> Enums.Attachment.ContentExtensionType.EXT_AMR
                        Enums.Attachment.ContentExtensionType.EXT_3GPP -> Enums.Attachment.ContentExtensionType.EXT_3GP
                        Enums.Attachment.ContentExtensionType.EXT_M4A -> Enums.Attachment.ContentExtensionType.EXT_M4A
                        Enums.Attachment.ContentExtensionType.EXT_M4B -> Enums.Attachment.ContentExtensionType.EXT_M4B
                        Enums.Attachment.ContentExtensionType.EXT_MPEG -> Enums.Attachment.ContentExtensionType.EXT_MP3
                        Enums.Attachment.ContentExtensionType.EXT_XM4A -> Enums.Attachment.ContentExtensionType.EXT_M4A
                        Enums.Attachment.ContentExtensionType.EXT_MP3 -> Enums.Attachment.ContentExtensionType.EXT_MP3
                        Enums.Attachment.ContentExtensionType.EXT_MP4 -> Enums.Attachment.ContentExtensionType.EXT_M4A


                        // Android specific... vnd.wave and wave must be converted to "wav" before sending.
                        Enums.Attachment.ContentExtensionType.EXT_VND_WAVE -> Enums.Attachment.ContentExtensionType.EXT_WAV
                        Enums.Attachment.ContentExtensionType.EXT_WAVE -> Enums.Attachment.ContentExtensionType.EXT_WAV
                        Enums.Attachment.ContentExtensionType.EXT_XWAVE -> Enums.Attachment.ContentExtensionType.EXT_WAV
                        Enums.Attachment.ContentExtensionType.EXT_WAV -> Enums.Attachment.ContentExtensionType.EXT_WAV

                        else -> Enums.Attachment.ContentExtensionType.EXT_NONE
                    }
                }
            }

            return Enums.Attachment.ContentExtensionType.EXT_NONE
        }

        @JvmStatic
        fun isFileTypeSupported(contentType: String): Boolean {
            return when (contentType) {
                Enums.Attachment.AttachmentContentType.IMAGE_BMP,
                Enums.Attachment.AttachmentContentType.IMAGE_GIF,
                Enums.Attachment.AttachmentContentType.IMAGE_JPEG,
                Enums.Attachment.AttachmentContentType.IMAGE_PNG,
                Enums.Attachment.AttachmentContentType.IMAGE_HEIC,
                Enums.Attachment.AttachmentContentType.IMAGE_HEIF,

                    //Audio
                Enums.Attachment.AttachmentContentType.AUDIO_XWAV,
                Enums.Attachment.AttachmentContentType.AUDIO_WAV,
                Enums.Attachment.AttachmentContentType.AUDIO_M4A,
                Enums.Attachment.AttachmentContentType.AUDIO_M4B,
                Enums.Attachment.AttachmentContentType.AUDIO_MP4,
                Enums.Attachment.AttachmentContentType.AUDIO_MPEG,
                Enums.Attachment.AttachmentContentType.AUDIO_AMR,
                Enums.Attachment.AttachmentContentType.AUDIO_M4R,
                Enums.Attachment.AttachmentContentType.AUDIO_XM4R,
                Enums.Attachment.AttachmentContentType.AUDIO_M4P,
                Enums.Attachment.AttachmentContentType.AUDIO_MP3 -> true
                else -> false
            }
        }

        fun isAudioFile(mimeType: String): Boolean {
            val mimeTypePart = mimeType.split("/")[0]
            if (mimeTypePart.contentEquals(Enums.Attachment.ContentMajorType.AUDIO)) {
                return true
            }

            return false
        }

        fun isImageFile(mimeType: String): Boolean {
            val mimeTypePart = mimeType.split("/")[0]
            if (mimeTypePart.contentEquals(Enums.Attachment.ContentMajorType.IMAGE)) {
                return true
            }

            return false
        }


        fun dpToPx(context: Context, valueInDp: Float): Float {
            val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
        }

        fun getDisplayNameStringList(phoneNumbersList: List<String>?, smsMessage: SmsMessage): List<SmsParticipant>? {
            val allParticipants: List<SmsParticipant>? = smsMessage.recipientParticipantsList?.let {
                smsMessage.sender?.plus(it)?.distinct()
            }

            if (allParticipants != null && allParticipants.isNotEmpty()) {
                return allParticipants.filter {
                    var isMatched = false
                    if (phoneNumbersList != null) {
                        for (phoneNumber in phoneNumbersList) {
                            if (CallUtil.getStrippedPhoneNumber(it.phoneNumber ?: "") == phoneNumber) {
                                isMatched = true
                                break
                            }
                        }
                    }
                    isMatched
                }
            }

            return null
        }

        fun getMimeType(uri: Uri, activity: Context): String? {
            var mimeType: String? = null

            try {
                val fileExtension = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
                    val contentResolver = activity.applicationContext.contentResolver
                    getFileExtensionFromMimeType(
                        contentResolver.getType(uri)
                            ?: Enums.Attachment.ContentExtensionType.EXT_NONE
                    )

                } else {
                    MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                }

                mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(fileExtension?.toLowerCase(Locale.ROOT))

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            if (mimeType != null) {
                return if (mimeType.contains(Enums.Attachment.ContentExtensionType.EXT_VND_WAVE) || mimeType.contains(Enums.Attachment.ContentExtensionType.EXT_XWAVE)) {
                    Enums.Attachment.AttachmentContentType.AUDIO_WAVE
                } else {
                    mimeType
                }
            }

            return mimeType
        }

        fun isFileExtensionSMSSupportedImageType(fileName: String?): Boolean {
            fileName?.let { fileName ->
                return when {
                    fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_BMP) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_DIB) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_GIF) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_JPG) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_JPEG) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_PNG) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_HEIC) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_HEIF) -> true
                    else -> false
                }
            }

            return false
        }

        fun isFileExtensionSMSSupportedAudioType(fileName: String?): Boolean {
            fileName?.let { fileName ->
                return when {
                    fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_AMR) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_3GA) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_M4A) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_M4B) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_M4P) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_M4R) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_VND_WAVE) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_WAV) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_WAVE) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_MP4) ||
                            fileName.endsWith(Enums.Attachment.ContentExtensionType.EXT_MP3) -> true
                    else -> false
                }
            }

            return false
        }

        fun getFileNameWithOutExtension(uri: Uri?, activity: Context): String? {
            if (uri == null) return null

            var result: String? = null
            if (uri.scheme == "content") {
                val cursor: Cursor? =
                    activity.applicationContext.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor?.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return FilenameUtils.removeExtension(result)
        }

        fun isAttachmentAPhotoFromCamera(attachmentName: String): Boolean {
            return attachmentName.startsWith("camera-")
        }

        private fun getFileExtensionFromUri(uri: Uri, activity: Context): String {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor: Cursor? =
                    activity.applicationContext.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor?.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return FilenameUtils.getExtension(result)
        }

        fun getAudioFileDuration(uriString: String, activity: Context): Long {
            return try {
                val uri = Uri.parse(uriString)
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(activity, uri)
                val durationStr: String? =
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                if (durationStr.isNullOrEmpty()) 0 else durationStr.toLong()
            } catch (e: java.lang.Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                0
            }
        }


        fun getFileExtension(uri: Uri, mimeType: String, context: Context): String? {
            if (mimeType.split("/").size > 1) {
                val mimeExt = mimeType.split("/")[1]
                val extension: String
                if (mimeExt == Enums.Attachment.ContentExtensionType.EXT_MP4 || mimeExt == Enums.Attachment.ContentExtensionType.EXT_MPEG) {
                    extension = getFileExtensionFromUri(uri, context)
                    if (!extension.isEmpty()) {
                        return extension
                    }
                }
                return getFileExtensionFromMimeType(mimeType)
            }
            return null
        }

        fun createAudioCacheFile(context: Context, messageId: String, contentType: String, contentData: ByteArray): Single<File> {
            return Single.fromCallable {
                val outputDirectory = context.cacheDir
                val outputFile: File = File.createTempFile(messageId, "." + getFileExtensionFromMimeType(contentType), outputDirectory)
                val stream = FileOutputStream(outputFile)
                stream.write(contentData)
                stream.close()

                return@fromCallable outputFile
            }
        }
    }
}