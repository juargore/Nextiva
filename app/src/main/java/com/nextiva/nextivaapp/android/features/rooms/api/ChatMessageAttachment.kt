package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChatMessageAttachment(
    @SerializedName("id") var id: String,
    @SerializedName("filename") var filename: String,
    @SerializedName("encodedFilename") var encodedFilename: String,
    @SerializedName("contentType") var contentType: String,
    @SerializedName("fileSize") var fileSize: Int,
    @SerializedName("compressed") var compressed: Boolean,
    @SerializedName("uploadedDate") var uploadedDate: String,
    @SerializedName("parentEntityId") var parentEntityId: String,
    @SerializedName("corpAcctNumber") var corpAcctNumber: String,
    @SerializedName("entityType") var entityType: String,
    @SerializedName("reference") var reference: AttachmentReference,
    @SerializedName("thumbnailReference") var thumbnailReference: AttachmentReference,
    @SerializedName("duration") var duration: Long,
    @SerializedName("ownershipMetadata") var ownershipMetadata: AttachmentOwnership,
    @SerializedName("url") var url: String,
    @SerializedName("text") var text: String,
    @SerializedName("title") var title: String,
    @SerializedName("contentData") var contentData: ByteArray?
) : Serializable {

    constructor(filename: String?, link: String, contentType: String?) : this(
        id = "",
        filename = filename ?: "",
        encodedFilename = "",
        contentType = contentType ?: "",
        fileSize = 0,
        compressed = false,
        uploadedDate = "",
        parentEntityId = "",
        corpAcctNumber = "",
        entityType = "",
        reference = AttachmentReference("", link, ""),
        thumbnailReference = AttachmentReference("", link, ""),
        duration = 0L,
        ownershipMetadata = AttachmentOwnership(null, null, null, null),
        url = "",
        text = "",
        title = "",
        contentData = byteArrayOf()
    )
}

data class AttachmentReference(
    @SerializedName("id") var id: String,
    @SerializedName("link") var link: String,
    @SerializedName("storageType") var storageType: String
) : Serializable

data class AttachmentOwnership(
    @SerializedName("owners") var owners: ArrayList<String>?,
    @SerializedName("owner") var owner: String?,
    @SerializedName("receivers") var receivers: ArrayList<String>?,
    @SerializedName("sharedGroups") var sharedGroups: ArrayList<String>?
) : Serializable
