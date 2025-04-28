package com.nextiva.nextivaapp.android.core.common.ui

import android.net.Uri
import java.io.Serializable

data class PendingMessageData(
    val editMessage: String?,
    val selectedAttachments: List<PendingAttachmentInfo>?
) : Serializable {
    data class PendingAttachmentInfo(
        val uri: String,
        val byteSize: Long,
        val isImageError: Boolean,
        val faIcon: Int,
        val displayAsIcon: Boolean,
        val resizeableType: Boolean,
        val excludedType: Boolean,
        val videoLength: String?
    ) : Serializable {
        constructor(attachmentInfo: AttachmentInfo) : this(
           attachmentInfo.uri.toString(),
           attachmentInfo.byteSize,
           attachmentInfo.isImageError,
           attachmentInfo.faIcon,
           attachmentInfo.displayAsIcon,
           attachmentInfo.resizeableType,
           attachmentInfo.excludedType,
           attachmentInfo.videoLength
       )

        fun toAttachmentInfo() : AttachmentInfo = AttachmentInfo(
            Uri.parse(uri),
            byteSize,
            isImageError,
            faIcon,
            displayAsIcon,
            resizeableType,
            excludedType,
            videoLength
        )
    }
}