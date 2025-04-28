//package com.nextiva.nextivaapp.android
//
//import android.Manifest
//import android.support.test.InstrumentationRegistry
//import android.support.test.rule.GrantPermissionRule
//import android.support.test.uiautomator.UiDevice
//import org.junit.Rule
//import org.junit.rules.RuleChain
//
//
//open class BaseModel{
//    @Rule
//    @JvmField
//    val screenshotRule = RuleChain
//            .outerRule(GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//            .around(ScreenShotRule())
//    //UIAutomator Infrastructure
//    @JvmField
//    val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//
//    @JvmField
//    val uiContext = InstrumentationRegistry.getContext()
//
//
//
//    companion object {
//        //For UIAutomator
//        //Gmail app identifiers
//        const val gmailPackageName = "com.google.android.gm"
//        const val gmailFromFieldID = "$gmailPackageName:id/from_account_name"
//        const val gmailSubjectFieldID = "$gmailPackageName:id/subject"
//        const val gmailBodyFieldID = "$gmailPackageName:id/wc_body"
//        const val gmailSendButtonID = "$gmailPackageName:id/send"
//
//    }
//}