package com.nextiva.nextivaapp.android.mocks.values

import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.Address
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.DbVCard
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.models.NextivaContact

object ContactLists {
    fun getNextivaContactTestList(): ArrayList<NextivaContact> {
        val nextivaContacts = ArrayList<NextivaContact>()

        // Contact 1
        val nextivaContact = NextivaContact("123456")
        nextivaContact.contactType = Enums.Contacts.ContactTypes.PERSONAL
        nextivaContact.jid = "jid@jid.im"
        nextivaContact.displayName = "Displayed Name"
        nextivaContact.firstName = "First"
        nextivaContact.lastName = "Last"
        nextivaContact.hiraganaFirstName = "HFirst"
        nextivaContact.hiraganaLastName = "HLast"
        nextivaContact.title = "His/Her Excellence"
        nextivaContact.company = "Fake Co"
        nextivaContact.setIsFavorite(true)
        nextivaContact.groupId = "Group Id"
        nextivaContact.serverUserId = "display.name"
        nextivaContact.addresses = arrayListOf(Address("Street", "Street Two", "12345", "City", "Region", "Country", "Location", Enums.Contacts.AddressType.OTHER, null))

        val emailList: ArrayList<EmailAddress> = ArrayList()
        emailList.add(EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL, "address@address.com", null))
        emailList.add(EmailAddress(Enums.Contacts.EmailTypes.HOME_EMAIL, "address2@address.com", null))
        nextivaContact.emailAddresses = emailList

        val phoneNumbers: ArrayList<PhoneNumber> = ArrayList()
        phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1112221111", null))
        phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "1112221112", null))
        phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1111"))
        phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1112"))
        phoneNumbers.add(PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "1231231234", "123456", "654321"))
        nextivaContact.allPhoneNumbers = phoneNumbers

        nextivaContact.presence = DbPresence("jid@jid.im", Enums.Contacts.PresenceStates.AVAILABLE, 0, "Status", Enums.Contacts.PresenceTypes.AVAILABLE)
        nextivaContact.vCard = DbVCard("jid@jid.im", ByteArray(3))

        nextivaContacts.add(nextivaContact)

        // Contact 2
        val nextivaContact2 = NextivaContact("234567")
        nextivaContact2.contactType = Enums.Contacts.ContactTypes.PERSONAL
        nextivaContact2.jid = "jid2@jid.im"
        nextivaContact2.displayName = "Display Name2"
        nextivaContact2.firstName = "First2"
        nextivaContact2.lastName = "Last2"
        nextivaContact2.hiraganaFirstName = "HFirst2"
        nextivaContact2.hiraganaLastName = "HLast2"
        nextivaContact2.title = "Title2"
        nextivaContact2.company = "Company2"
        nextivaContact2.setIsFavorite(true)
        nextivaContact2.groupId = "Group Id2"
        nextivaContact2.serverUserId = "display.name2"
        nextivaContact2.addresses = arrayListOf(Address("Stree1", "Street2", "12345", "City", "Region", "Country", "Location", null, null))

        val emailList2: ArrayList<EmailAddress> = ArrayList()
        emailList2.add(EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL, "address3@address.com", null))
        emailList2.add(EmailAddress(Enums.Contacts.EmailTypes.HOME_EMAIL, "address4@address.com", null))
        nextivaContact2.emailAddresses = emailList2

        val phoneNumbers2: ArrayList<PhoneNumber> = ArrayList()
        phoneNumbers2.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1112221113", null))
        phoneNumbers2.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1113"))
        phoneNumbers2.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1114"))
        nextivaContact2.allPhoneNumbers = phoneNumbers2

        nextivaContact2.presence = DbPresence("jid2@jid.im", Enums.Contacts.PresenceStates.AVAILABLE, 0, "Status", Enums.Contacts.PresenceTypes.AVAILABLE)
        nextivaContact2.vCard = DbVCard("jid2@jid.im", ByteArray(2))

        nextivaContacts.add(nextivaContact2)

        // Contact 3
        val nextivaContact3 = NextivaContact("234567")
        nextivaContact3.contactType = Enums.Contacts.ContactTypes.PERSONAL
        nextivaContact3.jid = "jid3@jid.im"
        nextivaContact3.displayName = "Display Name3"
        nextivaContact3.firstName = "First3"
        nextivaContact3.lastName = "Last3"
        nextivaContact3.hiraganaFirstName = "HFirst3"
        nextivaContact3.hiraganaLastName = "HLast3"
        nextivaContact3.title = "Title3"
        nextivaContact3.company = "Company3"
        nextivaContact3.setIsFavorite(true)
        nextivaContact3.groupId = "Group Id3"
        nextivaContact3.serverUserId = "display.name3"
        nextivaContact3.addresses = arrayListOf(Address("Stree1", "Street2", "12345", "City", "Region", "Country", "Location", null, null))

        val emailList3: ArrayList<EmailAddress> = ArrayList()
        emailList3.add(EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL, "address5@address.com", null))
        emailList3.add(EmailAddress(Enums.Contacts.EmailTypes.HOME_EMAIL, "address6@address.com", null))
        nextivaContact3.emailAddresses = emailList3

        val phoneNumbers3: ArrayList<PhoneNumber> = ArrayList()
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.OTHER_PHONE, "5556667777", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.MAIN_PHONE, "2221110000", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1112221113", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "3334445555", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.CUSTOM_PHONE, "4445556666", "My Phone"))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.MOBILE_PHONE, "2223334444", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_MOBILE_PHONE, "6667778888", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PAGER, "7778889999", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_FAX, "8889990000", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.PAGER, "4443332222", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.HOME_FAX, "3332221111", null))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1113"))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1114"))
        phoneNumbers3.add(PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "1233334444", "123456", "654321"))
        nextivaContact3.allPhoneNumbers = phoneNumbers3

        nextivaContact3.presence = DbPresence("jid3@jid.im", Enums.Contacts.PresenceStates.AWAY, 0, "Status Three", Enums.Contacts.PresenceTypes.AVAILABLE)
        nextivaContact3.vCard = DbVCard("jid3@jid.im", ByteArray(2))

        nextivaContacts.add(nextivaContact3)

        return nextivaContacts
    }
}