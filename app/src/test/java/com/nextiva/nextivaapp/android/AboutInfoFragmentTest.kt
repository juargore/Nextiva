package com.nextiva.nextivaapp.android
//
//import android.widget.TextView
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.ABOUT_INFO
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.fragments.AboutInfoFragment
//import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nhaarman.mockito_kotlin.any
//import com.nhaarman.mockito_kotlin.eq
//import com.nhaarman.mockito_kotlin.verify
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import javax.inject.Inject
//
//class AboutInfoFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var intentManager: IntentManager
//
//    private lateinit var fragment: AboutInfoFragment
//
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        fragment = AboutInfoFragment.newInstance()
//        SupportFragmentTestUtil.startFragment(fragment)
//    }
//
//    override fun after() {
//        super.after()
//        fragment.onPause()
//        fragment.onStop()
//        fragment.onDestroy()
//    }
//
//    @Test
//    fun onCreateView_setsDescriptionTextView() {
//        val descriptionTextView: TextView? = fragment.view?.findViewById(R.id.about_info_description_text_view)
//
//        assertEquals((ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.about_head_summary,
//                (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.app_name)), descriptionTextView?.text)
//    }
//
//    @Test
//    fun onCreateView_setsVersionTextView() {
//        val versionTextView: TextView? = fragment.view?.findViewById(R.id.about_info_version_text_view)
//
//        assertEquals((ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.about_version_string, BuildConfig.VERSION_NAME),
//                versionTextView?.text)
//    }
//
//    @Test
//    fun showUrl_callsToIntentManager() {
//        val urlTextView: TextView? = fragment.view?.findViewById(R.id.about_info_url_text_view)
//
//        urlTextView?.performClick()
//
//        verify(intentManager).showUrl(any(), eq(ABOUT_INFO), eq("https://www.nextiva.com"))
//    }
//}