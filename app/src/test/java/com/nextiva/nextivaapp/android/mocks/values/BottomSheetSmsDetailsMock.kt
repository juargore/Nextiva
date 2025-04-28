package com.nextiva.nextivaapp.android.mocks.values

import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsParticipant

class BottomSheetSmsDetailsMock {

    val participants = ArrayList<SmsParticipant>().apply {
        add(
            SmsParticipant(
                name = "Mirsada Four",
                emailId = "",
                phoneNumber = "19143390856",
                userUUID = "960c1298-f28b-11ee-b09f-0050569282e8",
                teamUUID = "",
                contacts = null
            )
        )
        add(
            SmsParticipant(
                name = "Shared UserOne",
                emailId = "",
                phoneNumber = "16232239093",
                userUUID = "995db22e-5719-11ee-82e9-00505692b190",
                teamUUID = "972123",
                contacts = null
            )
        )
        add(
            SmsParticipant(
                name = "Peter Connect",
                emailId = "",
                phoneNumber = "19702872263",
                userUUID = "be04617a-d7c1-11ec-878b-005056a3635e",
                teamUUID = "972123",
                contacts = null
            )
        )
        add(
            SmsParticipant(
                name = "Mirsada Three",
                emailId = "",
                phoneNumber = "17203103342",
                userUUID = "1394b85f-f28b-11ee-82bd-00505692b190",
                teamUUID = "972123",
                contacts = null
            )
        )
    }

    val teamMembers = ArrayList<SmsParticipant>().apply {
        add(
            SmsParticipant(
                name = "Shared UserOne",
                emailId = "",
                phoneNumber = "16232239093",
                userUUID = "995db22e-5719-11ee-82e9-00505692b190",
                teamUUID = "972123",
                contacts = null
            )
        )
        add(
            SmsParticipant(
                name = "Peter Connect",
                emailId = "",
                phoneNumber = "19702872263",
                userUUID = "be04617a-d7c1-11ec-878b-005056a3635e",
                teamUUID = "972123",
                contacts = null
            )
        )
        add(
            SmsParticipant(
                name = "Mirsada Three",
                emailId = "",
                phoneNumber = "17203103342",
                userUUID = "1394b85f-f28b-11ee-82bd-00505692b190",
                teamUUID = "972123",
                contacts = null
            )
        )
    }

    val teams = ArrayList<SmsTeam>().apply {
        add(
            SmsTeam(
                id = 56,
                legacyId = "972123",
                teamId = "972123",
                teamName = "Engineering",
                teamPhoneNumber = "12174329604",
                members = null
            )
        )
    }

    val conversationDetails = SmsConversationDetails(
        groupId = "842d6eec-54b4-38b1-bf55-d855bcbed372",
        groupValue = "842d6eec-54b4-38b1-bf55-d855bcbed372",
        teams = teams,
        teamMembers = teamMembers,
        participants = participants
    )

    val dbResponse = ArrayList<DbResponse<NextivaContact>>().apply {
        add(DbResponse(NextivaContact().apply {
            userId = "995db22e-5719-11ee-82e9-00505692b190"
            firstName = "Shared"
            lastName = "UserOne"
            displayName = "Shared UserOne"
            phoneNumbers = ArrayList<PhoneNumber>().apply {
                add(PhoneNumber().apply {
                    number = "+16232239093x9093"
                    strippedNumber = "16232239093"
                })
            }
        }))

        add(DbResponse(NextivaContact().apply {
            userId = "960c1298-f28b-11ee-b09f-0050569282e8"
            firstName = "Mirsada"
            lastName = "Four"
            displayName = "Mirsada Four"
            phoneNumbers = ArrayList<PhoneNumber>().apply {
                add(PhoneNumber().apply {
                    number = "19143390856"
                    strippedNumber = "19143390856"
                })
            }
        }))

        add(DbResponse(NextivaContact().apply {
            userId = "be04617a-d7c1-11ec-878b-005056a3635e"
            firstName = "Peter"
            lastName = "Connect"
            displayName = "Peter Connect"
            phoneNumbers = ArrayList<PhoneNumber>().apply {
                add(PhoneNumber().apply {
                    number = "+19702872263x2263"
                    strippedNumber = "19702872263"
                })
            }
        }))

        add(DbResponse(NextivaContact().apply {
            userId = "1394b85f-f28b-11ee-82bd-00505692b190"
            firstName = "Mirsada"
            lastName = "Three"
            displayName = "Mirsada Three"
            phoneNumbers = ArrayList<PhoneNumber>().apply {
                add(PhoneNumber().apply {
                    number = "+17203103342x3342"
                    strippedNumber = "17203103342"
                })
            }
        }))
        add(DbResponse(NextivaContact().apply {
            userId = "1394b85f-f28b-11ee-82bd-00505692b190"
            firstName = "Engineering"
            lastName = ""
            displayName = "Engineering"
            contactType = Enums.Contacts.ContactTypes.CONNECT_TEAM
            phoneNumbers = ArrayList<PhoneNumber>().apply {
                add(PhoneNumber().apply {
                    number = "+12174329604x9604"
                    strippedNumber = "12174329604"
                })
            }
        }))
    }
}