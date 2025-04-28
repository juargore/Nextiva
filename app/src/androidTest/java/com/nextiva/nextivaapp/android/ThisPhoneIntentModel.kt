//package com.nextiva.nextivaapp.android
//
//import android.support.test.espresso.matcher.ViewMatchers.withId
//import android.support.test.rule.ActivityTestRule
//import arrow.core.Try
//
//class ThisPhoneIntentModel (val activityRule: ThisPhoneActivityRule): BaseModel(){
//
//
//
//    fun thisPhoneScreenIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(thisPhoneNumberEntryField))
//            true
//        }
//    }
//
//    companion object{
//        private const val thisPhoneNumberEntryField = R.id.device_phone_number_text_view
//    }
//}
//
//typealias ThisPhoneActivityRule = ActivityTestRule<DevicePhoneNumberActivity>