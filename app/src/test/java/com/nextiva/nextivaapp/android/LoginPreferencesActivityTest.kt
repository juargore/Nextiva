package com.nextiva.nextivaapp.android

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.fragments.LoginPreferencesFragment
import com.nextiva.nextivaapp.android.viewmodels.LoginPreferencesViewModel
import com.squareup.okhttp.mockwebserver.MockWebServer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
class LoginPreferencesActivityTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mockWebServer: MockWebServer

    var viewModel: LoginPreferencesViewModel = mock()

    private lateinit var controller: ActivityController<LoginPreferencesActivity>
    private lateinit var activity: LoginPreferencesActivity

    @Before
    override fun setup() {
        hiltRule.inject()
        super.setup()

        // Setup ViewModel with mocked LiveData
        controller = Robolectric.buildActivity(LoginPreferencesActivity::class.java)
        activity = controller.create().start().resume().visible().get()
    }

    @Config(sdk = [Build.VERSION_CODES.O_MR1])
    @Test
    fun newIntent_returnsCorrectIntent() {
        val intent = LoginPreferencesActivity.newIntent(activity)
        assertEquals(LoginPreferencesActivity::class.java.name, intent.component?.className)
    }

    @Config(sdk = [Build.VERSION_CODES.O_MR1])
    @Test
    fun onCreate_setsUpViewsCorrectly() {
        assertEquals("Preferences", activity.mToolbar.title)
        assertThat(activity.supportFragmentManager.findFragmentById(R.id.login_preferences_fragment_container_layout), IsInstanceOf(LoginPreferencesFragment::class.java))
    }
}