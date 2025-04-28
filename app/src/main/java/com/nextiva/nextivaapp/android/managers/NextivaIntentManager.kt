package com.nextiva.nextivaapp.android.managers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.PhoneNumberUtils
import androidx.core.content.FileProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.ConnectMainActivity
import com.nextiva.nextivaapp.android.DevicePhoneNumberActivity
import com.nextiva.nextivaapp.android.LicenseAcceptanceActivity
import com.nextiva.nextivaapp.android.LoginActivity
import com.nextiva.nextivaapp.android.OneActiveCallActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.util.StringUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
open class NextivaIntentManager @Inject constructor(var dialogManager: DialogManager,
                                                    var sessionManager: SessionManager,
                                                    var settingsManager: SettingsManager,
                                                    var analyticsManager: AnalyticsManager) : IntentManager {

    @SuppressWarnings("MissingPermission")
    private fun performCallIntent(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            uri: Uri,
            humanReadableValue: String,
            chooserTitle: String) {

        val callIntent = Intent(Intent.ACTION_CALL, uri)
        val numberAvailableApps = numberAvailableApps(activity, callIntent)

        if (numberAvailableApps == 0) {
            analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_SHOWN)

            dialogManager.showDialog(
                    activity,
                    activity.getString(R.string.intent_util_no_app_found_title),
                    activity.getString(R.string.intent_util_no_app_found_message, humanReadableValue),
                    activity.getString(R.string.general_ok))
            { _, _ ->
                analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED)
            }

        } else if (numberAvailableApps == 1 || chooserTitle.isEmpty()) {
            activity.startActivity(callIntent)

        } else {
            activity.startActivity(Intent.createChooser(callIntent, chooserTitle))
        }
    }

    private fun performAddToLocalContacts(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            addToLocalContactsIntent: Intent,
            humanReadableValue: String?,
            chooserTitle: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                activity.startActivity(Intent.createChooser(addToLocalContactsIntent, chooserTitle))

            } catch (e: ActivityNotFoundException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_SHOWN)

                dialogManager.showDialog(
                        activity,
                        activity.getString(R.string.intent_util_no_app_found_title),
                        activity.getString(R.string.intent_util_no_app_found_message, humanReadableValue),
                        activity.getString(R.string.general_ok))
                { _, _ ->
                    analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED)
                }
            }

        } else {
            val numberAvailableApps = numberAvailableApps(activity, addToLocalContactsIntent)

            if (numberAvailableApps == 0) {
                analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_SHOWN)

                dialogManager.showDialog(
                        activity,
                        activity.getString(R.string.intent_util_no_app_found_title),
                        activity.getString(R.string.intent_util_no_app_found_message, humanReadableValue),
                        activity.getString(R.string.general_ok))
                { _, _ ->
                    analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED)
                }

            } else if (numberAvailableApps == 1 || chooserTitle.isEmpty()) {
                activity.startActivity(addToLocalContactsIntent)

            } else {
                activity.startActivity(Intent.createChooser(addToLocalContactsIntent, chooserTitle))
            }
        }
    }

    private fun performViewIntent(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            viewUri: Uri,
            humanReadableValue: String,
            chooserTitle: String,
            subject: String,
            body: String,
            attachment: File?) {

        val viewIntent = Intent(Intent.ACTION_VIEW, viewUri)

        if (subject.isNotEmpty()) {
            viewIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (body.isNotEmpty()) {
            viewIntent.putExtra(Intent.EXTRA_TEXT, StringUtil.fromHtml(body))
        }

        attachment?.let {
            viewIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(it))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                activity.startActivity(Intent.createChooser(viewIntent, chooserTitle))

            } catch (e: ActivityNotFoundException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_SHOWN)

                dialogManager.showDialog(
                        activity,
                        activity.getString(R.string.intent_util_no_app_found_title),
                        activity.getString(R.string.intent_util_no_app_found_message, humanReadableValue),
                        activity.getString(R.string.general_ok))
                { _, _ ->
                    analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED)
                }
            }

        } else {
            val numberAvailableApps = numberAvailableApps(activity, viewIntent)

            if (numberAvailableApps == 0) {
                analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_SHOWN)

                dialogManager.showDialog(
                        activity,
                        activity.getString(R.string.intent_util_no_app_found_title),
                        activity.getString(R.string.intent_util_no_app_found_message, humanReadableValue),
                        activity.getString(R.string.general_ok))
                { _, _ ->
                    analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED)
                }

            } else if (numberAvailableApps == 1 || chooserTitle.isEmpty()) {
                activity.startActivity(viewIntent)

            } else {
                activity.startActivity(Intent.createChooser(viewIntent, chooserTitle))
            }
        }
    }

    private fun performEmailIntent(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            viewUri: Uri,
            humanReadableValue: String,
            chooserTitle: String,
            to: String,
            subject: String,
            body: String,
            attachment: File?) {

        val viewIntent = Intent(Intent.ACTION_SEND, viewUri)
        viewIntent.type = "message/rfc822"

        if (to.isNotEmpty()) {
            viewIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        }

        if (subject.isNotEmpty()) {
            viewIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (body.isNotEmpty()) {
            viewIntent.putExtra(Intent.EXTRA_TEXT, StringUtil.fromHtml(body))
        }

        attachment?.let {
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            viewIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            val contentUri = FileProvider.getUriForFile(activity,
            "${BuildConfig.APPLICATION_ID}.provider",
            it)

            viewIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                activity.startActivity(Intent.createChooser(viewIntent, chooserTitle))

            } catch (e: ActivityNotFoundException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_SHOWN)

                dialogManager.showDialog(
                        activity,
                        activity.getString(R.string.intent_util_no_app_found_title),
                        activity.getString(R.string.intent_util_no_app_found_message, humanReadableValue),
                        activity.getString(R.string.general_ok))
                { _, _ ->
                    analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED)
                }
            }

        } else {
            val numberAvailableApps = numberAvailableApps(activity, viewIntent)

            if (numberAvailableApps == 0) {
                analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_SHOWN)

                dialogManager.showDialog(
                        activity,
                        activity.getString(R.string.intent_util_no_app_found_title),
                        activity.getString(R.string.intent_util_no_app_found_message, humanReadableValue),
                        activity.getString(R.string.general_ok))
                { _, _ ->
                    analyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_APP_FOUND_DIALOG_OK_BUTTON_PRESSED)
                }

            } else if (numberAvailableApps == 1 || chooserTitle.isEmpty()) {
                activity.startActivity(viewIntent)

            } else {
                activity.startActivity(Intent.createChooser(viewIntent, chooserTitle))
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------------------
    private fun numberAvailableApps(context: Context, intent: Intent): Int {
        val packageManager = context.packageManager
        val resolveInfoList: List<ResolveInfo> = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfoList.size
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // IntentManager Methods
    // --------------------------------------------------------------------------------------------
    override fun callPhone(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            numberToCall: String) {

        performCallIntent(activity,
                analyticsScreenName,
                Uri.parse("tel:$numberToCall"),
                PhoneNumberUtils.formatNumber(numberToCall, Locale.getDefault().country),
                activity.getString(R.string.intent_util_chooser_title_call))
    }

    override fun sendPersonalSMS(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            numberToSMS: String,
            name: String,
            subject: String,
            body: String) {

        performViewIntent(activity,
                analyticsScreenName,
                Uri.parse("sms:$numberToSMS"),
                activity.getString(R.string.intent_util_chooser_sms_to, name),
                activity.getString(R.string.intent_util_chooser_title_sms),
                subject,
                body,
                null)
    }

    override fun sendEmail(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            email: String,
            name: String,
            subject: String,
            body: String,
            attachment: File?) {

        performEmailIntent(activity,
                analyticsScreenName,
                Uri.parse("mailto:${email}?subject=${subject}&body=${body}"),
                activity.getString(R.string.intent_util_chooser_email_to, name),
                activity.getString(R.string.intent_util_chooser_title_email),
                email,
                subject,
                body,
                attachment)
    }

    override fun showUrl(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            url: String) {

        performViewIntent(activity,
                analyticsScreenName,
                Uri.parse(url),
                url,
                activity.getString(R.string.intent_util_chooser_title_url),
                "",
                "",
                null)
    }

    override fun addToLocalContacts(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            name: String,
            numbers: ArrayList<PhoneNumber>?,
            emails: ArrayList<EmailAddress>?) {

        val addToLocalContactsIntent = Intent(Intent.ACTION_INSERT)
        addToLocalContactsIntent.type = ContactsContract.Contacts.CONTENT_TYPE
        val data = ArrayList<ContentValues>()
        addToLocalContactsIntent.putExtra(ContactsContract.Intents.Insert.NAME, name)

        numbers?.let {
            for (number in it) {
                val row = ContentValues()
                row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number.number)
                row.put(ContactsContract.CommonDataKinds.Phone.TYPE, number.type)
                data.add(row)
            }
        }

        emails?.let {
            for (email in it) {
                val row = ContentValues()
                row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                row.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.address)
                row.put(ContactsContract.CommonDataKinds.Email.TYPE, email.type)
                data.add(row)
            }
        }

        addToLocalContactsIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)

        performAddToLocalContacts(activity,
                analyticsScreenName,
                addToLocalContactsIntent,
                name,
                activity.getString(R.string.intent_util_chooser_title_add_local_contact))
    }

    override fun addToLocalContacts(
            activity: Activity,
            @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
            name: String,
            phone: String?,
            phoneType: String?,
            email: String?,
            emailType: String?) {

        val addToLocalContactsIntent = Intent(Intent.ACTION_INSERT)
        addToLocalContactsIntent.type = ContactsContract.Contacts.CONTENT_TYPE
        addToLocalContactsIntent.putExtra(ContactsContract.Intents.Insert.NAME, name)

        if (!phone.isNullOrEmpty()) {
            addToLocalContactsIntent.putExtra(ContactsContract.Intents.Insert.PHONE,
                    PhoneNumberUtils.formatNumber(phone, Locale.getDefault().country))
        }

        if (!phoneType.isNullOrEmpty()) {
            addToLocalContactsIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, phoneType)
        }

        if (!email.isNullOrEmpty()) {
            addToLocalContactsIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, email)
        }

        if (!emailType.isNullOrEmpty()) {
            addToLocalContactsIntent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, emailType)

        }

        performAddToLocalContacts(activity,
                analyticsScreenName,
                addToLocalContactsIntent,
                name,
                activity.getString(R.string.intent_util_chooser_title_add_local_contact))
    }

    override fun isAbleToMakePhoneCall(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }

    override fun getInitialIntent(context: Context): Intent {
        return if (sessionManager.isLicenseApproved) {
            if (!settingsManager.phoneNumber.isNullOrEmpty()) {
                if (sessionManager.userDetails != null) {
                    Intent(context, ConnectMainActivity::class.java)
                } else {
                    LoginActivity.newIntent(context)
                }

            } else {
                DevicePhoneNumberActivity.newOnboardingIntent(context)
            }

        } else {
            LicenseAcceptanceActivity.newIntent(context)
        }
    }

    override fun newActiveCallActivityIntent(context: Context, participantInfo: ParticipantInfo): Intent {
        return newActiveCallActivityIntent(context, participantInfo, null)
    }

    override fun newActiveCallActivityIntent(context: Context, participantInfo: ParticipantInfo, retrievalNumber: String?): Intent {
        return OneActiveCallActivity.newIntent(context, participantInfo, retrievalNumber)
    }

    override fun navigateToInternetSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        context.startActivity(intent)
    }

    override fun navigateToPermissionSettings(context: Context) {
        val uri = Uri.fromParts("package", context.packageName, null)

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = uri

        context.startActivity(intent)
    }
    // --------------------------------------------------------------------------------------------
}