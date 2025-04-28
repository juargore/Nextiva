package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetDialerKeypadBinding
import com.nextiva.nextivaapp.android.databinding.IncludeDialerPadBinding
import com.nextiva.nextivaapp.android.view.DialerPadView
import com.nextiva.nextivaapp.android.view.DialerPadView.DialerPadClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetKeypadFragment() : BaseBottomSheetDialogFragment(), DialerPadClickListener {

    private lateinit var mInputTextView: AppCompatTextView
    private lateinit var mDialerPadView: DialerPadView
    private var mDialerPadClickListener: DialerPadClickListener? = null
    private lateinit var mCancelIcon: RelativeLayout
    private lateinit var mInputHorizontalScrollView: HorizontalScrollView
    private lateinit var mHideTextView: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
    }

    override fun onKeyPressed(key: String) {
        mInputTextView.append(key)
        mDialerPadClickListener?.onKeyPressed(key)
        mInputHorizontalScrollView.post {
            mInputTextView.gravity =
                if (mInputTextView.getWidth() > mInputHorizontalScrollView.getWidth()) Gravity.START else Gravity.CENTER
            mInputHorizontalScrollView.fullScroll(View.FOCUS_RIGHT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_dialer_keypad, container, false)
        view?.let { bindViews(view) }

        mInputHorizontalScrollView.isHorizontalScrollBarEnabled = false
        mInputTextView.isSelected = true
        mDialerPadView.setVoicemailEnabled(false)
        mDialerPadView.setDialerPadClickListener(this)

        return view
    }

    @Override
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDialerPadClickListener = parentFragment as DialerPadClickListener?
        return super.onCreateDialog(savedInstanceState)
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetDialerKeypadBinding.bind(view)
        val mergeBinding = IncludeDialerPadBinding.bind(binding.root)

        mInputTextView = binding.bottomSheetKeypadInputTxt
        mDialerPadView = mergeBinding.dialerPadIncludeDialerPadView
        mCancelIcon = binding.cancelIconInclude.closeIconView
        mInputHorizontalScrollView = binding.bottomSheetDialerKeypadInputScrollView
        mHideTextView = binding.bottomSheetKeypadHideTxt

        mDialerPadView.setDialerPadClickListener(this)

        context?.getColor(R.color.connectGrey08)?.let { mInputTextView.setHintTextColor(it) }

        mCancelIcon.setOnClickListener {
            dismiss()
        }

        mHideTextView.setOnClickListener {
            dismiss()
        }

    }

    override fun onVoiceMailPressed() {
    }


}