package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

@Entity(tableName = DbConstants.TABLE_NAME_POSTAL_ADDRESSES,
        indices = [Index(DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CONTACT_ID)],
        foreignKeys = [ForeignKey(entity = DbContact::class,
                parentColumns = [DbConstants.CONTACTS_COLUMN_NAME_ID],
                childColumns = [DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CONTACT_ID],
                onDelete = ForeignKey.CASCADE)])

data class Address(@PrimaryKey(autoGenerate = true)
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ID) var id: Long?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CONTACT_ID) var contactId: Int?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ADDRESS_LINE_ONE) var addressLineOne: String?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ADDRESS_LINE_TWO) var addressLineTwo: String?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_POSTAL_CODE) var postalCode: String?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CITY) var city: String?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_REGION) var region: String?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_COUNTRY) var country: String?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_LOCATION) var location: String?,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_TYPE) var type: Int? = Enums.Contacts.AddressType.OTHER,
                   @ColumnInfo(name = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?) : Serializable {

    @Ignore
    constructor() : this(null, null, null, null, null, null, null, null, null, Enums.Contacts.AddressType.OTHER, null)

    @Ignore
    constructor(address: Address?) : this(
            null,
            address?.contactId,
            address?.addressLineOne,
            address?.addressLineTwo,
            address?.postalCode,
            address?.city,
            address?.region,
            address?.country,
            address?.location,
            address?.type ?: Enums.Contacts.AddressType.OTHER,
            address?.transactionId)

    @Ignore
    constructor(contactId: Int?, address: Address?) : this(
            null,
            contactId,
            address?.addressLineOne,
            address?.addressLineTwo,
            address?.postalCode,
            address?.city,
            address?.region,
            address?.country,
            address?.location,
            address?.type ?: Enums.Contacts.AddressType.OTHER,
            address?.transactionId)

    @Ignore
    constructor(addressLineOne: String?, addressLineTwo: String?, postalCode: String?, city: String?, region: String?, country: String?, location: String?, type: Int?, transactionId: String?) : this(
            null,
            null,
            addressLineOne,
            addressLineTwo,
            postalCode,
            city,
            region,
            country,
            location,
            type ?: Enums.Contacts.AddressType.OTHER,
            transactionId)

    @Ignore
    constructor(type: Int) : this(null, null, null, null, null, null, null, null, null, type, null)

    fun getConnectDetailSubtitle(): String {
        var addressString = ""
        var shouldAddNewLine = false

        if (!addressLineOne.isNullOrEmpty()) {
            addressString += "${addressLineOne}"
            shouldAddNewLine = true
        }

        if (!addressLineTwo.isNullOrEmpty()) {
            if (shouldAddNewLine) {
                addressString += "\n"

            } else {
                shouldAddNewLine = true
            }

            addressString += "${addressLineTwo}"
        }

        if (!city.isNullOrEmpty() && !region.isNullOrEmpty() && !postalCode.isNullOrEmpty()) {
            if (shouldAddNewLine) {
                addressString += "\n"

            } else {
                shouldAddNewLine = true
            }

            addressString += "${city}, ${region} ${postalCode}"

        } else if (!city.isNullOrEmpty()) {
            if (shouldAddNewLine) {
                addressString += "\n"

            } else {
                shouldAddNewLine = true
            }

            addressString += "${city}"

            if (!region.isNullOrEmpty() || !postalCode.isNullOrEmpty()) {
                addressString += ", "
            }

            if (!region.isNullOrEmpty()) {
                addressString += "${region} "
            }

            if (!postalCode.isNullOrEmpty()) {
                addressString += "$postalCode"
            }

        } else if (!region.isNullOrEmpty()) {
            if (shouldAddNewLine) {
                addressString += "\n"

            } else {
                shouldAddNewLine = true
            }

            addressString += "${region} "

            if (!postalCode.isNullOrEmpty()) {
                addressString += "$postalCode"
            }

        } else if (!postalCode.isNullOrEmpty()) {
            if (shouldAddNewLine) {
                addressString += "\n"

            } else {
                shouldAddNewLine = true
            }

            addressString += "${postalCode} "
        }

        if (!country.isNullOrEmpty()) {
            if (shouldAddNewLine) {
                addressString += "\n"
            }

            addressString += "$country"
        }

        return addressString
    }

    fun hasNonNullValue(): Boolean {
        return !addressLineOne.isNullOrEmpty() ||
                !addressLineTwo.isNullOrEmpty() ||
                !postalCode.isNullOrEmpty() ||
                !city.isNullOrEmpty() ||
                !region.isNullOrEmpty() ||
                !country.isNullOrEmpty() ||
                !location.isNullOrEmpty()
    }
}