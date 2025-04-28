package com.nextiva.nextivaapp.android

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.viewmodels.DevicePhoneNumberViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import javax.inject.Inject

@HiltAndroidTest
class DevicePhoneNumberActivityPreferenceTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var controller: ActivityController<DevicePhoneNumberActivity>
    private lateinit var activity: DevicePhoneNumberActivity

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Mock
    lateinit var mockViewModel: DevicePhoneNumberViewModel

    @Before
    override fun setup() {
        super.setup()
        hiltRule.inject()
        MockitoAnnotations.openMocks(this)

        whenever(viewModelFactory.create(DevicePhoneNumberViewModel::class.java)).thenReturn(mockViewModel)
        whenever(viewModelFactory.create(eq(DevicePhoneNumberViewModel::class.java), any())).thenReturn(mockViewModel)

        val onboardingIntent = Intent()
        onboardingIntent.putExtra("PARAMS_SCREEN_TYPE", "com.nextiva.nextivaapp.android.PREFERENCE")

        controller = Robolectric.buildActivity(DevicePhoneNumberActivity::class.java, onboardingIntent)
        activity = controller.create().start().resume().visible().get()
    }

    @After
    fun cleanUp() {
        controller.pause().stop().destroy()
    }

    @Test
    fun newPreferenceIntent_returnsCorrectIntent() {
        val intent = DevicePhoneNumberActivity.newPreferenceIntent(activity)

        assertEquals("com.nextiva.nextivaapp.android.PREFERENCE", intent.getSerializableExtra("PARAMS_SCREEN_TYPE"))
        assertEquals(DevicePhoneNumberActivity::class.java.name, intent.component?.className)
    }

    @Test
    fun onCreate_showsCorrectViews() {
        assertEquals(View.VISIBLE, activity.mAppBarLayout.visibility)
        assertEquals(View.GONE, activity.mLogoImageView.visibility)
        assertEquals("This Mobile Phone Number", activity.mToolbar.title)
    }

    @Test
    fun onResume_callsToAnalyticsManagerToTrackScreenView() {
        //TODO Should this track a different analytic event?
        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.ONBOARDING_THIS_PHONE_NUMBER)
    }

    @Test
    fun onPhoneNumberSaved_finishesScreen() {
        activity.onPhoneNumberSaved()

        assertTrue(activity.isFinishing)
    }
}