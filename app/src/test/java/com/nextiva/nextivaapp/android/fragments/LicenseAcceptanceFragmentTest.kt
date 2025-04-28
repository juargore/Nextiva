package com.nextiva.nextivaapp.android.fragments
//
//import android.content.Intent
//import android.widget.Button
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeLicenseAcceptanceFragment
//import com.nextiva.nextivaapp.android.viewmodels.LicenseAcceptanceViewModel
//import com.nhaarman.mockito_kotlin.any
//import com.nhaarman.mockito_kotlin.mock
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.ArgumentCaptor
//import org.mockito.ArgumentMatchers.eq
//import org.mockito.Mockito.verify
//import org.powermock.api.mockito.PowerMockito
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.shadows.ShadowApplication
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import javax.inject.Inject
//
//
//@RunWith(RobolectricTestRunner::class)
//class LicenseAcceptanceFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var intentManager: IntentManager
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    private val mockViewModel: LicenseAcceptanceViewModel = mock()
//    private val mockFragmentListener: LicenseAcceptanceFragment.LicenseAcceptanceFragmentListener = mock()
//
//    private val licenseAcceptanceFragment: LicenseAcceptanceFragment = LicenseAcceptanceFragment.newInstance()
//
//    private val initialIntent: Intent = Intent()
//
//    override fun setup() {
//        super.setup()
//        SupportFragmentTestUtil.startFragment(licenseAcceptanceFragment, FakeLicenseAcceptanceFragment::class.java)
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//        licenseAcceptanceFragment.setViewModelForTest(mockViewModel)
//        licenseAcceptanceFragment.setFragmentListenerForTest(mockFragmentListener)
//        PowerMockito.`when`(intentManager.getInitialIntent(licenseAcceptanceFragment.activity!!)).thenReturn(initialIntent)
//    }
//
//    override fun after() {
//        super.after()
//        licenseAcceptanceFragment.onPause()
//        licenseAcceptanceFragment.onStop()
//        licenseAcceptanceFragment.onDestroy()
//    }
//
//    @Test
//    fun onLicenseAcceptanceAcceptButtonClick_showsAcceptDialog() {
//        val acceptButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_accept_button)
//
//        if (acceptButton.isClickable) {
//            acceptButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(R.string.license_agreement_title),
//                eq(R.string.license_agreement_accept_dialog_text),
//                eq(R.string.license_agreement_accept_dialog_positive_button_text),
//                buttonCaptor.capture(),
//                eq(R.string.license_agreement_accept_dialog_negative_button_text),
//                any())
//    }
//
//    @Test
//    fun onLicenseAcceptanceAcceptButtonClick_callsToAnalyticsManager() {
//        val acceptButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_accept_button)
//
//        if (acceptButton.isClickable) {
//            acceptButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LICENSE_AGREEMENT, Enums.Analytics.EventName.ACCEPT_BUTTON_PRESSED)
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LICENSE_AGREEMENT, Enums.Analytics.EventName.ACCEPT_LICENSE_AGREEMENT_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun onLicenseAcceptanceAcceptButtonClick_selectAccept_callsToAnalyticsManager() {
//        val acceptButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_accept_button)
//
//        if (acceptButton.isClickable) {
//            acceptButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(R.string.license_agreement_title),
//                eq(R.string.license_agreement_accept_dialog_text),
//                eq(R.string.license_agreement_accept_dialog_positive_button_text),
//                buttonCaptor.capture(),
//                eq(R.string.license_agreement_accept_dialog_negative_button_text),
//                any())
//
//        val dialog = MaterialDialog.Builder(licenseAcceptanceFragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LICENSE_AGREEMENT, Enums.Analytics.EventName.ACCEPT_LICENSE_AGREEMENT_DIALOG_ACCEPT_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onLicenseAcceptanceAcceptButtonClick_selectAccept_navigatesToInitialScreen() {
//        val acceptButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_accept_button)
//
//        if (acceptButton.isClickable) {
//            acceptButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(R.string.license_agreement_title),
//                eq(R.string.license_agreement_accept_dialog_text),
//                eq(R.string.license_agreement_accept_dialog_positive_button_text),
//                buttonCaptor.capture(),
//                eq(R.string.license_agreement_accept_dialog_negative_button_text),
//                any())
//
//        val dialog = MaterialDialog.Builder(licenseAcceptanceFragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.POSITIVE)
//
//        val actualIntent = ShadowApplication.getInstance().nextStartedActivity
//        assertEquals(initialIntent, actualIntent)
//    }
//
//    @Test
//    fun onLicenseAcceptanceAcceptButtonClick_selectAccept_setsLicenseApproved() {
//        val acceptButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_accept_button)
//
//        if (acceptButton.isClickable) {
//            acceptButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(R.string.license_agreement_title),
//                eq(R.string.license_agreement_accept_dialog_text),
//                eq(R.string.license_agreement_accept_dialog_positive_button_text),
//                buttonCaptor.capture(),
//                eq(R.string.license_agreement_accept_dialog_negative_button_text),
//                any())
//
//        val dialog = MaterialDialog.Builder(licenseAcceptanceFragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(mockViewModel).setLicenseApproved()
//    }
//
//    @Test
//    fun onLicenseAcceptanceAcceptButtonClick_selectDecline_callsToAnalyticsManager() {
//        val acceptButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_accept_button)
//
//        if (acceptButton.isClickable) {
//            acceptButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(R.string.license_agreement_title),
//                eq(R.string.license_agreement_accept_dialog_text),
//                eq(R.string.license_agreement_accept_dialog_positive_button_text),
//                any(),
//                eq(R.string.license_agreement_accept_dialog_negative_button_text),
//                buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(licenseAcceptanceFragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LICENSE_AGREEMENT, Enums.Analytics.EventName.ACCEPT_LICENSE_AGREEMENT_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onLicenseAcceptanceDeclineButtonClick_callsToAnalyticsManager() {
//        val declineButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_decline_button)
//
//        if (declineButton.isClickable) {
//            declineButton.performClick()
//        }
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LICENSE_AGREEMENT, Enums.Analytics.EventName.DECLINE_BUTTON_PRESSED)
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LICENSE_AGREEMENT, Enums.Analytics.EventName.DECLINE_LICENSE_AGREEMENT_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun onLicenseAcceptanceDeclineButtonClick_selectDecline_callsToAnalyticsManager() {
//        val declineButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_decline_button)
//
//        if (declineButton.isClickable) {
//            declineButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_title)),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_text,
//                        licenseAcceptanceFragment.activity!!.getString(R.string.app_name))),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_positive_button_text)),
//                any(),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_negative_button_text)),
//                buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(licenseAcceptanceFragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LICENSE_AGREEMENT, Enums.Analytics.EventName.DECLINE_LICENSE_AGREEMENT_DIALOG_DECLINE_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onLicenseAcceptanceDeclineButtonClick_selectDecline_showsDeclineDialog() {
//        val declineButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_decline_button)
//
//        if (declineButton.isClickable) {
//            declineButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_title)),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_text,
//                        licenseAcceptanceFragment.activity!!.getString(R.string.app_name))),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_positive_button_text)),
//                any(),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_negative_button_text)),
//                buttonCaptor.capture())
//    }
//
//    @Test
//    fun onLicenseAcceptanceDeclineButtonClick_selectDecline_callsToFragmentListener() {
//        val declineButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_decline_button)
//
//        if (declineButton.isClickable) {
//            declineButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_title)),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_text,
//                        licenseAcceptanceFragment.activity!!.getString(R.string.app_name))),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_positive_button_text)),
//                any(),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_negative_button_text)),
//                buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(licenseAcceptanceFragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.NEGATIVE)
//
//        verify(mockFragmentListener).onAgreementDeclined()
//    }
//
//    @Test
//    fun onLicenseAcceptanceDeclineButtonClick_selectReview_callsToAnalyticsManager() {
//        val declineButton: Button = licenseAcceptanceFragment.view!!.findViewById(R.id.license_acceptance_decline_button)
//
//        if (declineButton.isClickable) {
//            declineButton.performClick()
//        }
//
//        val buttonCaptor = ArgumentCaptor.forClass(MaterialDialog.SingleButtonCallback::class.java)
//
//        verify(licenseAcceptanceFragment.dialogManager).showDialog(
//                eq(licenseAcceptanceFragment.activity!!),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_title)),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_text,
//                        licenseAcceptanceFragment.activity!!.getString(R.string.app_name))),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_positive_button_text)),
//                buttonCaptor.capture(),
//                eq(licenseAcceptanceFragment.activity!!.getString(R.string.license_agreement_decline_dialog_negative_button_text)),
//                any())
//
//        val dialog = MaterialDialog.Builder(licenseAcceptanceFragment.activity!!).build()
//        buttonCaptor.value.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LICENSE_AGREEMENT, Enums.Analytics.EventName.DECLINE_LICENSE_AGREEMENT_DIALOG_REVIEW_BUTTON_PRESSED)
//    }
//}