package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

@Entity(tableName = DbConstants.TABLE_NAME_SOCIAL_MEDIA_ACCOUNTS,
        indices = [Index(DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_CONTACT_ID)],
        foreignKeys = [ForeignKey(entity = DbContact::class,
                parentColumns = [DbConstants.CONTACTS_COLUMN_NAME_ID],
                childColumns = [DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_CONTACT_ID],
                onDelete = ForeignKey.CASCADE)])
data class SocialMediaAccount(@PrimaryKey(autoGenerate = true)
                              @ColumnInfo(name = DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_ID) var id: Long?,
                              @ColumnInfo(name = DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_CONTACT_ID) var contactId: Int?,
                              @ColumnInfo(name = DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_LINK) var link: String?,
                              @ColumnInfo(name = DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_TYPE) @Enums.Contacts.SocialMediaType.Type var type: Int?,
                              @ColumnInfo(name = DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?) : Serializable {

    @Ignore
    constructor() : this(null, null, null, Enums.Contacts.SocialMediaType.OTHER, null)

    @Ignore
    constructor(socialMediaAccount: SocialMediaAccount?) : this(null, socialMediaAccount?.contactId, socialMediaAccount?.link, socialMediaAccount?.type
            ?: Enums.Contacts.SocialMediaType.OTHER, socialMediaAccount?.transactionId)

    @Ignore
    constructor(type: Int, link: String?) : this(null, null, link, type, null)

    fun getIconId(): Int {
        return when (type) {
            Enums.Contacts.SocialMediaType.LINKEDIN -> R.string.fa_linkedin
            Enums.Contacts.SocialMediaType.TWITTER -> R.string.fa_twitter
            Enums.Contacts.SocialMediaType.FACEBOOK -> R.string.fa_facebook
            Enums.Contacts.SocialMediaType.INSTAGRAM -> R.string.fa_instagram
            Enums.Contacts.SocialMediaType.TELEGRAM -> R.string.fa_telegram
            else -> R.string.fa_link
        }
    }

    fun getIconType(): Int {
        return when (type) {
            Enums.Contacts.SocialMediaType.LINKEDIN,
            Enums.Contacts.SocialMediaType.TWITTER,
            Enums.Contacts.SocialMediaType.FACEBOOK,
            Enums.Contacts.SocialMediaType.INSTAGRAM,
            Enums.Contacts.SocialMediaType.TELEGRAM -> Enums.FontAwesomeIconType.BRAND
            else -> Enums.FontAwesomeIconType.REGULAR
        }
    }
}