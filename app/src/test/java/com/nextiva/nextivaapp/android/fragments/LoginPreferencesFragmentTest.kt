package com.nextiva.nextivaapp.android.fragments
//
//import androidx.lifecycle.ViewModelProvider
//import androidx.preference.Preference
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.SingleFragmentActivity
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.viewmodels.LoginPreferencesViewModel
//import com.nhaarman.mockito_kotlin.mock
//import com.nhaarman.mockito_kotlin.verify
//import com.nhaarman.mockito_kotlin.whenever
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import org.robolectric.Robolectric
//import org.robolectric.android.controller.ActivityController
//import javax.inject.Inject
//
//class LoginPreferencesFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var controller: ActivityController<SingleFragmentActivity>
//    private lateinit var activity: SingleFragmentActivity
//    private lateinit var fragment: LoginPreferencesFragment
//
//    private val mockViewModel: LoginPreferencesViewModel = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(LoginPreferencesViewModel::class.java)).thenReturn(mockViewModel)
//
//        fragment = LoginPreferencesFragment.newInstance()
//
//        controller = Robolectric.buildActivity(SingleFragmentActivity::class.java)
//        activity = controller.create().start().resume().visible().get()
//        activity.supportFragmentManager.beginTransaction().add(android.R.id.content, fragment).commit()
//    }
//
//    override fun after() {
//        super.after()
//        activity.finish()
//    }
//
//    @Test
//    fun troubleshootingPreference_verifyUi() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("troubleshooting")
//
//        assertEquals("Troubleshooting", preference?.title)
//    }
//
//    @Test
//    fun troubleshootingPreference_onClick_callsToViewModelToNavigateToScreen() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("troubleshooting")
//        preference?.performClick()
//
//        verify(mockViewModel).navigateToTroubleshootingScreen(activity)
//    }
//
//    @Test
//    fun troubleshootingPreference_onClick_callsToAnalyticsManager() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("troubleshooting")
//        preference?.performClick()
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LOGIN_PREFERENCES, Enums.Analytics.EventName.TROUBLESHOOTING_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun thisPhoneNumberPreference_verifyUi() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("this_phone_number")
//
//        assertEquals("This Mobile Phone Number", preference?.title)
//    }
//
//    @Test
//    fun thisPhoneNumberPreference_onClick_callsToViewModelToNavigateToScreen() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("this_phone_number")
//        preference?.performClick()
//
//        verify(mockViewModel).navigateToThisPhoneNumberScreen(activity)
//    }
//
//    @Test
//    fun thisPhoneNumberPreference_onClick_callsToAnalyticsManager() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("this_phone_number")
//        preference?.performClick()
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LOGIN_PREFERENCES, Enums.Analytics.EventName.THIS_PHONE_NUMBER_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun aboutPreference_verifyUi() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("about")
//
//        assertEquals("About", preference?.title)
//    }
//
//    @Test
//    fun aboutPreference_onClick_callsToViewModelToNavigateToScreen() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("about")
//        preference?.performClick()
//
//        verify(mockViewModel).navigateToAboutScreen(activity)
//    }
//
//    @Test
//    fun aboutPreference_onClick_callsToAnalyticsManager() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("about")
//        preference?.performClick()
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LOGIN_PREFERENCES, Enums.Analytics.EventName.ABOUT_LIST_ITEM_PRESSED)
//    }
//
//    @Test
//    fun helpPreference_verifyUi() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("help")
//
//        assertEquals("Help", preference?.title)
//    }
//
//    @Test
//    fun helpPreference_onClick_callsToViewModelToNavigateToScreen() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("help")
//        preference?.performClick()
//
//        verify(mockViewModel).navigateToHelpScreen(activity)
//    }
//
//    @Test
//    fun helpPreference_onClick_callsToAnalyticsManager() {
//        val preference: Preference? = fragment.preferenceScreen.findPreference("help")
//        preference?.performClick()
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.LOGIN_PREFERENCES, Enums.Analytics.EventName.HELP_LIST_ITEM_PRESSED)
//    }
//}