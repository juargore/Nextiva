package com.nextiva.nextivaapp.android.core.common

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.font.FontFamily
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolid
import org.apache.commons.io.FilenameUtils
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Locale

class FileUtil {

    companion object {

        val RESIZEABLE_FILE_TYPES = listOf("jpeg", "jpg", "png", "bmp")
        val THUMBNAIL_FILE_TYPES = listOf("jpeg", "jpg", "png", "bmp", "gif", "mov", "mp4", "3gp")
        val ALLOWED_FILE_IMAGE_TYPES = listOf("jpeg", "jpg", "png", "bmp", "gif")
        val VIDEO_FILE_TYPES = listOf("mov", "mp4", "3gp")
        val AUDIO_FILE_TYPES = listOf("amr", "wav", "mp3", "m4r", "m4b", "m4a")
        val EXCLUDED_TYPES = listOf(
            "7x", "ade", "mde", "adp", "apk", "appx", "appxbundle", "aspx", "bat", "com", "dll", "exe",
            "msi", "cab", "cmd", "cpl", "dmg", "gz", "hta", "ins", "ipa", "iso", "isp", "jar", "js",
            "jse", "jsp", "lib", "lnk", "msc", "msix", "msixbundle", "msp", "mst",  "nsh", "pif",
            "ps1", "scr", "sct", "wsc", "shb", "sys", "vb", "vbe", "vbs", "vxd", "wsf", "wsh", "tar"
        )

        private data class IconMap(val faIconId: Int, val extensions: List<String>)
        private val ICON_MAP = listOf(
            IconMap(R.string.fa_file_pdf, listOf("pdf")),
            IconMap(R.string.fa_file_word, listOf("doc", "docm", "docx")),
            IconMap(R.string.fa_file_spreadsheet, listOf("xls", "xlsb", "xlsm")),
            IconMap(R.string.fa_file_powerpoint, listOf("ppt", "pptm", "pptx")),
            IconMap(R.string.fa_file_music, AUDIO_FILE_TYPES),
        )
        private val DEFAULT_FILE_ICON = R.string.fa_file

        fun faIconId(context: Context, uri: Uri): Int {
            return ICON_MAP.firstOrNull { FileUtil.hasExtension(context, uri, it.extensions) }?.faIconId ?: DEFAULT_FILE_ICON
        }

        fun faIconId(filename: String): Int {
            ICON_MAP.forEach { iconMap ->
                iconMap.extensions.forEach { extension ->
                    if (filename.endsWith(extension)) {
                        return iconMap.faIconId
                    }
                }
            }
            return DEFAULT_FILE_ICON
        }

        fun faIconFontFamily(filename: String): FontFamily {
            return if (isAudioFile(filename)) FontAwesomeSolid else FontAwesome
        }

        fun isAudioFile(filename: String): Boolean {
            return AUDIO_FILE_TYPES.firstOrNull { filename.endsWith(it) } != null
        }

        fun getImageSize(context: Context, uri: Uri): Long {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                val imageSize: Long = cursor.getLong(sizeIndex)
                cursor.close()
                return imageSize // returns size in bytes
            }
            return 0
        }

        fun getImageSizeFromBitmap(bitmap: Bitmap): Int {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return stream.toByteArray().size
        }

        fun getFileNameFromUri(uri: Uri, activity: Context): String? {
            var result: String? = null
            try {
                if (uri.scheme == "content") {
                    val cursor: Cursor? =
                        activity.applicationContext.contentResolver.query(uri, null, null, null, null)
                    cursor.use { cursor ->
                        if (cursor != null && cursor.moveToFirst()) {
                            val column = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (column >= 0) {
                                result = cursor.getString(column)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            return result
        }

        fun getFileExtensionFromUri(uri: Uri, activity: Context): String {
            var extension = ""
            var tempResult = FilenameUtils.getExtension(getFileNameFromUri(uri, activity))
            if (tempResult != null) {
                extension = tempResult
            } else {
                val parts = uri.toString().split(".")
                if (parts.size > 1) {
                    extension = parts.last()
                }
            }
            return extension
        }

        fun getContentTypeFromFileName(fileName: String): String {
            val mimeTypeMap = java.net.URLConnection.getFileNameMap()
            return mimeTypeMap.getContentTypeFor(fileName)
        }

        fun hasExtension(context: Context, uri: Uri, extensionList: List<String>): Boolean {
            val extension = getFileExtensionFromUri(uri, context)
            extensionList.forEach { type ->
                if (extension == type) {
                    return true
                }
            }
            return false
        }

        fun hasExtension(filename: String, extensionList: List<String>): Boolean {
            extensionList.forEach { extension ->
                if (filename.endsWith(extension)) {
                    return true
                }
            }
            return false
        }

        fun compressImage(context: Context, uri: Uri, file: File, maxSize: Int) {
            uri.path?.let { filePath ->
                var compressed = false
                var imageSize = getImageSize(context, uri)
                var firstLoop = true
                val exifOrientation = ExifInterface(file.path).getAttribute(ExifInterface.TAG_ORIENTATION)

                while (imageSize > maxSize) {
                    var bitmap = BitmapFactory.decodeFile(file.path)
                    if (firstLoop) {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        bitmap = BitmapFactory.decodeStream(inputStream)
                        firstLoop = false
                    }
                    val outputStream = FileOutputStream(file)

                    var bitmapCompression = Bitmap.CompressFormat.PNG
                    if (file.absolutePath.lowercase(Locale.ROOT).endsWith(Enums.Attachment.ContentExtensionType.EXT_JPEG) ||
                        file.absolutePath.lowercase(Locale.ROOT).endsWith(Enums.Attachment.ContentExtensionType.EXT_JPG)
                    ) {
                        bitmapCompression = Bitmap.CompressFormat.JPEG
                    }

                    bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width * 0.8).toInt(), (bitmap.height * 0.8).toInt(), false)
                    bitmap.compress(bitmapCompression, 80, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    imageSize = file.length()
                    compressed = true
                }

                if (!compressed) {
                    saveFile(context, uri, file)
                } else if (exifOrientation != null) {
                    val newExif = ExifInterface(file.path)
                    newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
                    newExif.saveAttributes()
                }
            }
        }

        fun saveFile(context: Context, sourceUri: Uri, destinationFile: File) {
            var bis: BufferedInputStream? = null
            var bos: BufferedOutputStream? = null
            try {
                bis = BufferedInputStream(context.contentResolver.openInputStream(sourceUri))
                bos = BufferedOutputStream(FileOutputStream(destinationFile, false))
                val buf = ByteArray(1024)
                bis.read(buf)
                do {
                    bos.write(buf)
                } while (bis.read(buf) !== -1)
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                try {
                    bis?.close()
                    bos?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun saveMediaToStorageQ(context: Context, filename: String, directory: String): OutputStream? {
            val resolver = context.contentResolver ?: return null
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues) ?: return null
            return resolver.openOutputStream(uri)
        }

        fun saveMediaToStorageLegacy(filename: String, directory: File): OutputStream {
            val file = File(directory, filename)
            return FileOutputStream(file)
        }

        fun drawableToByteArray(drawable: Drawable?): ByteArray? {
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                return stream.toByteArray()
            }

            return null
        }

        fun fileToByteArray(file: File): ByteArray? {
            return try {
                val inputStream = FileInputStream(file)
                val byteArray = inputStream.readBytes()
                inputStream.close()
                byteArray
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}
