package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetDialogBinding

class BottomSheetDialog(val title: String, val message: String,
                        private val primaryActionText: String,
                        private val secondaryActionText: String,
                        private val primaryButtonColor: Int,
                        private val secondaryTextColor: Int,
                        private val secondaryButtonColor: Int,
                        val primaryActionCallback: () -> Unit): BaseBottomSheetDialogFragment() {

    private lateinit var titleText: TextView
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var messageText: TextView
    private lateinit var primaryButton: TextView
    private lateinit var secondaryButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false)
        view?.let { bindViews(view) }

        titleText.text = title
        messageText.text = message
        primaryButton.text = primaryActionText
        primaryButton.setBackgroundColor(primaryButtonColor)

        secondaryButton.text = secondaryActionText
        secondaryButton.setTextColor(secondaryTextColor)
        secondaryButton.backgroundTintList = ColorStateList.valueOf(secondaryButtonColor)

        setContentDescriptions()

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetDialogBinding.bind(view)
        titleText = binding.bottomSheetDialogTitle
        cancelIcon = binding.cancelIconInclude.closeIconView
        messageText = binding.bottomSheetDialogMessage
        primaryButton = binding.bottomSheetDialogPrimaryButton
        secondaryButton = binding.bottomSheetDialogSecondaryButton

        cancelIcon.setOnClickListener { dismiss() }
        secondaryButton.setOnClickListener { dismiss() }
        primaryButton.setOnClickListener {
            primaryActionCallback()
            dismiss()
        }
    }

    private fun setContentDescriptions() {
        primaryButton.contentDescription = primaryButton.text
        secondaryButton.contentDescription = secondaryButton.text
    }
}