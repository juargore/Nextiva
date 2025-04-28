package com.nextiva.nextivaapp.android.models

import androidx.room.ColumnInfo

data class DbTableCountModel(@ColumnInfo(name = "addressCount") var addressCount: Int?,
                             @ColumnInfo(name = "attachmentCount") var attachmentCount: Int?,
                             @ColumnInfo(name = "callLogCount") var callLogCount: Int?,
                             @ColumnInfo(name = "contactCount") var contactCount: Int?,
                             @ColumnInfo(name = "dateCount") var dateCount: Int?,
                             @ColumnInfo(name = "emailCount") var emailCount: Int?,
                             @ColumnInfo(name = "groupCount") var groupCount: Int?,
                             @ColumnInfo(name = "loggingCount") var loggingCount: Int?,
                             @ColumnInfo(name = "chatMessageCount") var chatMessageCount: Int?,
                             @ColumnInfo(name = "messageStateCount") var messageStateCount: Int?,
                             @ColumnInfo(name = "participantCount") var participantCount: Int?,
                             @ColumnInfo(name = "phoneCount") var phoneCount: Int?,
                             @ColumnInfo(name = "presenceCount") var presenceCount: Int?,
                             @ColumnInfo(name = "recipientCount") var recipientCount: Int?,
                             @ColumnInfo(name = "senderCount") var senderCount: Int?,
                             @ColumnInfo(name = "sessionCount") var sessionCount: Int?,
                             @ColumnInfo(name = "smsMessageCount") var smsMessageCount: Int?,
                             @ColumnInfo(name = "socialMediaAccountCount") var socialMediaAccountCount: Int?,
                             @ColumnInfo(name = "vCardCount") var vCardCount: Int?,
                             @ColumnInfo(name = "voicemailCount") var voicemailCount: Int?)