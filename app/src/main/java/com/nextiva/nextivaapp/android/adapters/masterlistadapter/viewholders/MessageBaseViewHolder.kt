/*
 * Copyright (c) 2021 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Attachment.ContentMajorType
import com.nextiva.nextivaapp.android.features.rooms.view.BottomSheetMessageMenuDialog
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageBubbleType
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

abstract class MessageBaseViewHolder<T> constructor(itemView: View, val context: Context, masterListListener: MasterListListener?) :
        BaseViewHolder<SmsMessageListItem>(itemView, context, masterListListener) {

    @Inject
    lateinit var mPermissionManager: PermissionManager

    fun convertBubbleType(@Enums.Chats.MessageBubbleTypes.Type bubbleType: Int): MessageBubbleType {
        return when (bubbleType) {
            Enums.Chats.MessageBubbleTypes.SINGLE -> MessageBubbleType.NONE
            Enums.Chats.MessageBubbleTypes.START -> MessageBubbleType.TOP
            Enums.Chats.MessageBubbleTypes.MIDDLE -> MessageBubbleType.MIDDLE
            Enums.Chats.MessageBubbleTypes.END -> MessageBubbleType.BOTTOM
            else -> MessageBubbleType.NONE
        }
    }

    fun convertBubbleType(isTextBubble: Boolean, listItem: SmsMessageListItem): MessageBubbleType {
        var bubbleType = listItem.bubbleType
        if (isTextBubble) {
            if (listItem.data.attachments?.isNotEmpty() == true && bubbleType == Enums.Chats.MessageBubbleTypes.SINGLE) {
                bubbleType = Enums.Chats.MessageBubbleTypes.START
            }
        } else {
            if (listItem.data.attachments?.isNotEmpty() == true && !listItem.data.body.isNullOrEmpty() && bubbleType == Enums.Chats.MessageBubbleTypes.SINGLE) {
                bubbleType = Enums.Chats.MessageBubbleTypes.END
            }
        }

        return when (bubbleType) {
            Enums.Chats.MessageBubbleTypes.SINGLE -> MessageBubbleType.NONE
            Enums.Chats.MessageBubbleTypes.START -> MessageBubbleType.TOP
            Enums.Chats.MessageBubbleTypes.MIDDLE -> MessageBubbleType.MIDDLE
            Enums.Chats.MessageBubbleTypes.END -> MessageBubbleType.BOTTOM
            else -> MessageBubbleType.NONE
        }
    }

    fun showBottomMenuToDownloadFile(
            imageFile: ByteArray?,
            audioFile: File?,
            attachmentLink: String
    ) {
        if (context is FragmentActivity) {
            val stringDownloadToDisplay = when {
                imageFile != null -> context.getString(R.string.chat_details_download_image)
                audioFile != null -> context.getString(R.string.chat_details_download_file)
                else -> context.getString(R.string.room_conversation_menu_download)
            }
            BottomSheetMessageMenuDialog(
                    editAction = null,
                    deleteAction = null,
                    cancelAction = { },
                    downloadAction = {
                        validateStoragePermissions(imageFile, audioFile, attachmentLink)
                    },
                    downloadTextString = stringDownloadToDisplay
            ).show(context.supportFragmentManager, null)
        }
    }

    private fun validateStoragePermissions(imageFile: ByteArray?, audioFile: File?, attachmentLink: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveDataToStorage(imageFile, audioFile, attachmentLink)
        } else {
            mPermissionManager.requestStorageToDownloadPermission(
                    context as Activity,
                    Enums.Analytics.ScreenName.APP_PREFERENCES,
                    { saveDataToStorage(imageFile, audioFile, attachmentLink) },
                    { showSimpleToast(R.string.permission_required_title) }
            )
        }
    }

    private fun saveDataToStorage(imageFile: ByteArray?, audioFile: File?, attachmentLink: String) {
        if (imageFile == null && audioFile == null) {
            showSimpleToast(R.string.chat_details_attachment_invalid)
            return
        }

        val filename = attachmentLink.substringAfterLast("/")
        val fos: OutputStream?

        when {
            imageFile != null -> {
                fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveMediaToStorageQ(filename, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Environment.DIRECTORY_PICTURES)
                } else {
                    saveMediaToStorageLegacy(filename, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
                }

                fos?.use {
                    it.write(imageFile)
                    (context as Activity).runOnUiThread {
                        showCustomToastWhenFinished(ContentMajorType.IMAGE)
                    }
                }
            }
            audioFile != null -> {
                fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveMediaToStorageQ(filename, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Environment.DIRECTORY_MUSIC)
                } else {
                    saveMediaToStorageLegacy(filename, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
                }

                fos?.use {
                    val audioData = audioFile.readBytes()
                    it.write(audioData)
                    (context as Activity).runOnUiThread {
                        showCustomToastWhenFinished(ContentMajorType.AUDIO)
                    }
                }
            }
        }
    }

    private fun saveMediaToStorageQ(filename: String, mediaUri: Uri, directory: String): OutputStream? {
        val resolver = context.contentResolver ?: return null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
        }
        val uri = resolver.insert(mediaUri, contentValues) ?: return null
        return resolver.openOutputStream(uri)
    }

    private fun saveMediaToStorageLegacy(filename: String, directory: File): OutputStream {
        val file = File(directory, filename)
        return FileOutputStream(file)
    }

    private fun showSimpleToast(message: Int) {
        Toast.makeText(context, context.getString(message), Toast.LENGTH_LONG).show()
    }

    @SuppressLint("InflateParams")
    private fun showCustomToastWhenFinished(type: String) {
        val messageSuccess = when (type) {
            ContentMajorType.IMAGE -> R.string.chat_details_image_saved
            else -> R.string.chat_details_file_saved
        }

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_toast, null)
        val textView = view.findViewById<TextView>(R.id.custom_toast_message)
        textView.text = context.getString(messageSuccess)
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }
}
