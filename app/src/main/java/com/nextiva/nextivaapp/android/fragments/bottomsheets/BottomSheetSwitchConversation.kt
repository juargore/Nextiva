package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetSwitchConversationBinding
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

class BottomSheetSwitchConversation(val onSelectedCallback: (Boolean) -> Unit) : BaseBottomSheetDialogFragment(tintedAppBar = true) {
    private lateinit var cancelIcon: FontTextView
    private lateinit var yesButton: TextView
    private lateinit var cancelButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_switch_conversation, container, false)
        view?.let { bindViews(view) }
        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetSwitchConversationBinding.bind(view)
        cancelIcon = binding.cancelIconInclude.closeIconFontTextView
        yesButton = binding.bottomSheetSwitchConversationPrimaryButton
        cancelButton = binding.bottomSheetSwitchConversationSecondaryButton

        cancelIcon.setOnClickListener {
            onSelectedCallback(false)
            dismiss()
        }

        cancelButton.setOnClickListener {
            onSelectedCallback(false)
            dismiss()
        }

        yesButton.setOnClickListener {
            onSelectedCallback(true)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onSelectedCallback(false)
    }
}