package com.nextiva.nextivaapp.android

import android.view.MenuItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.fakes.RoboMenuItem
import javax.inject.Inject


@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
class LicenseAcceptanceActivityTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var controller: ActivityController<LicenseAcceptanceActivity>
    private lateinit var licenseAcceptanceActivity: LicenseAcceptanceActivity

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Before
    override fun setup() {
        hiltRule.inject()
        super.setup()
        controller = Robolectric.buildActivity(LicenseAcceptanceActivity::class.java)
        licenseAcceptanceActivity = controller.create().start().resume().visible().get()
    }

    @After
    fun cleanUp() {
        controller.pause().stop().destroy()
    }

    @Test
    fun startActivity_trackScreenViewAnalytics() {
        verify<AnalyticsManager>(analyticsManager).logScreenView(Enums.Analytics.ScreenName.LICENSE_AGREEMENT)
    }

    @Test
    fun onScreen_toolbarTitleDisplaysCorrectValue() {
        assertEquals("License Agreement", licenseAcceptanceActivity.mToolbar.title!!)
    }

    @Test
    fun onOptionsItemSelected_nullValue_returnsFalse() {
        val menuItem: MenuItem = RoboMenuItem(null)
        val wasOptionSelected = licenseAcceptanceActivity.onOptionsItemSelected(menuItem)

        assertFalse(wasOptionSelected)
    }

    @Test
    fun onAgreementDeclined_finishesActivity() {
        licenseAcceptanceActivity.onAgreementDeclined()

        assertTrue(licenseAcceptanceActivity.isFinishing)
    }
}