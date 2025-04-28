package com.nextiva.nextivaapp.android.fragments
//
//import android.text.SpannableStringBuilder
//import android.widget.HorizontalScrollView
//import android.widget.TextView
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.view.DialerPadView
//import com.nextiva.nextivaapp.android.viewmodels.DialerKeypadDialogViewModel
//import com.nhaarman.mockito_kotlin.mock
//import com.nhaarman.mockito_kotlin.verify
//import com.nhaarman.mockito_kotlin.whenever
//import org.junit.Assert.*
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment
//import javax.inject.Inject
//
//class DialerKeypadDialogFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var fragment: DialerKeypadDialogFragment
//
//    private val mockViewModel: DialerKeypadDialogViewModel = mock()
//    private val mockDialPadClickListener: DialerPadView.DialerPadClickListener = mock()
//
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(DialerKeypadDialogViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.widthMetric).thenReturn(120)
//        whenever(mockViewModel.heightMetric).thenReturn(240)
//
//        fragment = DialerKeypadDialogFragment.newInstance()
//        startFragment(fragment)
//        fragment.setDialerPadClickListener(mockDialPadClickListener)
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
//    fun onCreate_callsToViewModelForMetrics() {
//        verify(mockViewModel).widthMetric
//        verify(mockViewModel).heightMetric
//    }
//
//    @Test
//    fun onCreateView_setsUpViews() {
//        val inputTextView: TextView? = fragment.view?.findViewById(R.id.dialog_dialer_keypad_input_text_view)
//        val inputHorizontalScrollView: HorizontalScrollView? = fragment.view?.findViewById(R.id.dialog_dialer_keypad_input_scroll_view)
//
//        assertTrue(inputTextView!!.isSelected)
//        assertFalse(inputHorizontalScrollView!!.isHorizontalScrollBarEnabled)
//    }
//
//    @Test
//    fun onKeyPressed_callClickListener() {
//        fragment.onKeyPressed("0")
//        verify(mockDialPadClickListener).onKeyPressed("0")
//
//        fragment.onKeyPressed("1")
//        verify(mockDialPadClickListener).onKeyPressed("1")
//
//        fragment.onKeyPressed("2")
//        verify(mockDialPadClickListener).onKeyPressed("2")
//
//        fragment.onKeyPressed("3")
//        verify(mockDialPadClickListener).onKeyPressed("3")
//
//        val inputTextView: TextView? = fragment.view?.findViewById(R.id.dialog_dialer_keypad_input_text_view)
//
//        assertEquals("0123", (inputTextView?.text as SpannableStringBuilder).toString())
//    }
//
//}