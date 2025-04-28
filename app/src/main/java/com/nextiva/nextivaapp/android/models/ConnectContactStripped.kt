package com.nextiva.nextivaapp.android.models

import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Relation
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.DbVCard
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

data class ConnectContactStripped(@ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_ID) private var dbId: Long?,
                                    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID) var contactTypeId: String?,
                                  @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE) var contactType: Int?,
                                  @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_DISPLAY_NAME) var displayName: String?,
                                  @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME) var firstName: String?,
                                  @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME) var lastName: String?,
                                  @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_IS_FAVORITE) var favorite: Boolean?,
                                  @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_UI_NAME) var uiName: String?,
                                  @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID,
                                  entityColumn = DbConstants.PHONES_COLUMN_NAME_CONTACT_ID,
                                  entity = PhoneNumber::class) private var phoneNumbers: List<PhoneNumber>,
                                  @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID,
                                  entityColumn = DbConstants.PRESENCES_COLUMN_NAME_CONTACT_ID,
                                  entity = DbPresence::class) private var presences: List<DbPresence>?,
                                    @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID,
                                    entityColumn = DbConstants.VCARDS_COLUMN_NAME_CONTACT_ID,
                                    entity = DbVCard::class) private var vCards: List<DbVCard>?) : Serializable {

    fun toNextivaContact(): NextivaContact {
        val contact = NextivaContact(contactTypeId ?: "")
        contact.dbId = dbId
        contact.contactType = contactType
        contact.displayName = displayName
        contact.firstName = firstName
        contact.lastName = lastName
        contact.setIsFavorite(favorite ?: false)
        contact.presences = presences
        contact.vCard = vCards?.firstOrNull()
        contact.allPhoneNumbers = phoneNumbers

        return contact
    }

    private fun getAvatarName(): String? {
        return when {
            !TextUtils.isEmpty(displayName) -> displayName
            !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) -> "$firstName $lastName"
            !TextUtils.isEmpty(lastName) -> lastName
            !TextUtils.isEmpty(firstName) -> firstName
            else -> null
        }
    }

    val presence: DbPresence?
    get() {
        return presences?.firstOrNull()
    }

    val photoData: ByteArray?
    get() {
        return vCards?.firstOrNull()?.photoData
    }

    val avatarInfo: AvatarInfo
        get() {
            return if (contactType == Enums.Contacts.ContactTypes.CONFERENCE) {
                AvatarInfo.Builder()
                        .setIconResId(R.drawable.ic_phone)
                        .build()
            } else {
                val builder = AvatarInfo.Builder()
                        .isConnect(true)
                        .setDisplayName(getAvatarName())

                presence?.state?.let { builder.setPresence(DbPresence(it, presence?.status)) }

                if (contactType == Enums.Contacts.ContactTypes.CONNECT_PERSONAL || 
                        contactType == Enums.Contacts.ContactTypes.CONNECT_SHARED ||
                        contactType == Enums.Contacts.ContactTypes.CONNECT_USER ||
                        contactType == Enums.Contacts.ContactTypes.CONNECT_UNKNOWN) {
                    builder.setFontAwesomeIconResId(R.string.fa_user)
                }
                
                builder.build()
            }
        }
}
