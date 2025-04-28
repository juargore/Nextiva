package com.nextiva.nextivaapp.android

import android.content.Intent
import android.view.View
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.viewmodels.DevicePhoneNumberViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowApplication
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
class DevicePhoneNumberActivityOnboardingTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var intentManager: IntentManager

    var viewModel: DevicePhoneNumberViewModel = mock()

    private lateinit var controller: ActivityController<DevicePhoneNumberActivity>
    private lateinit var activity: DevicePhoneNumberActivity

    @Before
    override fun setup() {
        hiltRule.inject()  // Initialize Hilt
        super.setup()

        val onboardingIntent = Intent()
        onboardingIntent.putExtra("PARAMS_SCREEN_TYPE", "com.nextiva.nextivaapp.android.ONBOARDING")

        controller = Robolectric.buildActivity(DevicePhoneNumberActivity::class.java, onboardingIntent)
        activity = controller.create().start().resume().visible().get()
    }

    @After
    fun cleanUp() {
        controller.pause().stop().destroy()
    }

    @Test
    fun newOnboardingIntent_returnsCorrectIntent() {
        val intent = DevicePhoneNumberActivity.newOnboardingIntent(activity)

        assertEquals("com.nextiva.nextivaapp.android.ONBOARDING", intent.getSerializableExtra("PARAMS_SCREEN_TYPE"))
        assertEquals(DevicePhoneNumberActivity::class.java.name, intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK, intent.flags)
    }

    @Test
    fun onCreate_showsCorrectViews() {
        assertEquals(View.GONE, activity.mAppBarLayout.visibility)
        assertEquals(View.VISIBLE, activity.mLogoImageView.visibility)
    }

    @Test
    fun onResume_callsToAnalyticsManagerToTrackScreenView() {
        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.ONBOARDING_THIS_PHONE_NUMBER)
    }

    @Test
    fun onPhoneNumberSaved_callsToIntentManagerToStartInitialIntent() {
        val intent = Intent()
        whenever(intentManager.getInitialIntent(activity)).thenReturn(intent)

        activity.onPhoneNumberSaved()

        verify(intentManager).getInitialIntent(activity)

        val actualIntent = ShadowApplication.getInstance().nextStartedActivity
        assertEquals(intent, actualIntent)
    }
}