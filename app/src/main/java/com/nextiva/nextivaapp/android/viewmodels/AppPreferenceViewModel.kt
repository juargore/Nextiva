package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.interfaces.*
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

@HiltViewModel
class AppPreferenceViewModel @Inject constructor(
    val nextivaApplication: Application,
    val settingsManager: SettingsManager,
    val logManager: LogManager,
    val dbManager: DbManager,
    val calendarManager: CalendarManager,
    val sipManager: PJSipManager,
    val sessionManager: SessionManager,
    val conversationRepository: ConversationRepository
) : BaseViewModel(nextivaApplication) {

    fun deleteDirectory(directory: File): Boolean {
        if (directory.isDirectory) {
            val children = directory.list()

            children?.let {
                for (child in children) {
                    val success = deleteDirectory(File(directory, child))
                    if (!success) {
                        return false
                    }
                }
            }
        }

        // The directory is now empty so delete it
        return directory.delete()
    }

    fun deleteCurrentZips() {
        try {
            val file = File("${Environment.getExternalStorageDirectory().absolutePath}/Documents/Nextiva/${BuildConfig.FLAVOR}/")

            if (file.isDirectory) {
                file.listFiles()?.let { listFiles ->
                    for (listFile in listFiles) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val contentUri = FileProvider.getUriForFile(nextivaApplication,
                                    "${BuildConfig.APPLICATION_ID}.provider",
                                    listFile)
                            nextivaApplication.contentResolver.delete(contentUri, null)

                        } else {
                            listFile.delete()
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun zipLogs(): String {
        val formatterManager = FormatterManager.getInstance()
        val filename = String.format(formatterManager.getDateFormatter_logZipFilename(application)
                .format(calendarManager.nowInstant).replace(":", "", true) + ".zip",
                (application as Application).getString(R.string.app_name)).replace(" ", "")
        var absoluteFilePath = nextivaApplication.filesDir.path + LOGS_DIR + filename

        val fileOutputStream = FileOutputStream(absoluteFilePath)
        val zipOutputStream = ZipOutputStream(fileOutputStream)
        addDirectoryToZipArchive(zipOutputStream, absoluteFilePath, File(nextivaApplication.filesDir.path + LOGS_DIR), null)

        zipOutputStream.flush()
        fileOutputStream.flush()
        zipOutputStream.close()
        fileOutputStream.close()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            absoluteFilePath = moveFile(absoluteFilePath, filename) ?: absoluteFilePath
        }

        return absoluteFilePath
    }

    @Throws(Exception::class)
    private fun addDirectoryToZipArchive(zipOutputStream: ZipOutputStream, filename: String, fileToZip: File?, parentDirectoryName: String?) {
        fileToZip?.let { file ->
            if (file.exists()) {
                var zipEntryName = file.name

                if (!parentDirectoryName.isNullOrEmpty()) {
                    zipEntryName = parentDirectoryName + "/" + file.name
                }

                if (file.isDirectory) {
                    val zipsList: ArrayList<File> = ArrayList()

                    for (listFile in file.listFiles()) {
                        if (listFile.name.contains(".log")) {
                            addDirectoryToZipArchive(zipOutputStream, filename, listFile, zipEntryName)

                        } else if (listFile.name.contains(".zip") && !TextUtils.equals(listFile.path, filename)) {
                            zipsList.add(listFile)

                        }
                    }

                } else {
                    val buffer = ByteArray(1024)
                    val fileInputStream = FileInputStream(file)
                    zipOutputStream.putNextEntry(ZipEntry(zipEntryName))
                    var length = fileInputStream.read(buffer)

                    while (length > 0) {
                        zipOutputStream.write(buffer, 0, length)
                        length = fileInputStream.read(buffer)
                    }

                    zipOutputStream.closeEntry()
                    fileInputStream.close()
                }
            }
        }
    }

    private fun moveFile(currentPath: String, filename: String): String? {
        val source = File(currentPath)

        val destinationPath = "${Environment.getExternalStorageDirectory().absolutePath}/Documents/Nextiva/${BuildConfig.FLAVOR}/"
        val destination = File(destinationPath)
        destination.mkdirs()
        val file = File(destinationPath, filename)

        try {
            copyFile(source, file)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return file.absolutePath
    }

    fun isLoggedIn(): Boolean {
        return sessionManager.userDetails != null
    }

    @Throws(IOException::class)
    fun copyFile(source: File?, destination: File?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(FileInputStream(source), FileOutputStream(destination))
        }
    }

    fun setupLogger() {
        logManager.setupLogger()
    }

    fun expireContactsCache() {
        dbManager.expireContactCache()
    }

    fun enableLogging(enabled: Boolean) {
        settingsManager.enableLogging = enabled
    }

    fun enableFileLogging(enabled: Boolean) {
        settingsManager.fileLogging = enabled
    }

    fun enableXmppLogging(enabled: Boolean) {
        settingsManager.xmppLogging = enabled
    }

    fun enableSipLogging(enabled: Boolean) {
        settingsManager.sipLogging = enabled
    }

    fun enableEchoCancellation(enabled: Boolean) {
        settingsManager.isSipEchoCancellationEnabled = enabled
       // sipManager.setEchoCancellation(enabled)
    }

    fun enableShowDialogToDelete(enabled: Boolean) {
        settingsManager.isShowDialogToDeleteSmsEnabled = enabled
    }

    fun enableSwipeActions(enabled: Boolean) {
        settingsManager.isSwipeActionsEnabled = enabled
    }

    fun enableBlockNumberForCalling(enabled: Boolean) {
        settingsManager.isBlockNumberForCallingEnabled = enabled
    }

    fun enableDisplayAudioVideoStats(enabled: Boolean) {
        settingsManager.displayAudioVideoStats = enabled
    }

    fun enableOldActiveCallLayout(enabled: Boolean){
        settingsManager.setOldActiveCallLayoutEnabled(enabled)
    }

    fun enableDisplaySIPState(enabled: Boolean) {
        settingsManager.displaySIPState = enabled
    }

    fun enableDisplaySIPError(enabled: Boolean) {
        settingsManager.displaySIPError = enabled
    }

    fun isSipEchoCancellationEnabled(): Boolean {
        return settingsManager.isSipEchoCancellationEnabled
    }

    fun isShowDialogToDeleteSmsEnabled(): Boolean {
        return settingsManager.isShowDialogToDeleteSmsEnabled
    }

    fun isSwipeActionsEnabled(): Boolean {
        return settingsManager.isSwipeActionsEnabled
    }

    fun isLoggingEnabled(): Boolean {
        return settingsManager.enableLogging
    }

    fun isFileLoggingEnabled(): Boolean {
        return settingsManager.fileLogging
    }

    fun isXmppLoggingEnabled(): Boolean {
        return settingsManager.xmppLogging
    }

    fun isSipLoggingEnabled(): Boolean {
        return settingsManager.sipLogging
    }

    fun isDisplayAudioVideoStatsEnabled(): Boolean {

        return settingsManager.displayAudioVideoStats
    }

    fun isOldActiveCallLayoutEnabled(): Boolean{
        return settingsManager.isOldActiveCallLayoutEnabled
    }

    fun isDisplaySIPStateEnabled(): Boolean {
        return settingsManager.displaySIPState
    }
    fun isDisplaySIPErrorEnabled(): Boolean {
        return settingsManager.displaySIPError
    }

    fun deleteSms() {
        conversationRepository.deleteSmsMessages()
    }

    fun isCustomToneEnabled(): Boolean {
        return sessionManager.isCustomToneEnabled
    }

    companion object {
        const val LOGS_DIR = "/Logs/"
    }
}