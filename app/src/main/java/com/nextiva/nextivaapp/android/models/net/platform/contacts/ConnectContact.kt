package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbDate
import com.nextiva.nextivaapp.android.models.NextivaContact
import java.io.Serializable

data class ConnectContact(@SerializedName("id") var id: String?,
                          @SerializedName("createdById") var createdById: String?,
                          @SerializedName("createdAt") var createdAt: String?,
                          @SerializedName("lastModifiedById") var lastModifiedById: String?,
                          @SerializedName("lastModifiedAt") var lastModifiedAt: String?,
                          @SerializedName("externalId") var externalId: String?,
                          @SerializedName("externalSource") var externalSource: ConnectContactExternalSource?,
                          @SerializedName("legacyId") var legacyId: String?,
                          @SerializedName("type") var type: String?,
                          @SerializedName("firstName") var firstName: String?,
                          @SerializedName("middleName") var middleName: String?,
                          @SerializedName("lastName") var lastName: String?,
                          @SerializedName("prefix") var prefix: String?,
                          @SerializedName("suffix") var suffix: String?,
                          @SerializedName("nickname") var nickname: String?,
                          @SerializedName("email") var email: String?,
                          @SerializedName("secondaryEmail") var secondaryEmail: String?,
                          @SerializedName("primaryAccountId") var primaryAccountId: String?,
                          @SerializedName("birthdate") var birthDate: String?, // yyyy-mm-dd
                          @SerializedName("signUpDate") var signUpDate: String?, // yyyy-mm-dd
                          @SerializedName("nextContactDate") var nextContactDate: String?, // yyyy-mm-dd
                          @SerializedName("cancelDate") var cancelDate: String?, // yyyy-mm-dd
                          @SerializedName("homePhone") var homePhone: String?,
                          @SerializedName("mobilePhone") var mobilePhone: String?,
                          @SerializedName("workPhone") var workPhone: String?,
                          @SerializedName("workPhoneExt") var workPhoneExt: String?,
                          @SerializedName("telephoneNumber") var telephoneNumber: String?,
                          @SerializedName("otherPhone") var otherPhone: String?,
                          @SerializedName("assistant") var assistant: String?,
                          @SerializedName("assistantPhone") var assistantPhone: String?,
                          @SerializedName("fax") var fax: String?,
                          @SerializedName("doNotCall") var doNotCall: Boolean?,
                          @SerializedName("communicationPreference") var communicationPreference: String?,
                          @SerializedName("shippingAddress") var shippingAddress: ConnectAddressData?,
                          @SerializedName("billingAddress") var billingAddress: ConnectAddressData?,
                          @SerializedName("personalAddress") var personalAddress: ConnectAddressData?,
                          @SerializedName("workAddress") var workAddress: ConnectAddressData?,
                          @SerializedName("otherAddress") var otherAddress: ConnectAddressData?,
                          @SerializedName("tags") var tags: ArrayList<String>?,
                          @SerializedName("company") var company: String?,
                          @SerializedName("jobTitle") var jobTitle: String?,
                          @SerializedName("department") var department: String?,
                          @SerializedName("description") var description: String?,
                          @SerializedName("website") var website: String?,
                          @SerializedName("photoUrl") var photoUrl: String?,
                          @SerializedName("facebook") var facebook: String?,
                          @SerializedName("twitter") var twitter: String?,
                          @SerializedName("linkedIn") var linkedIn: String?,
                          @SerializedName("instagram") var instagram: String?,
                          @SerializedName("telegram") var telegram: String?,
                          @SerializedName("otherSocialMedia") var otherSocialMedia: String?,
                          @SerializedName("displayName") var displayName: String?,
                          @SerializedName("primaryAccount") var primaryAccount: ConnectContactPrimaryAccount?,
                          @SerializedName("owner") var owner: ConnectCreatedBy?,
                          @SerializedName("createdBy") var createdBy: ConnectCreatedBy?,
                          @SerializedName("lastModifiedBy") var lastModifiedBy: ConnectLastModifiedBy?,
                          @SerializedName("favorite") var favorite: Boolean?,
                          @SerializedName("aliases") var aliases: ArrayList<String>?,
                          @SerializedName("phoneNumbers") var phoneNumbers: ArrayList<ConnectPhoneNumber>?,
                          @SerializedName("emails") var emails: ArrayList<ConnectEmailAddress>?,
                          @SerializedName("socialMedia") var socialMedia: ArrayList<ConnectSocialMedia>?,
                          @SerializedName("physicalAddresses") var physicalAddresses: ArrayList<ConnectAddress>?
    ) : Serializable {

    constructor(nextivaContact: NextivaContact) : this(nextivaContact, false)

    constructor(nextivaContact: NextivaContact, isEdit: Boolean) : this(
        null,
        null,
        null,
        null,
        null,
        nextivaContact.lookupKey,
        null,
        null,
        if (isEdit) null else when (nextivaContact.contactType) {
            Enums.Contacts.ContactTypes.CONNECT_USER -> Enums.Contacts.ContactTypesValue.CONNECT_USER
            Enums.Contacts.ContactTypes.CONNECT_SHARED -> Enums.Contacts.ContactTypesValue.CONNECT_SHARED
            Enums.Contacts.ContactTypes.CONNECT_PERSONAL -> Enums.Contacts.ContactTypesValue.CONNECT_PERSONAL
            else -> Enums.Contacts.ContactTypesValue.CONNECT_SHARED
        },
        nextivaContact.firstName,
        null,
        nextivaContact.lastName,
        null,
        null,
        null,
        null,
        null,
        null,
        nextivaContact.dates?.firstOrNull { it.type == Enums.Contacts.DateType.BIRTH }?.date,
        nextivaContact.dates?.firstOrNull { it.type == Enums.Contacts.DateType.SIGN_UP }?.date,
        nextivaContact.dates?.firstOrNull { it.type == Enums.Contacts.DateType.NEXT_CONTACT }?.date,
        nextivaContact.dates?.firstOrNull { it.type == Enums.Contacts.DateType.CANCEL }?.date,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        nextivaContact.company,
        nextivaContact.title,
        nextivaContact.department,
        nextivaContact.description,
        nextivaContact.website,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        phoneNumbers = nextivaContact.allPhoneNumbers?.mapNotNull { ConnectPhoneNumber.fromPhoneNumber(it) }?.let { ArrayList(it) },
        emails = nextivaContact.emailAddresses?.mapNotNull { ConnectEmailAddress.fromEmailAddress(it) }?.let { ArrayList(it) },
        socialMedia = nextivaContact.socialMediaAccounts?.mapNotNull { ConnectSocialMedia.fromSocialMediaAccount(it) }?.let { ArrayList(it) },
        physicalAddresses = nextivaContact.addresses?.mapNotNull { ConnectAddress.fromAddress(it) }?.let { ArrayList(it) }
    )

    fun toNextivaContact(): NextivaContact {
        val nextivaContact = NextivaContact(id ?: "")
        nextivaContact.setIsFavorite(favorite ?: false)

        if (!displayName.isNullOrBlank()) {
            nextivaContact.displayName = displayName
        }

        if (!firstName.isNullOrBlank()) {
            nextivaContact.firstName = firstName
        }

        if (!lastName.isNullOrBlank()) {
            nextivaContact.lastName = lastName
        }

        if (!externalId.isNullOrBlank()) {
            nextivaContact.lookupKey = externalId
        }

        // Email Addresses

        emails?.mapNotNull { it.toEmailAddress() }?.let {
            nextivaContact.emailAddresses = ArrayList(it)
        }

        // Phone Numbers
        phoneNumbers?.mapNotNull { it.toPhoneNumber() }?.let {
            nextivaContact.phoneNumbers = ArrayList(it)
        }

        // Social Media
        socialMedia?.mapNotNull { it.toSocialMediaAccount() }?.let {
            nextivaContact.socialMediaAccounts = ArrayList(it)
        }

        // Address

        physicalAddresses?.mapNotNull { it.toAddress() }?.let {
            nextivaContact.addresses = ArrayList(it)
        }

        val dates: ArrayList<DbDate> = ArrayList()
        if (!birthDate.isNullOrBlank()) {
            dates.add(DbDate(Enums.Contacts.DateType.BIRTH, birthDate))
        }
        if (!signUpDate.isNullOrBlank()) {
            dates.add(DbDate(Enums.Contacts.DateType.SIGN_UP, signUpDate))
        }
        if (!nextContactDate.isNullOrBlank()) {
            dates.add(DbDate(Enums.Contacts.DateType.NEXT_CONTACT, nextContactDate))
        }
        if (!cancelDate.isNullOrBlank()) {
            dates.add(DbDate(Enums.Contacts.DateType.CANCEL, cancelDate))
        }
        nextivaContact.dates = dates

        if (!jobTitle.isNullOrBlank()) {
            nextivaContact.title = jobTitle
        }
        if (!description.isNullOrBlank()) {
            nextivaContact.description = description
        }
        if (!company.isNullOrBlank()) {
            nextivaContact.company = company
        }
        if (!website.isNullOrBlank()) {
            nextivaContact.website = website
        }
        if (!department.isNullOrBlank()) {
            nextivaContact.department = department
        }
        if (!createdBy?.id.isNullOrBlank()) {
            nextivaContact.createdBy = "${createdBy?.firstName ?: ""} ${createdBy?.lastName ?: ""}"
        }
        if (!lastModifiedBy?.id.isNullOrBlank()) {
            nextivaContact.lastModifiedBy = "${lastModifiedBy?.firstName ?: ""} ${lastModifiedBy?.lastName ?: ""}"
        }
        if (!lastModifiedAt.isNullOrBlank()) {
            nextivaContact.lastModifiedOn = lastModifiedAt
        }

        nextivaContact.contactType = when (type) {
            Enums.Contacts.ContactTypesValue.CONNECT_PERSONAL -> Enums.Contacts.ContactTypes.CONNECT_PERSONAL
            Enums.Contacts.ContactTypesValue.CONNECT_SHARED -> Enums.Contacts.ContactTypes.CONNECT_SHARED
            Enums.Contacts.ContactTypesValue.CONNECT_USER -> Enums.Contacts.ContactTypes.CONNECT_USER
            Enums.Contacts.ContactTypesValue.CONNECT_CALL_FLOW -> Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW
            Enums.Contacts.ContactTypesValue.CONNECT_CALL_CENTER -> Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS
            Enums.Contacts.ContactTypesValue.CONNECT_TEAM -> Enums.Contacts.ContactTypes.CONNECT_TEAM
            else -> Enums.Contacts.ContactTypes.CONNECT_UNKNOWN
        }

        if(aliases != null) {
            var aliasString = ""
            for (alias in aliases!!) {
                aliasString += "$alias,"
            }

            if(aliasString.last() == ',')
                aliasString = aliasString.dropLast(1)

            nextivaContact.aliases = aliasString
        }

        return nextivaContact
    }
}