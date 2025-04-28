package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.models.NextivaContact

@Entity(tableName = DbConstants.TABLE_NAME_CONTACTS)
data class DbContact(@PrimaryKey(autoGenerate = true) var id: Long?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID) var contactTypeId: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE) var contactType: Int?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_JID) var jid: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_DISPLAY_NAME) var displayName: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME) var firstName: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME) var lastName: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_HIRAGANA_FIRST_NAME) var hiraganaFirstName: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_HIRAGANA_LAST_NAME) var hiraganaLastName: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_TITLE) var title: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_COMPANY) var company: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_IS_FAVORITE) var favorite: Int?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_GROUP_ID) var groupId: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_SHORT_JID) var shortJid: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_SUBSCRIPTION_STATE) var subscriptionState: Int?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME) var sortName: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_SORT_GROUP) var sortGroup: Int?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL) var sortNameFirstInitial: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_UI_NAME) var uiName: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_WEBSITE) var website: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_DEPARTMENT) var department: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_DESCRIPTION) var description: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CREATED_BY) var createdBy: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LAST_MODIFIED_ON) var lastModifiedOn: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LAST_MODIFIED_BY) var lastModifiedBy: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LOOKUP_KEY) var lookupKey: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?,
                     @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_ALIASES) var aliases: String?

) {

    @Ignore
    constructor() : this(null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null)

    @Ignore
    constructor(nextivaContact: NextivaContact, transactionId: String?) : this(
            null,
            nextivaContact.userId,
            nextivaContact.contactType,
            nextivaContact.jid,
            nextivaContact.displayName,
            nextivaContact.firstName,
            nextivaContact.lastName,
            nextivaContact.hiraganaFirstName,
            nextivaContact.hiraganaLastName,
            nextivaContact.title,
            nextivaContact.company,
            if (nextivaContact.isFavorite) 1 else 0,
            nextivaContact.groupId,
            nextivaContact.serverUserId,
            nextivaContact.subscriptionState,
            nextivaContact.uiName,
            nextivaContact.sortGroup,
            if (nextivaContact.uiName?.get(0)?.isLetter() == true) {
                nextivaContact.uiName?.get(0).toString()
            } else {
                null
            },
            nextivaContact.uiName,
            nextivaContact.website,
            nextivaContact.department,
            nextivaContact.description,
            nextivaContact.createdBy,
            nextivaContact.lastModifiedOn,
            nextivaContact.lastModifiedBy,
            nextivaContact.lookupKey,
            transactionId,
            nextivaContact.aliases)
}