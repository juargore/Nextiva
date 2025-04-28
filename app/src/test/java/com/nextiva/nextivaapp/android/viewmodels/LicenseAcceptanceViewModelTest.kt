package com.nextiva.nextivaapp.android.viewmodels

import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
class LicenseAcceptanceViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var viewModel: LicenseAcceptanceViewModel

    @Before
    override fun setup() {
        super.setup()
        hiltRule.inject()

        viewModel = LicenseAcceptanceViewModel(ApplicationProvider.getApplicationContext(), sessionManager)
    }

    @Test
    fun setLicenseApproved_callsToSessionManager() {
        viewModel.setLicenseApproved()

        verify(sessionManager).isLicenseApproved = true
    }
}