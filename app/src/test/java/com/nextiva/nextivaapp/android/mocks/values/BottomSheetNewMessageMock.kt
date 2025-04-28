package com.nextiva.nextivaapp.android.mocks.values

import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact

class BottomSheetNewMessageMock {

    val phoneNumberList = ArrayList<String>().apply {
        add("16232239093")
        add("19143390856")
        add("19702872263")
        add("17203103342")
    }

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
    }
}