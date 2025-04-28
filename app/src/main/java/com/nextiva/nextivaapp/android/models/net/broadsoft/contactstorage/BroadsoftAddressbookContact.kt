package com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage

import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.Address
import com.nextiva.nextivaapp.android.db.model.DbGroup
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "contact", strict = false)
data class BroadsoftAddressbookContact(@field:Attribute(name = "id") var id: String? = null,
                                       @field:Attribute(name = "type", required = false) var type: String? = null,
                                       @field:Element(name = "first_name", required = false) var firstName: String? = null,
                                       @field:Element(name = "last_name", required = false) var lastName: String? = null,
                                       @field:Element(name = "display_name", required = false) var displayName: String? = null,
                                       @field:Element(name = "title", required = false) var title: String? = null,
                                       @field:Element(name = "hiragana_first_name", required = false) var hiraganaFirstName: String? = null,
                                       @field:Element(name = "hiragana_last_name", required = false) var hiraganaLastName: String? = null,
                                       @field:Element(name = "group_id", required = false) var groupId: String? = null,
                                       @field:Element(name = "email", required = false) var email: String? = null,
                                       @field:Element(name = "street", required = false) var street: String? = null,
                                       @field:Element(name = "postal_code", required = false) var postalCode: String? = null,
                                       @field:Element(name = "city", required = false) var city: String? = null,
                                       @field:Element(name = "region", required = false) var region: String? = null,
                                       @field:Element(name = "country", required = false) var country: String? = null,
                                       @field:Element(name = "location", required = false) var location: String? = null,
                                       @field:ElementList(name = "communication", inline = true, required = false) var communications: ArrayList<BroadsoftContactCommunication>? = ArrayList()) {

    fun toNextivaContact(favoritesList: ArrayList<BroadsoftAddressbookFavorite>?, groups: ArrayList<BroadsoftAddressbookGroup>?): NextivaContact? {
        id?.let { id ->
            val nextivaContact = NextivaContact(id)

            nextivaContact.contactType = if (type.isNullOrEmpty()) Enums.Contacts.ContactTypes.PERSONAL else Enums.Contacts.ContactTypes.CONFERENCE
            nextivaContact.firstName = firstName
            nextivaContact.lastName = lastName
            nextivaContact.displayName = displayName
            nextivaContact.hiraganaFirstName = hiraganaFirstName
            nextivaContact.hiraganaLastName = hiraganaLastName
            nextivaContact.groupId = groupId
            nextivaContact.emailAddresses = arrayListOf(EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL, email, null))

            val phoneNumbers: ArrayList<PhoneNumber> = ArrayList()

            communications?.let { communications ->
                for (communication in communications) {
                    when (communication.type) {
                        NextivaXMPPConstants.CONTACTS_IQ_JID_TYPE -> nextivaContact.jid = communication.value
                        NextivaXMPPConstants.CONTACTS_IQ_EXTENSION_TYPE -> phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, communication.value))
                        NextivaXMPPConstants.CONTACTS_IQ_BW_USERID_TAGNAME -> nextivaContact.serverUserId = communication.value
                        NextivaXMPPConstants.CONTACTS_IQ_PAGER_TYPE -> phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.PAGER, communication.value))
                        NextivaXMPPConstants.CONTACTS_IQ_CONFERENCE_NUMBER_TYPE -> phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, communication.value, communication.pinOne, communication.pinTwo))
                        NextivaXMPPConstants.CONTACTS_IQ_MOBILE_PHONE_TYPE -> phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.MOBILE_PHONE, communication.value))
                        NextivaXMPPConstants.CONTACTS_IQ_HOME_PHONE_TYPE -> phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, communication.value))
                        NextivaXMPPConstants.CONTACTS_IQ_WORK_PHONE_TYPE -> phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, communication.value))
                        NextivaXMPPConstants.CONTACTS_IQ_PERSONAL_PHONE_TYPE -> phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, communication.value))
                        NextivaXMPPConstants.CONTACTS_IQ_CUSTOM_PHONE_TYPE -> phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.CUSTOM_PHONE, communication.value))
                    }
                }

                nextivaContact.phoneNumbers = phoneNumbers
            }

            val address = Address()
            address.addressLineOne = street
            address.postalCode = postalCode
            address.city = city
            address.region = region
            address.country = country
            address.location = location

            nextivaContact.addresses = arrayListOf(address)

            favoritesList?.let { favoritesList ->
                for (favorite in favoritesList) {
                    if (nextivaContact.userId == favorite.id) {
                        nextivaContact.setIsFavorite(true)
                        break
                    }
                }
            }

            groups?.let { groups ->
                for (group in groups) {
                    group.members?.let { members ->
                        for (member in members) {
                            if (member.id == nextivaContact.userId) {
                                group.position?.let { groupPosition ->
                                    nextivaContact.addGroup(DbGroup(null, group.id, group.displayName, groupPosition, null))
                                }

                                break
                            }
                        }
                    }
                }
            }

            return nextivaContact
        }

        return null
    }

}