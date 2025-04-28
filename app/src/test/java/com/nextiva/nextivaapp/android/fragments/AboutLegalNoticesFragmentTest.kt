package com.nextiva.nextivaapp.android.fragments
//
//import android.text.TextUtils
//import android.widget.TextView
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.util.StringUtil
//import org.junit.Assert.assertTrue
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//
//class AboutLegalNoticesFragmentTest : BaseRobolectricTest() {
//
//    private lateinit var fragment: AboutLegalNoticesFragment
//
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        fragment = AboutLegalNoticesFragment.newInstance()
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
//    fun onCreateView_setsLegalNoticesTextView() {
//        val legalNoticesTextView: TextView? = fragment.view?.findViewById(R.id.about_legal_notices_text_view)
//
//        assertTrue(TextUtils.equals(StringUtil.fromHtml((ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.about_legal_notice)),
//                legalNoticesTextView?.text))
//    }
//}