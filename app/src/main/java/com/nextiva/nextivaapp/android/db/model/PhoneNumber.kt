package com.nextiva.nextivaapp.android.db.model

import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.util.CallUtil
import java.io.Serializable

@Entity(tableName = DbConstants.TABLE_NAME_PHONES,
        indices = [Index(DbConstants.PHONES_COLUMN_NAME_CONTACT_ID)],
        foreignKeys = [ForeignKey(entity = DbContact::class,
                parentColumns = [DbConstants.CONTACTS_COLUMN_NAME_ID],
                childColumns = [DbConstants.PHONES_COLUMN_NAME_CONTACT_ID],
                onDelete = ForeignKey.CASCADE)])

data class PhoneNumber(@PrimaryKey(autoGenerate = true)
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_ID) var id: Long?,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_CONTACT_ID) var contactId: Int?,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_NUMBER) var number: String?,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_STRIPPED_NUMBER) var strippedNumber: String?,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_TYPE) var type: Int,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_LABEL) var label: String?,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_EXTENSION) var extension: String?,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_PIN_ONE) var pinOne: String?,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_PIN_TWO) var pinTwo: String?,
                       @ColumnInfo(name = DbConstants.PHONES_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?) : Serializable {
    @Ignore
    constructor() :
            this(null, null, null, null, Enums.Contacts.PhoneTypes.PHONE, null, null, null, null, null)

    @Ignore
    constructor(phoneNumber: PhoneNumber) :
            this(null, phoneNumber.contactId, phoneNumber.number, phoneNumber.strippedNumber, phoneNumber.type, phoneNumber.label, phoneNumber.extension, phoneNumber.pinOne, phoneNumber.pinTwo, phoneNumber.transactionId)

    @Ignore
    constructor(type: Int, number: String?, label: String?) :
            this(null, null, number, if (!TextUtils.isEmpty(number)) CallUtil.getStrippedPhoneNumber(number!!) else null, type, label,null,  null, null, null)

    @Ignore
    constructor(type: Int, number: String?) :
            this(null, null, number, if (!TextUtils.isEmpty(number)) CallUtil.getStrippedPhoneNumber(number!!) else null, type, null, null, null, null, null)

    @Ignore
    constructor(type: Int, number: String?, pinOne: String?, pinTwo: String?) :
            this(null, null, number, if (!TextUtils.isEmpty(number)) CallUtil.getStrippedPhoneNumber(number!!) else null, type, null, null, pinOne, pinTwo, null)

    @Ignore
    constructor(type: Int, number: String?, pinOne: String?, pinTwo: String?, label: String?) :
            this(null, null, number, if (!TextUtils.isEmpty(number)) CallUtil.getStrippedPhoneNumber(number!!) else null, type, label, null, pinOne, pinTwo, null)

    @Ignore
    constructor(type: Int, number: String?, strippedNumber: String?, pinOne: String?, pinTwo: String?, label: String?) :
            this(null, null, number, strippedNumber, type, label, null, pinOne, pinTwo, null)

    val assembledPhoneNumber: String?
        get() {
            if (!number.isNullOrEmpty()) {
                number?.let {
                    val assembledPhoneNumber = StringBuilder(it).append(",")

                    if (!pinOne.isNullOrEmpty()) {
                        assembledPhoneNumber.append(pinOne).append("#,")
                    }

                    if (!pinTwo.isNullOrEmpty()) {
                        assembledPhoneNumber.append(pinTwo).append("#,")
                    }

                    if (assembledPhoneNumber.length > 1) {
                        assembledPhoneNumber.setLength(assembledPhoneNumber.length - 1)
                    }

                    return assembledPhoneNumber.toString()
                }
            }

            return null
        }
}