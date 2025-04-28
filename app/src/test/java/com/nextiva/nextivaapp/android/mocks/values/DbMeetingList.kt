package com.nextiva.nextivaapp.android.mocks.values

import com.nextiva.nextivaapp.android.db.model.DbMeeting

object DbMeetingList {
    fun getNextivaMeetingTestList(): ArrayList<DbMeeting>{
        val nextivaMeeting = ArrayList<DbMeeting>()

        val meeting1 = DbMeeting(
            calendarId = 19783,
            name = "15min Format",
            startTime = 1673312400000,
            createdBy = "hugo.calderon+mc2@lab.nextiva.com",
            meetingInfo = "{\"allDay\":false,\"attendees\":[{\"comment\":\"\",\"email\":\"hugo.calderon+mc2@lab.nextiva.com\",\"firstName\":\"Hugo\",\"lastName\":\"Two\",\"optional\":false,\"response\":\"ACCEPTED\",\"responseToken\":\"\",\"status\":\"YES\",\"type\":\"HOST\",\"userId\":\"98a35b46-06cc-11ed-973e-005056a33d5a\"},{\"comment\":\"\",\"email\":\"hugo.calderon+mc1@lab.nextiva.com\",\"firstName\":\"Hugo\",\"lastName\":\"One\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"NTA2MDcxMWMtYmYzMC00ZTE1LTk1ZDQtOWI2ZWJkZWY4OTQz\",\"type\":\"REGULAR\",\"userId\":\"f674cd3e-06cb-11ed-973e-005056a33d5a\"}],\"calendarId\":19783,\"corpAccountNumber\":\"3880947\",\"createdBy\":\"hugo.calderon+mc2@lab.nextiva.com\",\"endDate\":\"2023-01-10T01:30:00Z\",\"endTime\":\"1673314200000\",\"eventId\":4677627,\"eventOwner\":{\"comment\":\"\",\"email\":\"hugo.calderon+mc2@lab.nextiva.com\",\"firstName\":\"Hugo\",\"lastName\":\"Two\",\"optional\":false,\"response\":\"ACCEPTED\",\"status\":\"YES\",\"type\":\"HOST\",\"userId\":\"98a35b46-06cc-11ed-973e-005056a33d5a\"},\"eventType\":\"NEXTIVA\",\"hostEventId\":0,\"linkedAccountEmail\":\"Nextiva Connect Calendar\",\"location\":\"\",\"masterEvent\":false,\"masterEventId\":\"63bcb232524eae63d039e51f\",\"name\":\"15min Format\",\"recurringEvent\":false,\"startDate\":\"2023-01-10T01:00:00Z\",\"startTime\":\"1673312400000\",\"status\":\"COMPLETED\",\"timeZone\":\"America/Mexico_City\",\"userUuid\":\"f674cd3e-06cb-11ed-973e-005056a33d5a\",\"videoConfPhoneNum\":[],\"videoConfUrl\":\"https://mobileconnect.nextos.com/apps/nextiva-connect/#/meeting/2413157415\"}"
        )

        nextivaMeeting.add(meeting1)

        val meeting2 = DbMeeting(
            calendarId = 19783,
            name = "Test rec monthly",
            startTime = 1674667800000,
            createdBy = "sakshi.bhutani+mc2@lab.nextiva.com",
            meetingInfo = "{\"allDay\":false,\"attendees\":[{\"comment\":\"\",\"email\":\"sakshi.bhutani+mc2@lab.nextiva.com\",\"firstName\":\"Sakshi\",\"lastName\":\"Connect 2\",\"optional\":false,\"response\":\"ACCEPTED\",\"responseToken\":\"\",\"status\":\"YES\",\"type\":\"HOST\",\"userId\":\"a2a872ee-d6c6-11ec-8c37-005056a31519\"},{\"comment\":\"\",\"email\":\"sakshi.bhutani+mc3@lab.nextiva.com\",\"firstName\":\"Sakshi\",\"lastName\":\"Connect 3\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"\",\"type\":\"REGULAR\",\"userId\":\"f07dd56d-d6c6-11ec-878b-005056a3635e\"},{\"comment\":\"\",\"email\":\"hugo.calderon+mc2@lab.nextiva.com\",\"firstName\":\"Hugo\",\"lastName\":\"Two\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"\",\"type\":\"REGULAR\",\"userId\":\"98a35b46-06cc-11ed-973e-005056a33d5a\"},{\"comment\":\"\",\"email\":\"hugo.calderon+mc1@lab.nextiva.com\",\"firstName\":\"Hugo\",\"lastName\":\"One\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"\",\"type\":\"REGULAR\",\"userId\":\"f674cd3e-06cb-11ed-973e-005056a33d5a\"}],\"calendarId\":19783,\"corpAccountNumber\":\"3880947\",\"createdBy\":\"sakshi.bhutani+mc2@lab.nextiva.com\",\"endDate\":\"2023-01-25T12:30:00-06:00\",\"endTime\":\"1674671400000\",\"eventId\":3005444,\"eventOwner\":{\"comment\":\"\",\"email\":\"sakshi.bhutani+mc2@lab.nextiva.com\",\"firstName\":\"Sakshi\",\"lastName\":\"Connect 2\",\"optional\":false,\"response\":\"ACCEPTED\",\"status\":\"YES\",\"type\":\"HOST\",\"userId\":\"a2a872ee-d6c6-11ec-8c37-005056a31519\"},\"eventType\":\"NEXTIVA\",\"hostEventId\":0,\"instanceId\":1674667800000,\"linkedAccountEmail\":\"Nextiva Connect Calendar\",\"location\":\"\",\"masterEvent\":false,\"masterEventId\":\"63877ef01ce8bd00e51fa6b7\",\"name\":\"Test rec monthly\",\"recurringEvent\":false,\"startDate\":\"2023-01-25T11:30:00-06:00\",\"startTime\":\"1674667800000\",\"status\":\"COMPLETED\",\"timeZone\":\"America/Chicago\",\"userUuid\":\"f674cd3e-06cb-11ed-973e-005056a33d5a\",\"videoConfPhoneNum\":[],\"videoConfUrl\":\"https://mobileconnect.nextos.com/apps/nextiva-connect/#/meeting/1221423867\"}"
        )

        nextivaMeeting.add(meeting2)

        val meeting3 = DbMeeting(
            calendarId = 19783,
            name = "AvatarTest",
            startTime = 1675976400000,
            createdBy = "hugo.calderon+mc2@lab.nextiva.com",
            meetingInfo = "{\"allDay\":false,\"attendees\":[{\"comment\":\"\",\"email\":\"sakshi.bhutani+mc2@lab.nextiva.com\",\"firstName\":\"Sakshi\",\"lastName\":\"Connect 2\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"\",\"type\":\"REGULAR\",\"userId\":\"a2a872ee-d6c6-11ec-8c37-005056a31519\"},{\"comment\":\"\",\"email\":\"sakshi.bhutani+mc3@lab.nextiva.com\",\"firstName\":\"Sakshi\",\"lastName\":\"Connect 3\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"\",\"type\":\"REGULAR\",\"userId\":\"f07dd56d-d6c6-11ec-878b-005056a3635e\"},{\"comment\":\"\",\"email\":\"tyla.suon+mc3@lab.nextiva.com\",\"firstName\":\"Tyla\",\"lastName\":\"Connect 3\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"\",\"type\":\"REGULAR\",\"userId\":\"7647658d-b440-11ec-99e2-005056a3635e\"},{\"comment\":\"\",\"email\":\"hugo.calderon+mc2@lab.nextiva.com\",\"firstName\":\"Hugo\",\"lastName\":\"Two\",\"optional\":false,\"response\":\"ACCEPTED\",\"responseToken\":\"\",\"status\":\"YES\",\"type\":\"HOST\",\"userId\":\"98a35b46-06cc-11ed-973e-005056a33d5a\"},{\"comment\":\"\",\"email\":\"tyla.suon+mc1@lab.nextiva.com\",\"firstName\":\"Tyla\",\"lastName\":\"Connect 1\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"\",\"type\":\"REGULAR\",\"userId\":\"c9395547-b43f-11ec-b729-005056a33d5a\"},{\"comment\":\"\",\"email\":\"hugo.calderon+mc1@lab.nextiva.com\",\"firstName\":\"Hugo\",\"lastName\":\"One\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"NDAyYmY4MmMtMDM1YS00MjcxLWFlYTAtZGUxNWEyN2EyYjc0\",\"type\":\"REGULAR\",\"userId\":\"f674cd3e-06cb-11ed-973e-005056a33d5a\"},{\"comment\":\"\",\"email\":\"tyla.suon+mc2@lab.nextiva.com\",\"firstName\":\"Tyla\",\"lastName\":\"Connect 2\",\"optional\":false,\"response\":\"NO_RESPONSE\",\"responseToken\":\"\",\"type\":\"REGULAR\",\"userId\":\"2fb57299-b440-11ec-b729-005056a33d5a\"}],\"calendarId\":19783,\"corpAccountNumber\":\"3880947\",\"createdBy\":\"hugo.calderon+mc2@lab.nextiva.com\",\"endDate\":\"2023-02-10T00:30:00Z\",\"endTime\":\"1675989000000\",\"eventId\":5983991,\"eventOwner\":{\"comment\":\"\",\"email\":\"hugo.calderon+mc2@lab.nextiva.com\",\"firstName\":\"Hugo\",\"lastName\":\"Two\",\"optional\":false,\"response\":\"ACCEPTED\",\"status\":\"YES\",\"type\":\"HOST\",\"userId\":\"98a35b46-06cc-11ed-973e-005056a33d5a\"},\"eventType\":\"NEXTIVA\",\"hostEventId\":0,\"linkedAccountEmail\":\"Nextiva Connect Calendar\",\"location\":\"\",\"masterEvent\":false,\"masterEventId\":\"63e570e06a03e47c179769af\",\"name\":\"AvatarTest\",\"recurringEvent\":false,\"startDate\":\"2023-02-09T21:00:00Z\",\"startTime\":\"1675976400000\",\"status\":\"COMPLETED\",\"timeZone\":\"America/Mexico_City\",\"userUuid\":\"f674cd3e-06cb-11ed-973e-005056a33d5a\",\"videoConfPhoneNum\":[],\"videoConfUrl\":\"https://mobileconnect.nextos.com/apps/nextiva-connect/#/meeting/9464892759\"}"
        )

        nextivaMeeting.add(meeting3)
        return nextivaMeeting
    }
}