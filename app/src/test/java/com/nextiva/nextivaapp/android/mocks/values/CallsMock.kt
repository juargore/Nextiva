package com.nextiva.nextivaapp.android.mocks.values

import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.models.CallsDbReturnModel
import com.nextiva.nextivaapp.android.models.ConnectContactStripped

class CallsMock {

    private val missedData = listOf(
        CallsDbReturnModel(
            callLogId = "603a94f4-7551-4390-ab64-cdc4d1d57d7f",
            displayName = "Tyla Connect 1",
            callDateTime = 1686936363327L,
            countryCode = "1",
            phoneNumber = "17203103342",
            callType = "missed",
            isRead = 0,
            callWithContactId = "c9395547-b43f-11ec-b729-005056a33d5a",
            callWithContacts = listOf(
                ConnectContactStripped(
                    dbId = 1597,
                    contactTypeId = "c9395547-b43f-11ec-b729-005056a33d5a",
                    contactType = 7,
                    displayName = "Tyla Connect 1",
                    firstName = "Tyla",
                    lastName = "Connect 1",
                    favorite = false,
                    uiName = "Tyla Connect 1",
                    phoneNumbers = listOf(
                        PhoneNumber(
                            id = 1772,
                            contactId = 1597,
                            number = "7203103342",
                            strippedNumber = "7203103342",
                            type = 0,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "5db00145-9536-485e-b3cd-044e1a5fe9e0"
                        ),
                        PhoneNumber(
                            id = 1773,
                            contactId = 1597,
                            number = "3342",
                            strippedNumber = "3342",
                            type = 1,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "5db00145-9536-485e-b3cd-044e1a5fe9e0"
                        )
                    ), presences = listOf(
                        DbPresence(
                            id = 60,
                            contactId = 1597,
                            userId = "c9395547-b43f-11ec-b729-005056a33d5a",
                            jid = null,
                            state = 6,
                            type = null,
                            priority = null,
                            status = "RC -- make it the BEST phone app in the world!!!",
                            statusExpiryTime = "0001-01-01T00:00:00Z",
                            transactionId = null,
                            inCall = false
                        )
                    ), vCards = listOf()
                )
            ),
            duration = null,
            address = null,
            name = null,
            userId = null,
            messageId = null,
            read = null,
            transcription = null,
            rating = null,
            formattedPhoneNumber = "+17203103342",
            actualVoicemailId = null,
            voicemailFromContacts = listOf(), time = 1686936363327,
            callerId = null,
            callStartTime = null,
        ),
        CallsDbReturnModel(
            callLogId = "20104db1-b633-4463-ab71-67a7fa1922db",
            displayName = "Doug Connect 1",
            callDateTime = 1688116323917,
            countryCode = "1",
            phoneNumber = "16232328846",
            callType = "missed",
            isRead = 0,
            callWithContactId = "32e97784-a554-11ec-b729-005056a33d5a",
            callWithContacts = listOf(
                ConnectContactStripped(
                    dbId = 1191,
                    contactTypeId = "32e97784-a554-11ec-b729-005056a33d5a",
                    contactType = 7,
                    displayName = "Doug Connect 1",
                    firstName = "Doug",
                    lastName = "Connect 1",
                    favorite = false,
                    uiName = "Doug Connect 1",
                    phoneNumbers = listOf(
                        PhoneNumber(
                            id = 1241,
                            contactId = 1191,
                            number = "6232328846",
                            strippedNumber = "6232328846",
                            type = 0,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "ccd3912a-d2e9-425d-a0d9-881dcd43d071"
                        ),
                        PhoneNumber(
                            id = 1242,
                            contactId = 1191,
                            number = "8542",
                            strippedNumber = "8542",
                            type = 1, label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "ccd3912a-d2e9-425d-a0d9-881dcd43d071"
                        )
                    ),
                    presences = listOf(
                        DbPresence(
                            id = 5,
                            contactId = 1191,
                            userId = "32e97784-a554-11ec-b729-005056a33d5a",
                            jid = null,
                            state = 6,
                            type = null,
                            priority = null,
                            status = "",
                            statusExpiryTime = "0001-01-01T00:00:00Z",
                            transactionId = null,
                            inCall = false
                        )
                    ), vCards = listOf()
                )
            ),
            duration = null,
            address = null,
            name = null, userId = null,
            messageId = null, read = null,
            transcription = null, rating = null,
            formattedPhoneNumber = "+16232328846",
            actualVoicemailId = null,
            voicemailFromContacts = listOf(),
            time = 1686936363327,
            callerId = null,
            callStartTime = null,
        )
    )

