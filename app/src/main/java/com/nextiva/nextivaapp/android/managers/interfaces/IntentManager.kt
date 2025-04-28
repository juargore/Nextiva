package com.nextiva.nextivaapp.android.managers.interfaces

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import java.io.File

interface IntentManager {

    fun callPhone(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            @RequiresPermission numberToCall: String)

    fun sendPersonalSMS(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            numberToSMS: String,
            name: String,
            subject: String,
            body: String)

    fun sendEmail(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            email: String,
            name: String,
            subject: String,
            body: String,
            attachment: File?)

    fun showUrl(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            url: String)

    fun addToLocalContacts(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            name: String,
            numbers: ArrayList<PhoneNumber>?,
            emails: ArrayList<EmailAddress>?)

    fun addToLocalContacts(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            name: String,
            phone: String?,
            phoneType: String?,
            email: String?,
            emailType: String?)

    fun isAbleToMakePhoneCall(context: Context): Boolean

    fun getInitialIntent(context: Context): Intent

    fun newActiveCallActivityIntent(context: Context, participantInfo: ParticipantInfo, retrievalNumber: String?): Intent

    fun newActiveCallActivityIntent(context: Context, participantInfo: ParticipantInfo): Intent

    fun navigateToInternetSettings(context: Context)

    fun navigateToPermissionSettings(context: Context)
}