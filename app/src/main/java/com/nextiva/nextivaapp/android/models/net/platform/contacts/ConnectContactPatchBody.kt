package com.nextiva.nextivaapp.android.models.net.platform.contacts

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.models.NextivaContact
import java.io.Serializable

data class ConnectContactPatchBody(
    @SerializedName("type") var type: String?,
    @SerializedName("firstName") var firstName: String?,
    @SerializedName("lastName") var lastName: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("secondaryEmail") var secondaryEmail: String?,
    @SerializedName("birthdate") var birthDate: String?, // yyyy-mm-dd
    @SerializedName("signUpDate") var signUpDate: String?, // yyyy-mm-dd
    @SerializedName("nextContactDate") var nextContactDate: String?, // yyyy-mm-dd
    @SerializedName("cancelDate") var cancelDate: String?, // yyyy-mm-dd
    @SerializedName("homePhone") var homePhone: String?,
    @SerializedName("mobilePhone") var mobilePhone: String?,
    @SerializedName("workPhone") var workPhone: String?,
    @SerializedName("workPhoneExt") var workPhoneExt: String?,
    @SerializedName("otherPhone") var otherPhone: String?,
    @SerializedName("assistantPhone") var assistantPhone: String?,
    @SerializedName("fax") var fax: String?,
    @SerializedName("shippingAddress") var shippingAddress: ConnectAddressData?,
    @SerializedName("billingAddress") var billingAddress: ConnectAddressData?,
    @SerializedName("personalAddress") var personalAddress: ConnectAddressData?,
    @SerializedName("workAddress") var workAddress: ConnectAddressData?,
    @SerializedName("otherAddress") var otherAddress: ConnectAddressData?,
    @SerializedName("company") var company: String?,
    @SerializedName("jobTitle") var jobTitle: String?,
    @SerializedName("department") var department: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("website") var website: String?,
    @SerializedName("facebook") var facebook: String?,
    @SerializedName("twitter") var twitter: String?,
    @SerializedName("linkedIn") var linkedIn: String?,
    @SerializedName("instagram") var instagram: String?,
    @SerializedName("telegram") var telegram: String?,
    @SerializedName("otherSocialMedia") var otherSocialMedia: String?,
    @SerializedName("phoneNumbers") var phoneNumbers: ArrayList<ConnectPhoneNumber>?,
    @SerializedName("emails") var emails: ArrayList<ConnectEmailAddress>?,
    @SerializedName("socialMedia") var socialMedia: ArrayList<ConnectSocialMedia>?,
    @SerializedName("physicalAddresses") var physicalAddresses: ArrayList<ConnectAddress>?
) : Serializable {

    constructor(nextivaContact: NextivaContact) : this(
        when (nextivaContact.contactType) {
            Enums.Contacts.ContactTypes.CONNECT_USER -> Enums.Contacts.ContactTypesValue.CONNECT_USER
            Enums.Contacts.ContactTypes.CONNECT_SHARED -> Enums.Contacts.ContactTypesValue.CONNECT_SHARED
            Enums.Contacts.ContactTypes.CONNECT_PERSONAL -> Enums.Contacts.ContactTypesValue.CONNECT_PERSONAL
            else -> Enums.Contacts.ContactTypesValue.CONNECT_SHARED
        },
        nextivaContact.firstName,
        nextivaContact.lastName,
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
        nextivaContact.phoneNumbers?.firstOrNull { it.type == Enums.Contacts.PhoneTypes.FAX }?.number,
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
        phoneNumbers = nextivaContact.allPhoneNumbers?.mapNotNull { ConnectPhoneNumber.fromPhoneNumber(it) }?.let { ArrayList(it) },
        emails = nextivaContact.emailAddresses?.mapNotNull { ConnectEmailAddress.fromEmailAddress(it) }?.let { ArrayList(it) },
        socialMedia = nextivaContact.socialMediaAccounts?.mapNotNull { ConnectSocialMedia.fromSocialMediaAccount(it) }?.let { ArrayList(it) },
        physicalAddresses = nextivaContact.addresses?.mapNotNull { ConnectAddress.fromAddress(it) }?.let { ArrayList(it) }
    )
}