    val voicemailData = listOf(
        CallsDbReturnModel(
            callLogId = null,
            displayName = null,
            callDateTime = null,
            countryCode = null,
            phoneNumber = null,
            callType = null,
            isRead = null,
            callWithContactId = null,
            callWithContacts = listOf(),
            duration = 0, address = "19702872255",
            name = "Tyla Connect 3",
            userId = "7647658d-b440-11ec-99e2-005056a3635e",
            messageId = "3172aee5-1399-470f-bd1f-0ec798bab163",
            read = true,
            transcription = "Testing, one, two, three. Testing, one, two, three.",
            rating = "UNKNOWN",
            formattedPhoneNumber = null,
            actualVoicemailId = "b958ff56-a96e-4098-b10f-3a3217c037f0",
            voicemailFromContacts = listOf(
                ConnectContactStripped(
                    dbId = 1188,
                    contactTypeId = "7647658d-b440-11ec-99e2-005056a3635e",
                    contactType = 7,
                    displayName = "Tyla Connect 3",
                    firstName = "Tyla",
                    lastName = "Connect 3",
                    favorite = false,
                    uiName = "Tyla Connect 3",
                    phoneNumbers = listOf(
                        PhoneNumber(
                            id = 1235,
                            contactId = 1188,
                            number = "9702872255",
                            strippedNumber = "9702872255",
                            type = 0,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "ccd3912a-d2e9-425d-a0d9-881dcd43d071"
                        ),
                        PhoneNumber(
                            id = 1236,
                            contactId = 1188,
                            number = "2255",
                            strippedNumber = "2255",
                            type = 1,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "ccd3912a-d2e9-425d-a0d9-881dcd43d071"
                        )
                    ), presences = listOf(
                        DbPresence(
                            id = 18,
                            contactId = 1188,
                            userId = "7647658d-b440-11ec-99e2-005056a3635e",
                            jid = null,
                            state = 6,
                            type = null,
                            priority = null,
                            status = "RC -- make it the BEST phone app in the world!!!",
                            statusExpiryTime = "0001-01-01T00:00:00Z",
                            transactionId = null,
                            inCall = false
                        )
                    ),
                    vCards = listOf()
                )
            ),
            time = 1688150624014,
            callerId = null,
            callStartTime = null
        ),
        CallsDbReturnModel(
            callLogId = null,
            displayName = null,
            callDateTime = null,
            countryCode = null,
            phoneNumber = null,
            callType = null,
            isRead = null,
            callWithContactId = null,
            callWithContacts = listOf(),
            duration = 0,
            address = "19702872261",
            name = "Mirsada Connect 3",
            userId = "8fd8ee5a-b443-11ec-99e2-005056a3635e",
            messageId = "83a5748e-67c6-4c1b-a37f-dca074c76ef2",
            read = false,
            transcription = "The voicemail.",
            rating = "UNKNOWN",
            formattedPhoneNumber = null,
            actualVoicemailId = "d987848e-be65-4e52-bbc3-4d16fec47e65",
            voicemailFromContacts = listOf(
                ConnectContactStripped(
                    dbId = 1185,
                    contactTypeId = "8fd8ee5a-b443-11ec-99e2-005056a3635e",
                    contactType = 7,
                    displayName = "Mirsada Connect 3",
                    firstName = "Mirsada", lastName = "Connect 3",
                    favorite = false,
                    uiName = "Mirsada Connect 3",
                    phoneNumbers = listOf(
                        PhoneNumber(
                            id = 1229,
                            contactId = 1185,
                            number = "9702872261",
                            strippedNumber = "9702872261",
                            type = 0,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "ccd3912a-d2e9-425d-a0d9-881dcd43d071"
                        ),
                        PhoneNumber(
                            id = 1230,
                            contactId = 1185,
                            number = "2261",
                            strippedNumber = "2261",
                            type = 1,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "ccd3912a-d2e9-425d-a0d9-881dcd43d071"
                        )
                    ), presences = listOf(
                        DbPresence(
                            id = 4,
                            contactId = 1185,
                            userId = "8fd8ee5a-b443-11ec-99e2-005056a3635e",
                            jid = null,
                            state = 6,
                            type = null,
                            priority = null,
                            status = "",
                            statusExpiryTime = "0001-01-01T00:00:00Z",
                            transactionId = null,
                            inCall = false
                        )
                    ), vCards = listOf()
                )
            ),
            time = 1687893964548,
            callerId = null,
            callStartTime = null
        )
    )

