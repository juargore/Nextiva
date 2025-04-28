package com.nextiva.nextivaapp.android.core.common.ui

import android.app.Activity
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.LiveData
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.common.FileUtil
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.ConversationViewModel
import com.nextiva.nextivaapp.android.models.SmsConversationDetails

data class AttachmentInfo(
    val uri: Uri,
    val byteSize: Long,
    val isImageError: Boolean,
    val faIcon: Int,
    val displayAsIcon: Boolean,
    val resizeableType: Boolean,
    val excludedType: Boolean,
    val videoLength: String?
)

data class SendingViaBanner(val text: String?,
    val showClose: Boolean?)

enum class AttachmentMenuItems { TAKE_PICTURE, CHOOSE_PHOTO_OR_VIDEO, CHOOSE_PHOTO, ATTACH_FILE }

interface MessageTextFieldInterface {
    val isSending: LiveData<Boolean>
    val sendingPhoneNumbers: LiveData<ArrayList<ConversationViewModel.SendingPhoneNumber>?>
    val conversationDetails: LiveData<SmsConversationDetails?>
    val editMessage: LiveData<String?>
    val setDraftMessage: LiveData<String?>
    val errorMessages: LiveData<List<String>?>
    val selectedAttachments: LiveData<List<AttachmentInfo>?>
    val sendingViaBanner: LiveData<SendingViaBanner?>

    fun onEditMessageCancel()
    fun onSendingViaBannerClosed()
    fun onSend(text: String?)
    fun onValueChanged(text: String)
    fun addAttachments(attachments: List<Uri>)
    fun removeAttachment(info: AttachmentInfo)
    fun isImageError(uri: Uri, byteSize: Long): Boolean
    fun hasThumbnail(uri: Uri): Boolean
    fun menuItems() : List<AttachmentMenuItems>
    fun isExcludedType(uri: Uri) : Boolean
    fun onSelectPhoneNumberClicked() { }
    fun onAlertError() { }

    fun editMessageFormatted(title: String, text: String): AnnotatedString {
        val builder = AnnotatedString.Builder()
        builder.append("$title $text")
        builder.addStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold),
            start = 0,
            end = title.length)
        return builder.toAnnotatedString()
    }

    fun attachmentInfo(context: Context, uri: Uri): AttachmentInfo {
        val byteSize = FileUtil.getImageSize(context, uri)

        var videoLength: String? = null
        if (FileUtil.hasExtension(context, uri, FileUtil.VIDEO_FILE_TYPES)) {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLongOrNull()?.let { timeInMillisec ->
                timeInMillisec
                val minutes = timeInMillisec / 1000 / 60
                val seconds = timeInMillisec / 1000 % 60
                videoLength = if (seconds < 10) "$minutes:0$seconds" else "$minutes:$seconds"
            }
            retriever.release()
        }

        return AttachmentInfo(
            uri,
            byteSize,
            isImageError(uri, byteSize),
            FileUtil.faIconId(context, uri),
            !hasThumbnail(uri),
            FileUtil.hasExtension(context, uri, FileUtil.RESIZEABLE_FILE_TYPES),
            isExcludedType(uri),
            videoLength
        )
    }

    fun getPermission(activity: Activity, permissions: List<String>, granted: () -> Unit, denied: () -> Unit) {
        val dexter = Dexter.withActivity(activity)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        granted()
                    } else {
                        denied()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(request: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            })
        dexter.check()
    }

    fun choosePhotoMenuItem(context: Context): String {
        if (menuItems().contains(AttachmentMenuItems.CHOOSE_PHOTO))
            return context.resources.getString(R.string.connect_sms_choose_photo)
        else
            return context.resources.getString(R.string.room_conversation_choose_photo)
    }
}
