package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetGenericErrorBinding

class BottomSheetGenericError(
    val title: String,
    val message: String,
    val primaryActionTitle: String,
    val primaryActionCallback: () -> Unit
) : BaseBottomSheetDialogFragment() {

    private lateinit var titleText: TextView
    private lateinit var messageText: TextView
    private lateinit var primaryButton: TextView
    private lateinit var secondaryButton: TextView
    private lateinit var cancelIcon: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_generic_error, container, false)
        view?.let { bindViews(view) }

        titleText.text = title
        messageText.text = message

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetGenericErrorBinding.bind(view)
        titleText = binding.errorLayout.title
        messageText = binding.errorLayout.message
        primaryButton = binding.errorLayout.tryAgain
        secondaryButton = binding.errorLayout.cancel
        cancelIcon = binding.cancelIconInclude.closeIconView

        primaryButton.text = primaryActionTitle
        primaryButton.setOnClickListener {
            primaryActionCallback()
            dismiss()
        }

        secondaryButton.setOnClickListener { dismiss() }
        cancelIcon.setOnClickListener { dismiss() }
    }

}