    val searchTerm = "Sergey"
    private val receivedData = listOf(
        CallsDbReturnModel(
            callLogId = "5c648563-0879-4a73-a1b4-8b65dfb62cbe",
            displayName = "Sergey Connect 2",
            callDateTime = 1686611087520,
            countryCode = "1",
            phoneNumber = "12792071887",
            callType = "received",
            isRead = 1,
            callWithContactId = "ad45af34-fe03-11ec-a279-005056a33d5a",
            callWithContacts = listOf(
                ConnectContactStripped(
                    dbId = 1165,
                    contactTypeId = "ad45af34-fe03-11ec-a279-005056a33d5a",
                    contactType = 7,
                    displayName = "Sergey Connect 2",
                    firstName = "Sergey",
                    lastName = "Connect 2",
                    favorite = false,
                    uiName = "Sergey Connect 2",
                    phoneNumbers = listOf(
                        PhoneNumber(
                            id = 1190,
                            contactId = 1165,
                            number = "2792071887",
                            strippedNumber = "2792071887",
                            type = 0,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "ccd3912a-d2e9-425d-a0d9-881dcd43d071"
                        ),
                        PhoneNumber(
                            id = 1191,
                            contactId = 1165,
                            number = "1887",
                            strippedNumber = "1887",
                            type = 1,
                            label = null,
                            extension = "1234",
                            pinOne = null,
                            pinTwo = null,
                            transactionId = "ccd3912a-d2e9-425d-a0d9-881dcd43d071"
                        )
                    ),
                    presences = listOf(
                        DbPresence(
                            id = 6,
                            contactId = 1165,
                            userId = "ad45af34-fe03-11ec-a279-005056a33d5a",
                            jid = null,
                            state = 6,
                            type = null,
                            priority = null,
                            status = "",
                            statusExpiryTime = "0001-01-01T00:00:00Z",
                            transactionId = null,
                            inCall = false
                        )
                    ), vCards = listOf()
                )
            ),
            duration = null,
            address = null,
            name = null,
            userId = null,
            messageId = null,
            read = null,
            transcription = null,
            rating = null,
            formattedPhoneNumber = "+12792071887",
            actualVoicemailId = null,
            voicemailFromContacts = listOf(), time = 1686611087520,
            callerId = null,
            callStartTime = null
        )
    )

    val callsData: List<CallsDbReturnModel> = mutableListOf<CallsDbReturnModel>().apply {
        addAll(receivedData)
        addAll(missedData)
    }

    val allData: List<CallsDbReturnModel> = mutableListOf<CallsDbReturnModel>().apply {
        addAll(receivedData)
        addAll(missedData)
        addAll(voicemailData)
    }

}