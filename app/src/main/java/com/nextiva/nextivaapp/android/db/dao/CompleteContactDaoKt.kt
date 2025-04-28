package com.nextiva.nextivaapp.android.db.dao

import android.text.TextUtils
import androidx.room.Dao
import androidx.room.Query
import com.nextiva.nextivaapp.android.constants.Enums.Contacts.ContactTypes
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.CallUtil
import java.util.Arrays
import java.util.stream.Collectors

@Dao
abstract class CompleteContactDaoKt {

    private val connectContactTypePriorityFilter = intArrayOf(
        ContactTypes.CONNECT_USER,
        ContactTypes.CONNECT_PERSONAL,
        ContactTypes.CONNECT_TEAM,
        ContactTypes.CONNECT_SHARED,
        ContactTypes.CONNECT_CALL_CENTERS,
        ContactTypes.CONNECT_CALL_FLOW,
        ContactTypes.CONNECT_UNKNOWN,
        ContactTypes.LOCAL
    )

    fun getConnectContactInThread(phoneNumber: String?): DbResponse<NextivaContact> {
        if (TextUtils.isEmpty(phoneNumber)) {
            return DbResponse(null)
        }
        val cleanedNumber = CallUtil.cleanPhoneNumber(phoneNumber!!)
        val strippedCleanedNumber = if (!TextUtils.isEmpty(cleanedNumber) && cleanedNumber[0] == '1') cleanedNumber.substring(1) else cleanedNumber
        val countryCodedNumbers: Array<String> = getCountryCodedPhoneNumbers(strippedCleanedNumber)

        // Fetch Contacts based on connect ContactType array to keep same behavior across all app, do not
        // modify order of contact type priority here, modify the {connectContactTypes} variable
        val list: List<NextivaContactListFetcher> = if (cleanedNumber.length > 9) {
            Arrays.stream(connectContactTypePriorityFilter)
                .mapToObj { contactType: Int ->
                    object : NextivaContactListFetcher {
                        override val nextivaContacts: List<NextivaContact?>?
                            get() = getNextivaContactsListFromPhoneNumber(contactType, countryCodedNumbers)
                    }
                }
                .collect(Collectors.toList())
        } else {
            Arrays.stream(connectContactTypePriorityFilter)
                .mapToObj { contactType: Int ->
                    object : NextivaContactListFetcher {
                        override val nextivaContacts: List<NextivaContact?>?
                            get() = getNextivaContactsListFromExtension(contactType, "%x$cleanedNumber%", cleanedNumber)
                    }
                }
                .collect(Collectors.toList())
        }

        return getNextivaContactFromListsWithThread(list)
    }

    private fun getNextivaContactFromListsWithThread(nextivaContactListFetchers: List<NextivaContactListFetcher>): DbResponse<NextivaContact> {
        for (nextivaContactListFetcher in nextivaContactListFetchers) {
            nextivaContactListFetcher.nextivaContacts?.firstOrNull()?.let { return DbResponse(it) }
        }

        return DbResponse(null)
    }

    fun getConnectContactFromUuid(userUuid: String): NextivaContact? {
        return getContactFromUuid(userUuid, connectContactTypePriorityFilter)
    }

    @Query("SELECT contacts.* " +
            "FROM contacts " +
            "INNER JOIN phones ON contacts.id = phones.contact_id " +
            "WHERE phones.stripped_number IN (:filterNumbers) " +
            "AND contacts.contact_type = :contactType " +
            "ORDER BY ui_name COLLATE NOCASE ASC, contact_type COLLATE NOCASE ASC")
    abstract fun getNextivaContactsListFromPhoneNumber(@ContactTypes.Type contactType: Int, filterNumbers: Array<String>): List<NextivaContact?>?

    @Query("SELECT contacts.* " +
            "FROM contacts " +
            "INNER JOIN phones ON contacts.id = phones.contact_id " +
            "WHERE (phones.number LIKE :xExtension OR phones.extension = :extension OR phones.stripped_number = :extension) " +
            "AND contacts.contact_type = :contactType " +
            "ORDER BY ui_name COLLATE NOCASE ASC, contact_type COLLATE NOCASE ASC")
    abstract fun getNextivaContactsListFromExtension(@ContactTypes.Type contactType: Int, xExtension: String?, extension: String?): List<NextivaContact?>?

    @Query("SELECT * FROM contacts WHERE " +
            "contact_type_id = :userUuid AND " +
            "contact_type IN (:contactTypes)")
    abstract fun getContactFromUuid(userUuid: String, contactTypes: IntArray): NextivaContact?

    private fun getCountryCodedPhoneNumbers(number: String): Array<String> {
        return if (!TextUtils.isEmpty(number)) {
            arrayOf(number, "1$number", "+1$number")
        } else {
            arrayOf()
        }
    }

    private interface NextivaContactListFetcher {
        val nextivaContacts: List<NextivaContact?>?
    }
}